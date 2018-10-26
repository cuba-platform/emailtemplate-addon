package com.haulmont.addon.emailtemplates.web.emailtemplate.report;

import com.haulmont.addon.emailtemplates.entity.ReportEmailTemplate;
import com.haulmont.addon.emailtemplates.entity.TemplateReport;
import com.haulmont.addon.emailtemplates.web.emailtemplate.AbstractTemplateEditor;
import com.haulmont.addon.emailtemplates.web.frames.EmailTemplateParametersFrame;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.components.CheckBox;
import com.haulmont.cuba.gui.components.LookupPickerField;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.components.VBoxLayout;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.reports.entity.Report;
import com.haulmont.reports.entity.ReportOutputType;
import org.apache.commons.lang.BooleanUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Map;

public class ReportEmailTemplateEdit extends AbstractTemplateEditor<ReportEmailTemplate> {

    @Named("defaultGroup.subject")
    private TextField subjectField;

    @Inject
    private LookupPickerField emailBody;

    @Named("useReportSubjectGroup.useReportSubject")
    private CheckBox useReportSubject;

    @Inject
    private Metadata metadata;

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
                ParamsMap.of(EmailTemplateParametersFrame.IS_DEFAULT_PARAM_VALUES, true,
                        EmailTemplateParametersFrame.HIDE_REPORT_CAPTION, true));

        parametersFrame.setTemplateReport(getItem().getEmailBodyReport());
        if (getItem().getEmailBodyReport() != null) {
            parametersFrame.createComponents();
        } else {
            parametersFrame.clearComponents();
        }

        emailBody.setOptionsDatasource((CollectionDatasource) getDsContext().get("emailBodiesDs"));
        emailBody.addLookupAction();
        emailBody.addClearAction();
        emailBody.addOpenAction();

        emailBody.setValue(getItem().getReport());

        emailBody.addValueChangeListener(e -> {
            Report value = (Report) e.getValue();
            if (value != null) {
                Report report = getDsContext().getDataSupplier().reload(value, "emailTemplate-view");
                if (ReportOutputType.HTML == report.getDefaultTemplate().getReportOutputType()) {
                    removeTemplateReportIfRequired(report);
                    TemplateReport templateReport = metadata.create(TemplateReport.class);
                    templateReport.setParameterValues(new ArrayList<>());
                    templateReport.setReport(report);
                    getItem().setEmailBodyReport(templateReport);
                    parametersFrame.setTemplateReport(getItem().getEmailBodyReport());
                    parametersFrame.createComponents();
                } else {
                    getItem().setEmailBodyReport(null);
                    parametersFrame.setTemplateReport(getItem().getEmailBodyReport());
                    parametersFrame.clearComponents();
                    showNotification(getMessage("notification.reportIsNotHtml"), NotificationType.ERROR);
                }
            } else {
                removeTemplateReportIfRequired(value);
                getItem().setEmailBodyReport(null);
                parametersFrame.setTemplateReport(getItem().getEmailBodyReport());
                parametersFrame.clearComponents();
            }
        });

        useReportSubject.addValueChangeListener(e -> {
            setSubjectVisibilty();
        });

        setSubjectVisibilty();
    }

    private void removeTemplateReportIfRequired(Report report) {
        if (!PersistenceHelper.isNew(getItem())) {
            TemplateReport templateReport = getItem().getEmailBodyReport();
            if (templateReport != null) {
                if (!templateReport.getReport().equals(report)) {
                    getItem().setEmailBodyReport(null);
                    getDsContext().getDataSupplier().remove(templateReport);
                }
            }
        }
    }

    public void setSubjectVisibilty() {
        subjectField.setVisible(BooleanUtils.isNotTrue(getItem().getUseReportSubject()));
    }

    public void runReport() {
    }
}