package com.haulmont.addon.yargemailtemplateaddon.web.outboundemail;

import com.haulmont.addon.yargemailtemplateaddon.entity.ContentEmailTemplate;
import com.haulmont.addon.yargemailtemplateaddon.entity.LayoutEmailTemplate;
import com.haulmont.addon.yargemailtemplateaddon.entity.OutboundEmail;
import com.haulmont.addon.yargemailtemplateaddon.service.OutboundTemplateService;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.client.ClientConfig;
import com.haulmont.cuba.core.app.EmailService;
import com.haulmont.cuba.core.global.EmailAttachment;
import com.haulmont.cuba.core.global.EmailException;
import com.haulmont.cuba.core.global.EmailInfo;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.PickerField;
import com.haulmont.cuba.gui.components.ScrollBoxLayout;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.impl.AbstractDatasource;
import com.haulmont.reports.entity.Report;
import com.haulmont.reports.exception.ReportParametersValidationException;
import com.haulmont.reports.gui.ReportParameterValidator;
import com.haulmont.reports.gui.report.run.InputParametersFrame;
import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OutboundEmailEdit extends AbstractEditor<OutboundEmail> {

    @WindowParam(name = "ITEM", required = true)
    protected OutboundEmail outboundEmail;
    @Named("fieldGroup.addressses")
    protected TextField addresssesField;
    @Named("fieldGroup.from")
    protected TextField fromField;
    @Named("fieldGroup.template")
    protected PickerField templateField;
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
    protected EmailService emailService;
    @Inject
    protected Logger log;

    protected List<InputParametersFrame> inputParametersFrames = new ArrayList<>();
    protected List<Report> reports = new ArrayList<>();
    protected LayoutEmailTemplate emailTemplate;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        templateField.setEnabled(false);
        emailTemplate = outboundEmail.getTemplate();
        if (emailTemplate == null) {
            return;
        }
        reports.add(emailTemplate.getReport());

        if (emailTemplate instanceof ContentEmailTemplate) {
            reports.addAll(((ContentEmailTemplate) emailTemplate).getAttachments());
        }

        for (Report report : reports) {
            Map<String, Object> parameters = new HashMap<>(reports.size());
            parameters.put("report", report);
            InputParametersFrame frame = (InputParametersFrame) openFrame(propertiesScrollBox, "report$inputParametersFrame", parameters);
            inputParametersFrames.add(frame);
        }
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
        for (InputParametersFrame frame : inputParametersFrames) {
            if (BooleanUtils.isTrue(frame.getReport().getValidationOn())) {
                try {
                    reportParameterValidator.crossValidateParameters(frame.getReport(),
                            frame.collectParameters());
                } catch (ReportParametersValidationException e) {
                    NotificationType notificationType = NotificationType.valueOf(clientConfig.getValidationNotificationType());
                    showNotification(messages.getMainMessage("validationFail.caption"), e.getMessage(), notificationType);
                    isValid = false;
                    break;
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
        List<Map<String, Object>> params = inputParametersFrames.stream().map(InputParametersFrame::collectParameters).collect(Collectors.toList());
        EmailInfo emailInfo = outboundTemplateService.generateMessageByTemplate(emailTemplate, addresssesField.getRawValue(), fromField.getRawValue(), params);

        openWindow("outboundEmailScreen", WindowManager.OpenType.DIALOG, ParamsMap.of("body", emailInfo));
    }

    public void onSendButtonClick() {
        if (!validateAll()) {
            return;
        }
        List<Map<String, Object>> params = inputParametersFrames.stream().map(InputParametersFrame::collectParameters).collect(Collectors.toList());
        EmailInfo emailInfo = outboundTemplateService.generateMessageByTemplate(emailTemplate, addresssesField.getRawValue(), fromField.getRawValue(), params);

        openWindow("outboundEmailScreen", WindowManager.OpenType.DIALOG, ParamsMap.of(
                "body", emailInfo,
                "send", Boolean.TRUE));
    }
}