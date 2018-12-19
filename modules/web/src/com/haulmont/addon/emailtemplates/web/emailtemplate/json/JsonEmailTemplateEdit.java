package com.haulmont.addon.emailtemplates.web.emailtemplate.json;

import com.haulmont.addon.emailtemplates.entity.JsonEmailTemplate;
import com.haulmont.addon.emailtemplates.service.TemplateConverterService;
import com.haulmont.addon.emailtemplates.web.emailtemplate.AbstractTemplateEditor;
import com.haulmont.addon.grapesjs.web.gui.components.GrapesJsHtmlEditor;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.chile.core.model.MetaPropertyPath;
import com.haulmont.chile.core.model.MetadataObject;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.Screens;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.impl.DatasourceImplementation;
import com.haulmont.cuba.gui.export.ByteArrayDataProvider;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.model.DataContext;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.gui.upload.FileUploadingAPI;
import com.haulmont.reports.entity.ParameterType;
import com.haulmont.reports.entity.Report;
import com.haulmont.reports.entity.ReportInputParameter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@UiController("emailtemplates$JsonEmailTemplate.edit")
@UiDescriptor("email-template-json-edit.xml")
@EditedEntityContainer("emailTemplateDs")
@LoadDataBeforeShow
public class JsonEmailTemplateEdit extends AbstractTemplateEditor<JsonEmailTemplate> {

    private static final Pattern SIMPLE_FIELD_PATTERN = Pattern.compile("\\$\\{([a-zA-Z0-9]+)[^}]*}");
    private static final Pattern ENTITY_FIELD_PATTERN = Pattern.compile("\\$\\{([a-zA-Z0-9]+)\\.([a-zA-Z0-9]*)[^}]*}");

    @Inject
    private GrapesJsHtmlEditor templateEditor;

    @Inject
    protected FileUploadField fileUpload;

    @Inject
    protected FileUploadingAPI fileUploadingApi;

    @Inject
    private ExportDisplay exportDisplay;

    @Inject
    private TemplateConverterService templateConverterService;

    private Report report;

    @Inject
    private Datasource<Report> reportDs;

    @Inject
    private Metadata metadata;

    @Inject
    private Notifications notifications;

    @Inject
    private MessageBundle messageBundle;

    @Inject
    private Screens screens;

    @Inject
    private ScreenBuilders screenBuilders;

    @Named("parametersFrame.inputParametersTable")
    protected Table parametersTable;

    @Named("formatsFrame.valuesFormatsTable")
    protected Table formatsTable;

    @Named("parametersFrame.up")
    protected Button paramUpButton;

    @Named("parametersFrame.down")
    protected Button paramDownButton;

    @Named("defaultGroup.subject")
    private TextField subjectField;

    @Named("parametersFrame.validationScriptGroupBox")
    protected GroupBoxLayout validationScriptGroupBox;

    @Inject
    protected CollectionDatasource.Sortable<ReportInputParameter, UUID> parametersDs;

    @Subscribe
    protected void onInit(InitEvent event) {
        initParameters();
        initValuesFormats();
    }

