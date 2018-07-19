package com.haulmont.addon.yargemailtemplateaddon.web.outboundemail;

import com.haulmont.addon.yargemailtemplateaddon.dto.ReportWithParams;
import com.haulmont.addon.yargemailtemplateaddon.entity.ContentEmailTemplate;
import com.haulmont.addon.yargemailtemplateaddon.entity.LayoutEmailTemplate;
import com.haulmont.addon.yargemailtemplateaddon.entity.OutboundEmail;
import com.haulmont.addon.yargemailtemplateaddon.service.OutboundTemplateService;
import com.haulmont.addon.yargemailtemplateaddon.web.frames.MultiReportParametersFrame;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.client.ClientConfig;
import com.haulmont.cuba.core.global.EmailInfo;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.impl.AbstractDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.reports.entity.Report;
import com.haulmont.reports.exception.ReportParametersValidationException;
import com.haulmont.reports.gui.ReportParameterValidator;
import org.apache.commons.lang.BooleanUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OutboundEmailEdit extends AbstractEditor<OutboundEmail> {

    @WindowParam(name = "ITEM", required = true)
    protected OutboundEmail outboundEmail;
    @Named("fieldGroup.addressses")
    protected TextField addresssesField;
    @Named("fieldGroup.from")
    protected TextField fromField;
    @Named("fieldGroup.layoutTemplate")
    protected LookupPickerField layoutTemplateField;
    @Named("fieldGroup.contentTemplate")
    protected PickerField contentTemplateField;
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

    protected LayoutEmailTemplate layoutTemplate;
    protected ContentEmailTemplate contentTemplate;
    protected MultiReportParametersFrame parametersFrame;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        List<Report> reports = new ArrayList<>();

        layoutTemplateField.setEnabled(false);
        contentTemplateField.setEnabled(false);

        layoutTemplate = outboundEmail.getLayoutTemplate();
        contentTemplate = outboundEmail.getContentTemplate();

        reports.add(layoutTemplate.getReport());
        if (contentTemplate != null) {
            reports.add(contentTemplate.getReport());
            if (contentTemplate.getAttachments() != null) {
                reports.addAll(contentTemplate.getAttachments());
            }
        }

        VBoxLayout vBoxLayout = componentsFactory.createComponent(VBoxLayout.class);
        propertiesScrollBox.add(vBoxLayout);
        parametersFrame = (MultiReportParametersFrame) openFrame(vBoxLayout,
                "multiReportParametersFrame", ParamsMap.of(MultiReportParametersFrame.REPORT_PARAMETER, reports));
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
        for (ReportWithParams reportWithParams: parametersFrame.collectParameters()) {
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
        EmailInfo emailInfo = outboundTemplateService.generateEmail(layoutTemplate, contentTemplate, reportsWithParams);
        emailInfo.setAddresses(addresssesField.getRawValue());
        emailInfo.setFrom(fromField.getRawValue());

        openWindow("outboundEmailScreen", WindowManager.OpenType.DIALOG, ParamsMap.of("body", emailInfo));
    }

    public void onSendButtonClick() {
        if (!validateAll()) {
            return;
        }
        List<ReportWithParams> reportsWithParams = parametersFrame.collectParameters();
        EmailInfo emailInfo = outboundTemplateService.generateEmail(layoutTemplate, contentTemplate, reportsWithParams);
        emailInfo.setAddresses(addresssesField.getRawValue());
        emailInfo.setFrom(fromField.getRawValue());

        openWindow("outboundEmailScreen", WindowManager.OpenType.DIALOG, ParamsMap.of(
                "body", emailInfo,
                "send", Boolean.TRUE));
    }
}