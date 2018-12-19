package com.haulmont.addon.emailtemplates.web.emailtemplate.send;

import com.haulmont.addon.emailtemplates.dto.ReportWithParams;
import com.haulmont.addon.emailtemplates.entity.EmailTemplate;
import com.haulmont.addon.emailtemplates.entity.ParameterValue;
import com.haulmont.addon.emailtemplates.entity.TemplateReport;
import com.haulmont.addon.emailtemplates.exceptions.ReportParameterTypeChangedException;
import com.haulmont.addon.emailtemplates.exceptions.TemplateNotFoundException;
import com.haulmont.addon.emailtemplates.service.EmailTemplatesService;
import com.haulmont.addon.emailtemplates.web.frames.EmailTemplateParametersFrame;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.client.ClientConfig;
import com.haulmont.cuba.core.app.EmailService;
import com.haulmont.cuba.core.global.EmailException;
import com.haulmont.cuba.core.global.EmailInfo;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.Fragments;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.GroupBoxLayout;
import com.haulmont.cuba.gui.components.HasValue;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.components.VBoxLayout;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.export.ByteArrayDataProvider;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.reports.app.service.ReportService;
import com.haulmont.reports.entity.ParameterType;
import com.haulmont.reports.entity.ReportInputParameter;
import com.haulmont.reports.exception.ReportParametersValidationException;
import com.haulmont.reports.gui.ReportParameterValidator;
import com.haulmont.reports.gui.report.run.ParameterClassResolver;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

@UiController("emailtemplates$EmailTemplate.send")
@UiDescriptor("email-template-send.xml")
public class EmailTemplateSender extends Screen {

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
    private Metadata metadata;

    @Inject
    private Fragments fragments;

    @Inject
    private Notifications notifications;

    @Inject
    private MessageBundle messageBundle;

    @Inject
    protected ReportService reportService;

    @Inject
    protected ParameterClassResolver classResolver;

    @Named("defaultGroup.subject")
    private TextField subjectField;

    @Subscribe
    protected void onInit(InitEvent event) {
        if (emailTemplate == null) {
            throw new IllegalStateException("'emailTemplate' parameter is required");
        }
        emailTemplateDs.setItem(emailTemplate);

        updateDefaultTemplateParameters(((MapScreenOptions) event.getOptions()).getParams());

        defaultBodyParametersFrame = (EmailTemplateParametersFrame) fragments.create(this,
                "emailtemplates$parametersFrame",
                new MapScreenOptions(ParamsMap.of(EmailTemplateParametersFrame.TEMPLATE_REPORT, emailTemplate.getEmailBodyReport(),
                        EmailTemplateParametersFrame.HIDE_REPORT_CAPTION, true)))
                .init();
        defaultBodyParameters.add(defaultBodyParametersFrame.getFragment());

        if (!emailTemplate.getAttachedTemplateReports().isEmpty()) {
            attachmentParametersFrame = (EmailTemplateParametersFrame) fragments.create(this,
                    "emailtemplates$parametersFrame",
                    new MapScreenOptions(ParamsMap.of(EmailTemplateParametersFrame.TEMPLATE_REPORTS, emailTemplate.getAttachedTemplateReports())))
                    .init();
            attachmentParametersFrame.createComponents();
            attachmentParameters.add(attachmentParametersFrame.getFragment());
        } else {
            attachmentGroupBox.setVisible(false);
        }
        subjectField.addValueChangeListener(new Consumer<HasValue.ValueChangeEvent>() {
            @Override
            public void accept(HasValue.ValueChangeEvent valueChangeEvent) {
                if (!Objects.equals(valueChangeEvent.getPrevValue(), valueChangeEvent.getValue())) {
                    emailTemplate.setUseReportSubject(false);
                }
            }
        });
    }

    private void updateDefaultTemplateParameters(Map<String, Object> params) {
        List<TemplateReport> templateReports = new ArrayList<>();
        if (emailTemplate.getEmailBodyReport() != null) {
            templateReports.add(emailTemplate.getEmailBodyReport());
        }
        templateReports.addAll(emailTemplate.getAttachedTemplateReports());
        for (String alias : params.keySet()) {
            for (TemplateReport templateReport : templateReports) {
                ReportInputParameter inputParameter = templateReport.getReport().getInputParameters().stream()
                        .filter(e -> alias.equals(e.getAlias()))
                        .findFirst()
                        .orElse(null);
                if (inputParameter != null) {
                    ParameterValue parameterValue = templateReport.getParameterValues().stream()
                            .filter(pv -> pv.getAlias().equals(alias))
                            .findFirst()
                            .orElse(null);
                    if (parameterValue == null) {
                        parameterValue = metadata.create(ParameterValue.class);
                        parameterValue.setAlias(alias);
                        parameterValue.setParameterType(inputParameter.getType());
                        parameterValue.setTemplateParameters(templateReport);
                        templateReport.getParameterValues().add(parameterValue);
                    }
                    Class parameterClass = classResolver.resolveClass(inputParameter);
                    if (!ParameterType.ENTITY_LIST.equals(inputParameter.getType())) {
                        String stringValue = reportService.convertToString(parameterClass, params.get(alias));
                        parameterValue.setDefaultValue(stringValue);
                    }
                }
            }
        }
    }

    //todo
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
                        Notifications.NotificationType notificationType = Notifications.NotificationType.valueOf(clientConfig.getValidationNotificationType());
                        notifications.create(notificationType).withCaption(messageBundle.getMessage("validationFail.caption")).withDescription(e.getMessage()).show();
                        isValid = false;
                    }
                }
            }
        }
        return isValid;
    }

    public void onCancelButtonClick() {
        close(WINDOW_CLOSE_ACTION);
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
        if (BooleanUtils.isNotTrue(emailTemplate.getUseReportSubject()) && subjectField.getValue() == null) {
            notifications.create(Notifications.NotificationType.WARNING).withDescription(messageBundle.getMessage("emptySubject")).show();
            return;
        }
        EmailInfo emailInfo = getEmailInfo();

        try {
            emailService.sendEmail(emailInfo);
            notifications.create(Notifications.NotificationType.HUMANIZED).withDescription(messageBundle.getMessage("emailSent")).show();
            close(WINDOW_COMMIT_AND_CLOSE_ACTION);
        } catch (EmailException e) {
            notifications.create(Notifications.NotificationType.ERROR).withDescription(StringUtils.join(e.getMessages(), "\n")).show();
        }
    }

    private EmailInfo getEmailInfo() throws ReportParameterTypeChangedException, TemplateNotFoundException {
        return emailTemplatesService.generateEmail(emailTemplate, new ArrayList<>());
    }
}