package com.haulmont.addon.yargemailtemplateaddon.web.layoutemailtemplate;

import com.haulmont.cuba.client.ClientConfig;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.addon.yargemailtemplateaddon.entity.LayoutEmailTemplate;
import com.haulmont.cuba.gui.components.LookupPickerField;
import com.haulmont.reports.entity.Report;
import com.haulmont.reports.entity.ReportInputParameter;

import javax.inject.Inject;
import javax.inject.Named;

public class LayoutEmailTemplateEdit extends AbstractEditor<LayoutEmailTemplate> {

    @Named("fieldGroup.report")
    protected LookupPickerField reportField;
    @Inject
    protected ClientConfig clientConfig;

    @Override
    public boolean validateAll() {
        return super.validateAll() && validateContentReportParameter();
    }

    protected boolean validateContentReportParameter() {
        Report report = reportField.getValue();
        for (ReportInputParameter parameter: report.getInputParameters()) {
            if (parameter.getAlias().equals("content")) {
                return true;
            }
        }
        NotificationType notificationType = NotificationType.valueOf(clientConfig.getValidationNotificationType());
        showNotification(messages.getMainMessage("validationFail.caption"), messages.getMessage(
                getClass(),"contentParamValidationFail"), notificationType);
        return false;
    }
}