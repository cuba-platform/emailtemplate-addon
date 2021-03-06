/*
 * Copyright (c) 2008-2019 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.haulmont.addon.emailtemplates.web.emailtemplate.send;

import com.haulmont.addon.emailtemplates.dto.ReportWithParams;
import com.haulmont.addon.emailtemplates.entity.EmailTemplate;
import com.haulmont.addon.emailtemplates.entity.ParameterValue;
import com.haulmont.addon.emailtemplates.entity.TemplateReport;
import com.haulmont.addon.emailtemplates.exceptions.ReportParameterTypeChangedException;
import com.haulmont.addon.emailtemplates.exceptions.TemplateNotFoundException;
import com.haulmont.addon.emailtemplates.service.EmailTemplatesService;
import com.haulmont.addon.emailtemplates.web.frames.EmailTemplateParametersFrame;
import com.haulmont.cuba.client.ClientConfig;
import com.haulmont.cuba.core.app.EmailService;
import com.haulmont.cuba.core.global.EmailException;
import com.haulmont.cuba.core.global.EmailInfo;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.Fragments;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.GroupBoxLayout;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.components.VBoxLayout;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.export.ByteArrayDataProvider;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.screen.FrameOwner;
import com.haulmont.cuba.gui.screen.MessageBundle;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;
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
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@UiController("emailtemplates$EmailTemplate.send")
@UiDescriptor("email-template-send.xml")
public class EmailTemplateSender extends AbstractWindow {

    private final static Charset PREVIEW_CHARSET = StandardCharsets.UTF_16;

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
    protected EmailService emailService;

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
    private TextField<String> subjectField;

    public EmailTemplate getEmailTemplate() {
        return emailTemplate;
    }

    public void setEmailTemplate(EmailTemplate emailTemplate) {
        this.emailTemplate = emailTemplate;
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        if (emailTemplate == null) {
            throw new IllegalStateException("'emailTemplate' parameter is required");
        }
        emailTemplateDs.setItem(emailTemplate);

        setParameters(params);

        defaultBodyParametersFrame = fragments.create(this, EmailTemplateParametersFrame.class)
                .setTemplateReport(emailTemplate.getEmailBodyReport())
                .setHideReportCaption(true)
                .createComponents();
        defaultBodyParameters.add(defaultBodyParametersFrame);

        if (!emailTemplate.getAttachedTemplateReports().isEmpty()) {
            attachmentParametersFrame = fragments.create(this, EmailTemplateParametersFrame.class)
                    .setTemplateReports(emailTemplate.getAttachedTemplateReports())
                    .createComponents();
            attachmentParameters.add(attachmentParametersFrame);
        } else {
            attachmentGroupBox.setVisible(false);
        }

        subjectField.addValueChangeListener(e -> {
            if (!Objects.equals(e.getPrevValue(), e.getValue())) {
                emailTemplate.setUseReportSubject(false);
            }
        });
    }

    public void setParameters(Map<String, Object> params) {
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String alias = entry.getKey();
            Object value = entry.getValue();
            setParameter(alias, value);
        }
    }

    public void setParameter(String alias, Object value) {
        List<TemplateReport> templateReports = new ArrayList<>();
        if (emailTemplate.getEmailBodyReport() != null) {
            templateReports.add(emailTemplate.getEmailBodyReport());
        }
        templateReports.addAll(emailTemplate.getAttachedTemplateReports());
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
                    String stringValue = reportService.convertToString(parameterClass, value);
                    parameterValue.setDefaultValue(stringValue);
                }
            }
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
                        Notifications.NotificationType notificationType = Notifications.NotificationType.valueOf(
                                clientConfig.getValidationNotificationType()
                        );
                        notifications.create(notificationType)
                                .withCaption(messageBundle.getMessage("validationFail.caption"))
                                .withDescription(e.getMessage())
                                .show();
                        isValid = false;
                    }
                }
            }
        }
        return isValid;
    }

    public void onCancelButtonClick() {
        close(FrameOwner.WINDOW_CLOSE_ACTION);
    }

    public void onTestButtonClick() throws TemplateNotFoundException, ReportParameterTypeChangedException {
        if (!validateAll()) {
            return;
        }
        EmailInfo emailInfo = getEmailInfo();
        exportDisplay.show(new ByteArrayDataProvider(emailInfo.getBody().getBytes(PREVIEW_CHARSET)), emailInfo.getCaption() + ".html");
    }

    public void onSendButtonClick() throws TemplateNotFoundException, ReportParameterTypeChangedException {
        if (!validateAll()) {
            return;
        }
        if (BooleanUtils.isNotTrue(emailTemplate.getUseReportSubject()) && subjectField.getValue() == null) {
            notifications.create(Notifications.NotificationType.WARNING)
                    .withDescription(messageBundle.getMessage("emptySubject"))
                    .show();
            return;
        }
        EmailInfo emailInfo = getEmailInfo();

        try {
            emailService.sendEmail(emailInfo);
            notifications.create(Notifications.NotificationType.HUMANIZED)
                    .withDescription(messageBundle.getMessage("emailSent"))
                    .show();
            close(WINDOW_COMMIT_AND_CLOSE_ACTION);
        } catch (EmailException e) {
            notifications.create(Notifications.NotificationType.ERROR)
                    .withDescription(StringUtils.join(e.getMessages(), "\n"))
                    .show();
        }
    }

    private EmailInfo getEmailInfo() throws ReportParameterTypeChangedException, TemplateNotFoundException {
        return emailTemplatesService.generateEmail(emailTemplate, new ArrayList<>());
    }
}