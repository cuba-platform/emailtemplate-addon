package com.haulmont.addon.emailtemplates.web.emailtemplate;

import com.haulmont.addon.emailtemplates.entity.ParameterValue;
import com.haulmont.addon.emailtemplates.entity.ReportEmailTemplate;
import com.haulmont.addon.emailtemplates.entity.TemplateParameters;
import com.haulmont.addon.emailtemplates.web.frames.EmailTemplateParametersFrame;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.reports.app.service.ReportService;
import com.haulmont.reports.entity.Report;
import com.haulmont.reports.entity.ReportOutputType;
import com.haulmont.reports.gui.report.run.ParameterClassResolver;
import org.apache.commons.lang.BooleanUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

public class ReportEmailTemplateEdit extends AbstractTemplateEditor<ReportEmailTemplate> {

    @Named("defaultGroup.subject")
    private TextField subjectField;
    @Named("reportGroup.emailBody")
    private LookupPickerField emailBodyField;
    @Named("useReportSubjectGroup.useReportSubject")
    private CheckBox useReportSubject;

    @Inject
    private ScrollBoxLayout propertiesScrollBox;

    @Inject
    private CollectionDatasource<Report, UUID> attachmentsDs;
    @Inject
    private CollectionDatasource<TemplateParameters, UUID> parametersDs;
    @Inject
    private CollectionDatasource<ParameterValue, UUID> parameterValuesDs;

    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    private Metadata metadata;

    @Inject
    protected ReportService reportService;
    @Inject
    protected ParameterClassResolver classResolver;

    @Inject
    protected VBoxLayout defaultValuesBox;

    protected EmailTemplateParametersFrame parametersFrame;


    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
    }

    @Override
    protected void postInit() {
        ReportEmailTemplate emailTemplate = getItem();

        parametersFrame = (EmailTemplateParametersFrame) openFrame(defaultValuesBox, "emailtemplates$parametersFrame",
                ParamsMap.of(EmailTemplateParametersFrame.TEMPLATE, emailTemplate,
                        EmailTemplateParametersFrame.IS_DEFAULT_PARAM_VALUES, true,
                        EmailTemplateParametersFrame.HIDE_REPORT_CAPTION, true));

        parametersFrame.setReport(getItem().getEmailBodyReport());
        parametersFrame.createComponents();

        emailBodyField.addValueChangeListener(e -> {
            Report report = getDsContext().getDataSupplier().reload((Report) e.getValue(), "emailTemplate-view");
            if (ReportOutputType.HTML==report.getDefaultTemplate().getReportOutputType()) {
                getItem().setEmailBody(report);
                parametersFrame.setReport(getItem().getEmailBodyReport());
                parametersFrame.createComponents();
            }
            else {
                getItem().setEmailBody(null);
                parametersFrame.setReport(getItem().getEmailBodyReport());
                parametersFrame.clearComponents();
                showNotification(getMessage("notification.reportIsNotHtml"),NotificationType.ERROR);
            }
        });

        useReportSubject.addValueChangeListener(e -> {
            setSubjectVisibilty();
        });

        setSubjectVisibilty();
    }

    public void setSubjectVisibilty() {
        subjectField.setVisible(BooleanUtils.isNotTrue(getItem().getUseReportSubject()));
    }

    @Override
    protected boolean preCommit() {
        return super.preCommit();
    }

    public void runReport() {
    }
}