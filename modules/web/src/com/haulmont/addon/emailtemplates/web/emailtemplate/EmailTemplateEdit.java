package com.haulmont.addon.emailtemplates.web.emailtemplate;

import com.haulmont.addon.emailtemplates.dto.ReportWithParams;
import com.haulmont.addon.emailtemplates.entity.EmailTemplate;
import com.haulmont.addon.emailtemplates.web.frames.TemplateParametersFrame;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.DataSupplier;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.reports.app.service.ReportService;
import com.haulmont.reports.entity.Report;
import com.haulmont.reports.entity.ReportInputParameter;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EmailTemplateEdit extends AbstractEditor<EmailTemplate> {

    @Named("fieldGroup.caption")
    private TextField captionField;
    @Named("fieldGroup.emailBody")
    private LookupPickerField emailBodyField;
    @Inject
    private ScrollBoxLayout propertiesScrollBox;
    @Inject
    private CollectionDatasource<Report, UUID> attachmentsDs;
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected ReportService reportService;
    @Inject
    private DataSupplier dataSupplier;

    protected TemplateParametersFrame parametersFrame;
    protected VBoxLayout frameContainer;

    @Override
    public void init(Map<String, Object> params) {
        frameContainer = componentsFactory.createComponent(VBoxLayout.class);
        propertiesScrollBox.add(frameContainer);
    }

    @Override
    protected void postInit() {
        parametersFrame = (TemplateParametersFrame) openFrame(frameContainer,"templateParametersFrame",
                ParamsMap.of(TemplateParametersFrame.TEMPLATE, getItem(), TemplateParametersFrame.IS_DEFAULT_PARAM_VALUES, true));

        emailBodyField.addValueChangeListener(e -> {
            Report report = (Report) e.getValue();
            if (report != null) {
                captionField.setValue(report.getName());
            }
            parametersFrame.updateBodyReport(report);
            parametersFrame.createComponents();
        });

        attachmentsDs.addCollectionChangeListener(e -> {
            if (e.getOperation().equals(CollectionDatasource.Operation.ADD)) {
                parametersFrame.addAttachmentReports(e.getItems());
                parametersFrame.createComponents();
            } else if (e.getOperation().equals(CollectionDatasource.Operation.REMOVE)) {
                parametersFrame.removeAttachmentReports(e.getItems());
                parametersFrame.createComponents();
            }
        });
    }

    @Override
    protected boolean preCommit() {
        List<ReportWithParams> reportData = new ArrayList<>();
        for (ReportWithParams reportWithParams: parametersFrame.collectParameters()) {
            Report report = reportWithParams.getReport();
            ReportWithParams newReportWithParams = reportData.stream().filter(
                    e -> e.getReport().equals(report)).findFirst().orElse(null);
            if (newReportWithParams == null) {
                reportData.add(reportWithParams);
            }
        }
        for (ReportWithParams reportWithParams: reportData) {
            Report report = reportWithParams.getReport();
            Map<String, Object> params = reportWithParams.getParams();
            for (ReportInputParameter inputParameter: report.getInputParameters()) {
                Object value = params.get(inputParameter.getAlias());
                String defaultValue = reportService.convertToString(inputParameter.getClass(), value);
                inputParameter.setDefaultValue(defaultValue);
            }
            String xml = reportService.convertToString(report);
            report.setXml(xml);
            dataSupplier.commit(new CommitContext(report));
        }
        return super.preCommit();
    }

}