package com.haulmont.addon.emailtemplates.web.emailtemplate;

import com.haulmont.addon.emailtemplates.entity.JsonEmailTemplate;
import com.haulmont.addon.emailtemplates.web.gui.components.UnlayerTemplateEditor;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.chile.core.model.MetaPropertyPath;
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

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class JsonEmailTemplateEdit extends AbstractEditor<JsonEmailTemplate> {

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
            report = getItem().getEmailBodyReport();
        }

        reportDs.setItem(report);

        templateEditor.setJson(getItem().getJsonBody());

        templateEditor.setListener(json -> {
            getItem().setJsonBody(json.toJson());
            String html = templateEditor.getHTML();
            getItem().setHtml(html);
            report.getDefaultTemplate().setContent(html.getBytes());
        });

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
    }

    private Report createReport() {
        Metadata metadata = AppBeans.get(Metadata.class);
        Report report = metadata.create(Report.class);

        ReportTemplate template = metadata.create(ReportTemplate.class);
        template.setCode(ReportTemplate.DEFAULT_TEMPLATE_CODE);
        template.setReportOutputType(ReportOutputType.HTML);
        template.setReport(report);
        template.setName("templateEditor.html");
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
        report.setBands(new HashSet<>());
        report.getBands().add(rootDefinition);

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
                        for (ReportInputParameter _param : inputParameters) {
                            if (_param.getPosition() == index - 1) {
                                previousParameter = _param;
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
                        for (ReportInputParameter _param : inputParameters) {
                            if (_param.getPosition() == index + 1) {
                                nextParameter = _param;
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
        });
    }

    protected void orderParameters() {
        if (report.getInputParameters() == null) {
            report.setInputParameters(new ArrayList<ReportInputParameter>());
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
        if (html != null) {
            getItem().setHtml(html);
            report.getDefaultTemplate().setContent(html.getBytes());
        }
        report.setName(getItem().getName());
        getItem().setReportXml(reportService.convertToString(report));
        return true;
    }

    public void exportJson() {
        String name = getItem().getName() != null ? getItem().getName() : "templateEditor";
        exportDisplay.show(new ByteArrayDataProvider(templateEditor.getJson().toString().getBytes()), name + ".json");

    }

    public void exportHtml() {
        openWindow("emailtemplates$htmlSourceCode", WindowManager.OpenType.DIALOG, ParamsMap.of("html", templateEditor.getHTML()));
    }

    public void viewHtml() {
        String name = getItem().getName() != null ? getItem().getName() : "templateEditor";
        exportDisplay.show(new ByteArrayDataProvider(templateEditor.getHTML().getBytes()), name + ".html");
    }


}