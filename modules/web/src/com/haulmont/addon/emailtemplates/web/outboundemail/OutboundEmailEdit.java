package com.haulmont.addon.emailtemplates.web.outboundemail;

import com.haulmont.addon.emailtemplates.dto.ReportWithParams;
import com.haulmont.addon.emailtemplates.entity.EmailTemplate;
import com.haulmont.addon.emailtemplates.entity.OutboundEmail;
import com.haulmont.addon.emailtemplates.entity.ParameterValue;
import com.haulmont.addon.emailtemplates.entity.TemplateParameter;
import com.haulmont.addon.emailtemplates.exceptions.TemplateNotFoundException;
import com.haulmont.addon.emailtemplates.service.OutboundTemplateService;
import com.haulmont.addon.emailtemplates.web.frames.TemplateParametersFrame;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.client.ClientConfig;
import com.haulmont.cuba.core.global.EmailInfo;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.impl.AbstractDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.reports.app.service.ReportService;
import com.haulmont.reports.entity.Report;
import com.haulmont.reports.entity.ReportInputParameter;
import com.haulmont.reports.exception.ReportParametersValidationException;
import com.haulmont.reports.gui.ReportParameterValidator;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class OutboundEmailEdit extends AbstractEditor<OutboundEmail> {

    public static final String IS_TEST = "isTest";

    @WindowParam(name = IS_TEST, required = true)
    protected Boolean isTest;
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
    private CollectionDatasource<TemplateParameter, UUID> templateParametersDs;
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
    private Metadata metadata;
    @Inject
    private ReportService reportService;
    @Inject
    private Logger log;

    protected TemplateParametersFrame parametersFrame;
    protected VBoxLayout frameContainer;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        emailTemplateField.setEnabled(false);

        if (BooleanUtils.isTrue(isTest)) {
            addressesField.setVisible(false);
            fromField.setVisible(false);
            sendButton.setVisible(false);
        }

        frameContainer = componentsFactory.createComponent(VBoxLayout.class);
        propertiesScrollBox.add(frameContainer);
    }

    @Override
    protected void postInit() {
        super.postInit();

        EmailTemplate emailTemplate = getItem().getEmailTemplate();

        List<ReportWithParams> parameters = new ArrayList<>();
        if (emailTemplate.getEmailBody() != null) {
            parameters.add(new ReportWithParams(emailTemplate.getEmailBody()));
        }
        if (CollectionUtils.isNotEmpty(emailTemplate.getAttachments())) {
            List<ReportWithParams> attachmentParams = emailTemplate.getAttachments().stream().map(ReportWithParams::new).collect(Collectors.toList());
            parameters.addAll(attachmentParams);
        }

        fillDefaultValues(parameters);

        parametersFrame = (TemplateParametersFrame) openFrame(frameContainer,"templateParametersFrame",
                ParamsMap.of(TemplateParametersFrame.PARAMETERS, parameters));
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
            EmailInfo emailInfo = outboundTemplateService.generateEmail(getItem().getEmailTemplate(), reportsWithParams);
            emailInfo.setAddresses(addressesField.getRawValue());
            emailInfo.setFrom(fromField.getRawValue());

            openWindow("outboundEmailScreen", WindowManager.OpenType.DIALOG, ParamsMap.of("email", emailInfo));
        } catch (TemplateNotFoundException e) {
            log.warn(e.getMessage());
        }
    }

    public void onSendButtonClick() {
        if (!validateAll()) {
            return;
        }
        List<ReportWithParams> reportsWithParams = parametersFrame.collectParameters();
        try {
            EmailInfo emailInfo = outboundTemplateService.generateEmail(getItem().getEmailTemplate(), reportsWithParams);
            emailInfo.setAddresses(addressesField.getRawValue());
            emailInfo.setFrom(fromField.getRawValue());

            openWindow("outboundEmailScreen", WindowManager.OpenType.DIALOG, ParamsMap.of(
                    "email", emailInfo,
                    "send", Boolean.TRUE));
        } catch (TemplateNotFoundException e) {
            log.warn(e.getMessage());
        }
    }

    protected void fillDefaultValues(List<ReportWithParams> parameters) {
        templateParametersDs.refresh();
        List<TemplateParameter> defaultParams = new ArrayList<>(templateParametersDs.getItems());
        for (ReportWithParams paramsData: parameters) {
            TemplateParameter templateParameter = defaultParams.stream().filter(e -> e.getReport().equals(paramsData.getReport())).findFirst().orElse(null);
            defaultParams.remove(templateParameter);
            if (templateParameter != null) {
                for (ParameterValue paramValue: templateParameter.getParameterValues()) {
                    String alias = paramValue.getAlias();
                    String stringValue = paramValue.getDefaultValue();
                    Report report = templateParameter.getReport();
                    Report reportFromXml = reportService.convertToReport(report.getXml());
                    ReportInputParameter inputParameter = reportFromXml.getInputParameters().stream()
                            .filter(e -> e.getAlias().equals(alias))
                            .findFirst()
                            .orElse(null);
                    if (inputParameter != null) {
                        MetaClass metaClass = metadata.getClass(inputParameter.getEntityMetaClass());
                        if (metaClass != null) {
                            Class javaClass = metaClass.getJavaClass();
                            Object value = reportService.convertFromString(javaClass, stringValue);
                            paramsData.put(alias, value);
                        }
                    }
                }
            }
        }
    }

}