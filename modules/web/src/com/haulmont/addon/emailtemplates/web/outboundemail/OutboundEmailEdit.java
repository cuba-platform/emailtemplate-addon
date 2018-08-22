package com.haulmont.addon.emailtemplates.web.outboundemail;

import com.haulmont.addon.emailtemplates.dto.ReportWithParams;
import com.haulmont.addon.emailtemplates.entity.EmailTemplate;
import com.haulmont.addon.emailtemplates.entity.OutboundEmail;
import com.haulmont.addon.emailtemplates.exceptions.TemplatesAreNotFoundException;
import com.haulmont.addon.emailtemplates.service.OutboundTemplateService;
import com.haulmont.addon.emailtemplates.web.frames.MultiReportParametersFrame;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.client.ClientConfig;
import com.haulmont.cuba.core.global.EmailInfo;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.impl.AbstractDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.reports.exception.ReportParametersValidationException;
import com.haulmont.reports.gui.ReportParameterValidator;
import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Map;

public class OutboundEmailEdit extends AbstractEditor<OutboundEmail> {

    public static final String IS_TEST = "isTest";

    @WindowParam(name = IS_TEST, required = true)
    protected Boolean isTest;
    @WindowParam(name = "ITEM", required = true)
    protected OutboundEmail outboundEmail;
    @Named("fieldGroup.emailTemplate")
    private PickerField emailTemplateField;
    @Named("fieldGroup.from")
    private TextField fromField;
    @Named("fieldGroup.addresses")
    private TextField addressesField;
    @Inject
    private Button sendButton;

    @Inject
    protected Datasource<OutboundEmail> outboundEmailDs;
    @Inject
    protected ScrollBoxLayout propertiesScrollBox;
    @Inject
    protected ReportParameterValidator reportParameterValidator;
    @Inject
    protected ClientConfig clientConfig;
    @Inject
    protected OutboundTemplateService outboundTemplateService;
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    private Logger log;

    protected EmailTemplate emailTemplate;
    protected MultiReportParametersFrame parametersFrame;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        if (BooleanUtils.isTrue(isTest)) {
            addressesField.setVisible(false);
            fromField.setVisible(false);
            sendButton.setVisible(false);
        }

        emailTemplate = outboundEmail.getEmailTemplate();

        VBoxLayout vBoxLayout = componentsFactory.createComponent(VBoxLayout.class);
        propertiesScrollBox.add(vBoxLayout);
        parametersFrame = (MultiReportParametersFrame) openFrame(vBoxLayout,
                "multiReportParametersFrame", ParamsMap.of(MultiReportParametersFrame.TEMPLATE, emailTemplate));

        emailTemplateField.addValueChangeListener(e -> {
            EmailTemplate template = (EmailTemplate) e.getValue();
            parametersFrame.setTemplateReport(template);
            parametersFrame.createComponents();
        });
    }

    @Override
    protected boolean preClose(String actionId) {
        ((AbstractDatasource) outboundEmailDs).setModified(false);
        return super.preClose(actionId);
    }

    @Override
    public boolean validateAll() {
        return super.validateAll() && crossValidateParameters();
    }

    protected boolean crossValidateParameters() {
        boolean isValid = true;
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

        return isValid;
    }

    public void onCancelButtonClick() {
        close("windowClose ");
    }

    public void onTestButtonClick() {
        if (!validateAll()) {
            return;
        }
        List<ReportWithParams> reportsWithParams = parametersFrame.collectParameters();
        try {
            EmailInfo emailInfo = outboundTemplateService.generateEmail(emailTemplate, reportsWithParams);
            emailInfo.setAddresses(addressesField.getRawValue());
            emailInfo.setFrom(fromField.getRawValue());

            openWindow("outboundEmailScreen", WindowManager.OpenType.DIALOG, ParamsMap.of("email", emailInfo));
        } catch (TemplatesAreNotFoundException e) {
            log.warn(e.getMessage());
        }
    }

    public void onSendButtonClick() {
        if (!validateAll()) {
            return;
        }
        List<ReportWithParams> reportsWithParams = parametersFrame.collectParameters();
        try {
            EmailInfo emailInfo = outboundTemplateService.generateEmail(emailTemplate, reportsWithParams);
            emailInfo.setAddresses(addressesField.getRawValue());
            emailInfo.setFrom(fromField.getRawValue());

            openWindow("outboundEmailScreen", WindowManager.OpenType.DIALOG, ParamsMap.of(
                    "email", emailInfo,
                    "send", Boolean.TRUE));
        } catch (TemplatesAreNotFoundException e) {
            log.warn(e.getMessage());
        }
    }
}