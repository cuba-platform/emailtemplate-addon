package com.haulmont.addon.emailtemplates.web.outboundemail;

import com.haulmont.addon.emailtemplates.dto.ReportWithParams;
import com.haulmont.addon.emailtemplates.entity.EmailTemplate;
import com.haulmont.addon.emailtemplates.entity.OutboundEmail;
import com.haulmont.addon.emailtemplates.exceptions.TemplateNotFoundException;
import com.haulmont.addon.emailtemplates.service.OutboundTemplateService;
import com.haulmont.addon.emailtemplates.web.frames.EmailTemplateParametersFrame;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.client.ClientConfig;
import com.haulmont.cuba.core.global.EmailInfo;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.impl.AbstractDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.reports.exception.ReportParametersValidationException;
import com.haulmont.reports.gui.ReportParameterValidator;
import org.apache.commons.lang.BooleanUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Map;

public class OutboundEmailEdit extends AbstractEditor<OutboundEmail> {

    @Named("fieldGroup.emailTemplate")
    private PickerField emailTemplateField;
    @Named("fieldGroup.from")
    private TextField fromField;
    @Named("fieldGroup.addresses")
    private TextField addressesField;

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


    protected EmailTemplateParametersFrame parametersFrame;
    protected VBoxLayout frameContainer;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        emailTemplateField.setEnabled(false);

        frameContainer = componentsFactory.createComponent(VBoxLayout.class);
        propertiesScrollBox.add(frameContainer);
    }

    @Override
    protected void postInit() {
        super.postInit();
        EmailTemplate emailTemplate = getItem().getEmailTemplate();

        fromField.setValue(emailTemplate.getFrom());
        addressesField.setValue(emailTemplate.getTo());

        parametersFrame = (EmailTemplateParametersFrame) openFrame(frameContainer, "emailtemplates$parametersFrame",
                ParamsMap.of(EmailTemplateParametersFrame.TEMPLATE, emailTemplate));
        parametersFrame.createComponents();
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
        close(Window.CLOSE_ACTION_ID);
    }

    public void onTestButtonClick() throws TemplateNotFoundException {
        if (!validateAll()) {
            return;
        }
        List<ReportWithParams> reportsWithParams = parametersFrame.collectParameters();

        EmailInfo emailInfo = outboundTemplateService.generateEmail(getItem().getEmailTemplate(), reportsWithParams);
        emailInfo.setAddresses(addressesField.getRawValue());
        emailInfo.setFrom(fromField.getRawValue());

        openWindow("emailTemplatePreview", WindowManager.OpenType.DIALOG, ParamsMap.of("email", emailInfo));

    }

    public void onSendButtonClick() throws TemplateNotFoundException {
        if (!validateAll()) {
            return;
        }
        List<ReportWithParams> reportsWithParams = parametersFrame.collectParameters();

        EmailInfo emailInfo = outboundTemplateService.generateEmail(getItem().getEmailTemplate(), reportsWithParams);
        emailInfo.setAddresses(addressesField.getRawValue());
        emailInfo.setFrom(fromField.getRawValue());

        openWindow("emailTemplatePreview", WindowManager.OpenType.DIALOG, ParamsMap.of(
                "email", emailInfo,
                "send", Boolean.TRUE));
    }
}