    @Subscribe
    protected void onAfterInit(AfterInitEvent event) {
        report = templateConverterService.convertToReport(getEditedEntity());

        reportDs.setItem(report);

        templateEditor.setValue(getEditedEntity().getHtml());

        templateEditor.addValueChangeListener(e -> {
            String value = (String) e.getValue();
            getEditedEntity().setHtml(value);
            createParameters();
        });

        subjectField.addValueChangeListener(e -> createParameters());

        fileUpload.addFileUploadSucceedListener(fileUploadSucceedEvent -> {
            UUID fileID = fileUpload.getFileId();
            File file = fileUploadingApi.getFile(fileID);
            try {
                byte[] bytes = FileUtils.readFileToByteArray(file);
                fileUploadingApi.deleteFile(fileID);
                getEditedEntity().setHtml(new String(bytes, StandardCharsets.UTF_8));
                templateEditor.setValue(getEditedEntity().getHtml());
                createParameters();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        initValidationScriptGroupBoxCaption();
    }

    @Subscribe(target = Target.DATA_CONTEXT)
    protected void onPreCommit1(DataContext.PreCommitEvent event) {
        initTemplateReport();
    }

    private void createParameters() {
        List<ReportInputParameter> newParameters = new ArrayList<>();
        newParameters.addAll(createEntityParameters());
        newParameters.addAll(createSimpleParameters());
        if (!newParameters.isEmpty()) {
            report.getInputParameters().addAll(newParameters);
            parametersDs.refresh();

            List<String> newParamNames = newParameters.stream()
                    .map(ReportInputParameter::getName)
                    .collect(Collectors.toList());
            notifications.create(Notifications.NotificationType.TRAY)
                    .withDescription(messageBundle.formatMessage("newParametersCreated", StringUtils.join(newParamNames, ", ")))
                    .show();
        }
    }

    private List<ReportInputParameter> createSimpleParameters() {
        List<ReportInputParameter> newParameters = new ArrayList<>();
        extractParams(newParameters, getEditedEntity().getHtml());
        extractParams(newParameters, getEditedEntity().getSubject());
        return newParameters;
    }

    private void extractParams(List<ReportInputParameter> newParameters, String parameterSource) {
        if (StringUtils.isNotBlank(parameterSource)) {
            Matcher m = SIMPLE_FIELD_PATTERN.matcher(parameterSource);
            while (m.find()) {
                String field = m.group(1);
                ReportInputParameter parameter = getParameter(field);
                if (parameter == null) {
                    parameter = initNewParameter(field);
                    parameter.setType(ParameterType.TEXT);
                    newParameters.add(parameter);
                }
            }
        }
    }

    private List<ReportInputParameter> createEntityParameters() {
        List<ReportInputParameter> newParameters = new ArrayList<>();
        Map<String, List<String>> entityWithProperties = new HashMap<>();
        extractEntityParams(entityWithProperties, getEditedEntity().getHtml());
        extractEntityParams(entityWithProperties, getEditedEntity().getSubject());
        for (String entityAlias : entityWithProperties.keySet()) {
            ReportInputParameter parameter = getParameter(entityAlias);
            if (parameter == null) {
                parameter = initNewParameter(entityAlias);
                parameter.setType(ParameterType.ENTITY);
                newParameters.add(parameter);
            }
            if (parameter.getEntityMetaClass() == null) {
                List<String> fields = entityWithProperties.get(entityAlias);
                MetaClass metaClass = findMetaclassForFields(fields);
                if (metaClass != null) {
                    parameter.setEntityMetaClass(metaClass.getName());
                }
            }
        }
        return newParameters;
    }

    private void extractEntityParams(Map<String, List<String>> entityWithProperties, String parameterSource) {
        if (StringUtils.isNotBlank(parameterSource)) {
            Matcher m = ENTITY_FIELD_PATTERN.matcher(parameterSource);
            while (m.find()) {
                String entityAlias = m.group(1);
                String field = m.group(2);
                if (!entityWithProperties.containsKey(entityAlias)) {
                    entityWithProperties.put(entityAlias, new ArrayList<>());
                }
                entityWithProperties.get(entityAlias).add(field);
            }
        }
    }

    private ReportInputParameter initNewParameter(String entityAlias) {
        ReportInputParameter parameter;
        parameter = metadata.create(ReportInputParameter.class);
        parameter.setName(splitCamelCase(entityAlias));
        parameter.setAlias(entityAlias);
        parameter.setReport(report);
        parameter.setPosition(0);
        return parameter;
    }

    private MetaClass findMetaclassForFields(List<String> fields) {
        for (MetaClass metaClass : metadata.getSession().getClasses()) {
            List<String> parameters = metaClass.getProperties().stream()
                    .map(MetadataObject::getName)
                    .collect(Collectors.toList());
            if (parameters.containsAll(fields)) {
                return metaClass;
            }
        }
        return null;
    }

    protected String splitCamelCase(String s) {
        return StringUtils.capitalize(StringUtils.join(
                StringUtils.splitByCharacterTypeCamelCase(s), ' '
        ));
    }

    private boolean parameterExists(String alias) {
        return report.getInputParameters().stream()
                .anyMatch(p -> alias.equals(p.getAlias()));
    }

    private ReportInputParameter getParameter(String alias) {
        return report.getInputParameters().stream()
                .filter(p -> alias.equals(p.getAlias()))
                .findFirst()
                .orElse(null);
    }

    protected void initValuesFormats() {
        CreateAction formatCreateAction = CreateAction.create(formatsTable, WindowManager.OpenType.DIALOG);
        formatCreateAction.setInitialValuesSupplier(() ->
                ParamsMap.of("report", report)
        );
        formatsTable.addAction(formatCreateAction);

        formatsTable.addAction(new RemoveAction(formatsTable, false));
        formatsTable.addAction(new EditAction(formatsTable, WindowManager.OpenType.DIALOG));
    }

    protected void initParameters() {
        parametersTable.addAction(
                new CreateAction(parametersTable, WindowManager.OpenType.DIALOG) {
                    @Override
                    public Map<String, Object> getInitialValues() {
                        Map<String, Object> params = new HashMap<>();
                        params.put("position", parametersDs.getItemIds().size());
                        params.put("report", report);
                        return params;
                    }

                    @Override
                    public void actionPerform(Component component) {
                        orderParameters();
                        super.actionPerform(component);
                    }
                }
        );

        parametersTable.addAction(new RemoveAction(parametersTable, false) {
            @Override
            protected void afterRemove(Set selected) {
                super.afterRemove(selected);
                orderParameters();
            }
        });
        parametersTable.addAction(new EditAction(parametersTable, WindowManager.OpenType.DIALOG));

        paramUpButton.setAction(new BaseAction("generalFrame.up") {
            @Override
            public void actionPerform(Component component) {
                ReportInputParameter parameter = (ReportInputParameter) parametersTable.getSingleSelected();
                if (parameter != null) {
                    List<ReportInputParameter> inputParameters = report.getInputParameters();
                    int index = parameter.getPosition();
                    if (index > 0) {
                        ReportInputParameter previousParameter = null;
                        for (ReportInputParameter inputParameter : inputParameters) {
                            if (inputParameter.getPosition() == index - 1) {
                                previousParameter = inputParameter;
                                break;
                            }
                        }
                        if (previousParameter != null) {
                            parameter.setPosition(previousParameter.getPosition());
                            previousParameter.setPosition(index);

                            sortParametersByPosition();
                        }
                    }
                }
            }

            @Override
            protected boolean isApplicable() {
                if (getWindow().getFrame() != null) {
                    ReportInputParameter item = (ReportInputParameter) parametersTable.getSingleSelected();
                    if (item != null && parametersDs.getItem() == item) {
                        return item.getPosition() > 0;
                    }
                }

                return false;
            }
        });

        paramDownButton.setAction(new BaseAction("generalFrame.down") {
            @Override
            public void actionPerform(Component component) {
                ReportInputParameter parameter = (ReportInputParameter) parametersTable.getSingleSelected();
                if (parameter != null) {
                    List<ReportInputParameter> inputParameters = report.getInputParameters();
                    int index = parameter.getPosition();
                    if (index < parametersDs.getItemIds().size() - 1) {
                        ReportInputParameter nextParameter = null;
                        for (ReportInputParameter inputParameter : inputParameters) {
                            if (inputParameter.getPosition() == index + 1) {
                                nextParameter = inputParameter;
                                break;
                            }
                        }
                        if (nextParameter != null) {
                            parameter.setPosition(nextParameter.getPosition());
                            nextParameter.setPosition(index);

                            sortParametersByPosition();
                        }
                    }
                }
            }

            @Override
            protected boolean isApplicable() {
                if (getWindow().getFrame() != null) {
                    ReportInputParameter item = (ReportInputParameter) parametersTable.getSingleSelected();
                    if (item != null && parametersDs.getItem() == item) {
                        return item.getPosition() < parametersDs.size() - 1;
                    }
                }

                return false;
            }
        });

        parametersTable.addAction(paramUpButton.getAction());
        parametersTable.addAction(paramDownButton.getAction());

        parametersDs.addItemPropertyChangeListener(e -> {
            if ("position".equals(e.getProperty())) {
                ((DatasourceImplementation) parametersDs).modified(e.getItem());
            }
            refreshTemplateParameters();
        });

        parametersDs.addCollectionChangeListener(e -> {
            refreshTemplateParameters();
        });
    }

    protected void initValidationScriptGroupBoxCaption() {
        setValidationScriptGroupBoxCaption(report.getValidationOn());

        reportDs.addItemPropertyChangeListener(e -> {
            boolean validationOnChanged = e.getProperty().equalsIgnoreCase("validationOn");

            if (validationOnChanged) {
                setValidationScriptGroupBoxCaption(e.getItem().getValidationOn());
            }
        });
    }

    protected void setValidationScriptGroupBoxCaption(Boolean onOffFlag) {
        if (BooleanUtils.isTrue(onOffFlag)) {
            validationScriptGroupBox.setCaption(messageBundle.getMessage("report.validationScriptOn"));
        } else {
            validationScriptGroupBox.setCaption(messageBundle.getMessage("report.validationScriptOff"));
        }
    }

    private void refreshTemplateParameters() {
//        templateEditor.setParameters(parametersDs.getItems().stream()
//                .collect(Collectors.toMap(ReportInputParameter::getName, ReportInputParameter::getAlias)));
    }

    protected void orderParameters() {
        if (report.getInputParameters() == null) {
            report.setInputParameters(new ArrayList<>());
        }

        for (int i = 0; i < report.getInputParameters().size(); i++) {
            report.getInputParameters().get(i).setPosition(i);
        }
    }

    protected void sortParametersByPosition() {
        MetaClass metaClass = metadata.getClassNN(ReportInputParameter.class);
        MetaPropertyPath mpp = new MetaPropertyPath(metaClass, metaClass.getProperty("position"));

        CollectionDatasource.Sortable.SortInfo<MetaPropertyPath> sortInfo = new CollectionDatasource.Sortable.SortInfo<>();
        sortInfo.setOrder(CollectionDatasource.Sortable.Order.ASC);
        sortInfo.setPropertyPath(mpp);

        parametersDs.sort(new CollectionDatasource.Sortable.SortInfo[]{sortInfo});
    }

    private void initTemplateReport() {
        getEditedEntity().setHtml(templateEditor.getValue());
        getEditedEntity().setReport(report);
        report = templateConverterService.convertToReport(getEditedEntity());
        reportDs.setItem(report);
        getEditedEntity().setReport(report);
    }

    public void exportHtml() {
        screens.create("emailtemplates$htmlSourceCode",
                        OpenMode.DIALOG, new
                        MapScreenOptions(ParamsMap.of("html", getEditedEntity().getHtml())))
                .show();
    }

    public void viewHtml() {
        String name = getEditedEntity().getName() != null ? getEditedEntity().getName() : "template";
        String html = getEditedEntity().getHtml() != null ? getEditedEntity().getHtml() : "";
        exportDisplay.show(new ByteArrayDataProvider(html.getBytes()), name + ".html");
    }


    public void exportReport() {
        getEditedEntity().setReport(report);
        Report report = templateConverterService.convertToReport(getEditedEntity());
        //todo
        AbstractEditor reportEditor = openEditor(report, WindowManager.OpenType.NEW_TAB);
        report = templateConverterService.convertToReport(getEditedEntity());
        reportEditor.getDsContext().get("reportDs").setItem(report);
    }
}