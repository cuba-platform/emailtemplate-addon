package com.haulmont.addon.emailtemplates.web.emailtemplate.send;

import com.haulmont.addon.emailtemplates.dto.ReportWithParams;
import com.haulmont.addon.emailtemplates.entity.EmailTemplate;
import com.haulmont.addon.emailtemplates.exceptions.ReportParameterTypeChangedException;
import com.haulmont.addon.emailtemplates.exceptions.TemplateNotFoundException;
import com.haulmont.addon.emailtemplates.service.EmailTemplatesService;
import com.haulmont.addon.emailtemplates.service.TemplateParametersExtractorService;
import com.haulmont.addon.emailtemplates.web.frames.EmailTemplateParametersFrame;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.client.ClientConfig;
import com.haulmont.cuba.core.app.EmailService;
import com.haulmont.cuba.core.global.EmailException;
import com.haulmont.cuba.core.global.EmailInfo;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.GroupBoxLayout;
import com.haulmont.cuba.gui.components.VBoxLayout;
import com.haulmont.cuba.gui.components.Window;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.export.ByteArrayDataProvider;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.reports.exception.ReportParametersValidationException;
import com.haulmont.reports.gui.ReportParameterValidator;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

public class EmailTemplateSender extends AbstractWindow {

    @WindowParam
    private EmailTemplate emailTemplate;

    @Inject
    private Datasource<EmailTemplate> emailTemplateDs;

    @Inject
    private VBoxLayout defaultBodyParameters;
    protected EmailTemplateParametersFrame defaultBodyParametersFrame;

    @Inject
    private VBoxLayout attachmentParameters;
    protected EmailTemplateParametersFrame attachmentParametersFrame;

    @Inject
    private GroupBoxLayout attachmentGroupBox;

    @Inject
    protected ReportParameterValidator reportParameterValidator;
    @Inject
    protected ClientConfig clientConfig;
    @Inject
    protected EmailTemplatesService emailTemplatesService;

    @Inject
    private ExportDisplay exportDisplay;

    @Inject
    private EmailService emailService;

    @Inject
    private TemplateParametersExtractorService templateParametersExtractorService;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        emailTemplateDs.setItem(emailTemplate);

        defaultBodyParametersFrame = (EmailTemplateParametersFrame) openFrame(defaultBodyParameters, "emailtemplates$parametersFrame",
                ParamsMap.of(EmailTemplateParametersFrame.TEMPLATE_REPORT, emailTemplate.getEmailBodyReport(),
                        EmailTemplateParametersFrame.HIDE_REPORT_CAPTION, true));
        defaultBodyParametersFrame.createComponents();

        if (!emailTemplate.getAttachedTemplateReports().isEmpty()) {
            attachmentParametersFrame = (EmailTemplateParametersFrame) openFrame(attachmentParameters, "emailtemplates$parametersFrame",
                    ParamsMap.of(EmailTemplateParametersFrame.TEMPLATE_REPORTS, emailTemplate.getAttachedTemplateReports()));
            attachmentParametersFrame.createComponents();
        } else {
            attachmentGroupBox.setVisible(false);
        }
    }

    @Override
    public boolean validateAll() {
        return super.validateAll() && crossValidateParameters();
    }

    protected boolean crossValidateParameters() {
        boolean isValid = true;
        isValid = crossValidateParameters(defaultBodyParametersFrame);
        if (isValid) {
            isValid = crossValidateParameters(attachmentParametersFrame);
        }

        return isValid;
    }

    private boolean crossValidateParameters(EmailTemplateParametersFrame parametersFrame) {
        boolean isValid = true;
        if (parametersFrame != null && parametersFrame.collectParameters() != null) {
            for (ReportWithParams reportWithParams : parametersFrame.collectParameters()) {
                if (BooleanUtils.isTrue(reportWithParams.getReport().getValidationOn())) {
                    try {
                        reportParameterValidator.crossValidateParameters(reportWithParams.getReport(),
                                reportWithParams.getParams());
                    } catch (ReportParametersValidationException e) {
                        NotificationType notificationType = NotificationType.valueOf(clientConfig.getValidationNotificationType());
                        showNotification(messages.getMainMessage("validationFail.caption"), e.getMessage(), notificationType);
                        isValid = false;
                    }
                }
            }
        }
        return isValid;
    }

    public void onCancelButtonClick() {
        close(Window.CLOSE_ACTION_ID);
    }

    public void onTestButtonClick() throws TemplateNotFoundException, ReportParameterTypeChangedException {
        if (!validateAll()) {
            return;
        }
        EmailInfo emailInfo = getEmailInfo();
        exportDisplay.show(new ByteArrayDataProvider(emailInfo.getBody().getBytes()), emailInfo.getCaption() + ".html");
    }

    public void onSendButtonClick() throws TemplateNotFoundException, ReportParameterTypeChangedException {
        if (!validateAll()) {
            return;
        }
        EmailInfo emailInfo = getEmailInfo();


        try {
            emailService.sendEmail(emailInfo);
            showNotification(getMessage("emailSent"), NotificationType.HUMANIZED);
            close(COMMIT_ACTION_ID);
        } catch (EmailException e) {
            showNotification(StringUtils.join(e.getMessages(),"\n"), NotificationType.ERROR);
        }
    }

    private EmailInfo getEmailInfo() throws ReportParameterTypeChangedException, TemplateNotFoundException {
        List<ReportWithParams> reportsWithParams = templateParametersExtractorService.getTemplateDefaultValues(emailTemplate);

        EmailInfo emailInfo = emailTemplatesService.generateEmail(emailTemplate, reportsWithParams);
        emailInfo.setAddresses(emailTemplate.getTo());
        emailInfo.setFrom(emailTemplate.getFrom());
        if (BooleanUtils.isNotTrue(emailTemplate.getUseReportSubject())) {
            emailInfo.setCaption(emailTemplate.getSubject());
        }
        return emailInfo;
    }
}