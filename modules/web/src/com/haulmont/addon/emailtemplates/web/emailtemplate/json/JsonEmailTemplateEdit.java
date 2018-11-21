package com.haulmont.addon.emailtemplates.web.emailtemplate.json;

import com.haulmont.addon.emailtemplates.entity.JsonEmailTemplate;
import com.haulmont.addon.emailtemplates.web.emailtemplate.AbstractTemplateEditor;
import com.haulmont.addon.emailtemplates.web.gui.components.UnlayerTemplateEditor;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.chile.core.model.MetaPropertyPath;
import com.haulmont.chile.core.model.MetadataObject;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.PersistenceHelper;
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
import com.haulmont.cuba.gui.upload.FileUploadingAPI;
import com.haulmont.reports.app.service.ReportService;
import com.haulmont.reports.entity.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class JsonEmailTemplateEdit extends AbstractTemplateEditor<JsonEmailTemplate> {

    private static final Pattern SIMPLE_FIELD_PATTERN = Pattern.compile("\\$\\{([a-zA-Z0-9]*)[^}]*}");
    private static final Pattern ENTITY_FIELD_PATTERN = Pattern.compile("\\$\\{([a-zA-Z0-9]*)\\.([a-zA-Z0-9]*)[^}]*}");

    @Inject
    private UnlayerTemplateEditor templateEditor;

    @Inject
    protected FileUploadField fileUpload;

    @Inject
    protected FileUploadingAPI fileUploadingApi;

    @Inject
    private ExportDisplay exportDisplay;

    @Inject
    private ReportService reportService;

    private Report report;

    @Inject
    private Datasource<Report> reportDs;

    @Inject
    private Metadata metadata;

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

    @Inject
    protected CollectionDatasource.Sortable<ReportInputParameter, UUID> parametersDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        initParameters();
        initValuesFormats();
    }

    @Override
    protected void postInit() {
        super.postInit();

        if (PersistenceHelper.isNew(getItem())) {
            report = createReport();
        } else {
            report = getItem().getReport();
        }

        reportDs.setItem(report);

        templateEditor.setJson(getItem().getJsonBody());

        templateEditor.setListener(json -> {
            getItem().setJsonBody(json.toJson());
            String html = templateEditor.getHTML();
            getItem().setHtml(html);
            report.getDefaultTemplate().setContent(html.getBytes());
            createParameters();
        });

        subjectField.addValueChangeListener(e -> createParameters());

        fileUpload.addFileUploadSucceedListener(fileUploadSucceedEvent -> {
            UUID fileID = fileUpload.getFileId();
            File file = fileUploadingApi.getFile(fileID);
            try {
                byte[] bytes = FileUtils.readFileToByteArray(file);
                fileUploadingApi.deleteFile(fileID);
                getItem().setJsonBody(new String(bytes, StandardCharsets.UTF_8));
                templateEditor.setJson(getItem().getJsonBody());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        subjectField.addValueChangeListener(e -> updateReportOutputName());
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
            showNotification(formatMessage("newParametersCreated", StringUtils.join(newParamNames, ", ")), NotificationType.TRAY);
        }
    }

    private List<ReportInputParameter> createSimpleParameters() {
        List<ReportInputParameter> newParameters = new ArrayList<>();
        extractParams(newParameters, getItem().getJsonBody());
        extractParams(newParameters, getItem().getSubject());
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
        extractEntityParams(entityWithProperties, getItem().getJsonBody());
        extractEntityParams(entityWithProperties, getItem().getSubject());
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
        parameter.setName(entityAlias);
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

    private Report createReport() {
        Metadata metadata = AppBeans.get(Metadata.class);
        Report report = metadata.create(Report.class);

        ReportTemplate template = metadata.create(ReportTemplate.class);
        template.setCode(ReportTemplate.DEFAULT_TEMPLATE_CODE);
        template.setReportOutputType(ReportOutputType.HTML);
        template.setReport(report);
        template.setName("template.html");
        String html = templateEditor.getHTML();
        if (html != null) {
            template.setContent(html.getBytes());
        }
        report.setTemplates(Collections.singletonList(template));
        report.setDefaultTemplate(template);

        BandDefinition rootDefinition = metadata.create(BandDefinition.class);
        rootDefinition.setReport(report);
        rootDefinition.setName("Root");
        rootDefinition.setPosition(0);
        rootDefinition.setDataSets(new ArrayList<>());
        report.setBands(new HashSet<>());
        report.getBands().add(rootDefinition);

        DataSet dataSet = metadata.create(DataSet.class);
        dataSet.setBandDefinition(rootDefinition);
        dataSet.setType(DataSetType.GROOVY);
        dataSet.setName("Root");
        rootDefinition.getDataSets().add(dataSet);

        rootDefinition.setReport(report);


        report.setName(getItem().getName());
        report.setReportType(ReportType.SIMPLE);
        report.setIsTmp(true);

        report.setXml(AppBeans.get(ReportService.class).convertToString(report));
        return report;
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
                ReportInputParameter parameter = (ReportInputParameter) target.getSingleSelected();
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
                if (target != null) {
                    ReportInputParameter item = (ReportInputParameter) target.getSingleSelected();
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
                ReportInputParameter parameter = (ReportInputParameter) target.getSingleSelected();
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
                if (target != null) {
                    ReportInputParameter item = (ReportInputParameter) target.getSingleSelected();
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

    private void refreshTemplateParameters() {
        templateEditor.setParameters(parametersDs.getItems().stream()
                .collect(Collectors.toMap(ReportInputParameter::getName, ReportInputParameter::getAlias)));
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

    @Override
    protected boolean preCommit() {
        getItem().setJsonBody(templateEditor.getJson().toString());
        String html = templateEditor.getHTML();
        updateReportOutputName();
        if (html != null) {
            getItem().setHtml(html);
            report.getDefaultTemplate().setContent(html.getBytes());
        }
        report.setName(getItem().getName());
        getItem().setReportXml(reportService.convertToString(report));
        return super.preCommit();
    }

    public void updateReportOutputName() {
        DataSet dataSet = report.getRootBandDefinition().getDataSets().stream()
                .filter(e -> "Root".equals(e.getName()))
                .findFirst()
                .orElse(null);
        if (dataSet == null) {
            dataSet = metadata.create(DataSet.class);
            dataSet.setBandDefinition(report.getRootBandDefinition());
            dataSet.setType(DataSetType.GROOVY);
            dataSet.setName("Root");
            report.getRootBandDefinition().getDataSets().add(dataSet);
        }
        String subject = getItem().getSubject();
        if (StringUtils.isNotBlank(subject)) {
            subject = subject.replaceAll("\\$\\{([a-zA-Z0-9]*)}", "\"+params[\"$1\"]+\"");
            subject = subject.replaceAll("\\$\\{([a-zA-Z0-9]*).([a-zA-Z0-9.]*)}", "\"+params[\"$1\"].$2+\"");
            dataSet.setText("return [[\"__REPORT_FILE_NAME\": \"" + subject + "\"]]");
        } else {
            dataSet.setText("");
        }
    }

    public void exportJson() {
        String name = getItem().getName() != null ? getItem().getName() : "template";
        exportDisplay.show(new ByteArrayDataProvider(templateEditor.getJson().toString().getBytes()), name + ".json");

    }

    public void exportHtml() {
        openWindow("emailtemplates$htmlSourceCode", WindowManager.OpenType.DIALOG, ParamsMap.of("html", templateEditor.getHTML()));
    }

    public void viewHtml() {
        String name = getItem().getName() != null ? getItem().getName() : "template";
        exportDisplay.show(new ByteArrayDataProvider(templateEditor.getHTML().getBytes()), name + ".html");
    }


    public void exportReport() {
        getItem().setReportXml(reportService.convertToString(report));
        AbstractEditor reportEditor = openEditor(getItem().getReport(), WindowManager.OpenType.NEW_TAB);
        reportEditor.getDsContext().get("reportDs").setItem(getItem().getReport());
    }
}