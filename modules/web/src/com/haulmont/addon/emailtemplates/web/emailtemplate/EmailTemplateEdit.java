package com.haulmont.addon.emailtemplates.web.emailtemplate;

import com.haulmont.addon.emailtemplates.dto.ReportWithParams;
import com.haulmont.addon.emailtemplates.entity.EmailTemplate;
import com.haulmont.addon.emailtemplates.entity.ParameterValue;
import com.haulmont.addon.emailtemplates.entity.TemplateParameter;
import com.haulmont.addon.emailtemplates.web.editors.ParametersEditor;
import com.haulmont.addon.emailtemplates.web.frames.TemplateParametersFrame;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.LookupPickerField;
import com.haulmont.cuba.gui.components.ScrollBoxLayout;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.components.VBoxLayout;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.reports.entity.Report;
import com.haulmont.reports.entity.ReportInputParameter;
import org.apache.commons.collections4.CollectionUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;
import java.util.stream.Collectors;

public class EmailTemplateEdit extends ParametersEditor<EmailTemplate> {

    @Named("fieldGroup.caption")
    private TextField captionField;
    @Named("fieldGroup.emailBody")
    private LookupPickerField emailBodyField;
    @Inject
    private ScrollBoxLayout propertiesScrollBox;

    @Inject
    private CollectionDatasource<Report, UUID> attachmentsDs;
    @Inject
    private CollectionDatasource<TemplateParameter, UUID> parametersDs;
    @Inject
    private CollectionDatasource<ParameterValue, UUID> parameterValuesDs;

    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    private Metadata metadata;


    protected TemplateParametersFrame parametersFrame;
    protected Collection<TemplateParameter> defaultParameters;
    protected VBoxLayout frameContainer;

    @Override
    public void init(Map<String, Object> params) {
        frameContainer = componentsFactory.createComponent(VBoxLayout.class);
        propertiesScrollBox.add(frameContainer);
    }

    @Override
    protected void postInit() {
        EmailTemplate emailTemplate = getItem();
        defaultParameters = parametersDs.getItems();

        List<ReportWithParams> parameters = new ArrayList<>();
        if (emailTemplate.getEmailBody() != null) {
            parameters.add(new ReportWithParams(emailTemplate.getEmailBody()));
        }
        if (CollectionUtils.isNotEmpty(emailTemplate.getAttachments())) {
            List<ReportWithParams> attachmentParams = emailTemplate.getAttachments().stream().map(ReportWithParams::new).collect(Collectors.toList());
            parameters.addAll(attachmentParams);
        }

        fillParamsByDefaultValues(parameters, defaultParameters);

        parametersFrame = (TemplateParametersFrame) openFrame(frameContainer, "templateParametersFrame",
                ParamsMap.of(TemplateParametersFrame.PARAMETERS, parameters, TemplateParametersFrame.IS_DEFAULT_PARAM_VALUES, true));

        emailBodyField.addValueChangeListener(e -> {
            Report report = (Report) e.getValue();
            List<ReportWithParams> params = new ArrayList<>();
            if (report != null) {
                captionField.setValue(report.getName());
                params.add(new ReportWithParams(report));
                if (CollectionUtils.isNotEmpty(emailTemplate.getAttachments())) {
                    List<ReportWithParams> attachmentParams = emailTemplate.getAttachments().stream().map(ReportWithParams::new).collect(Collectors.toList());
                    params.addAll(attachmentParams);
                }
            }
            fillParamsByDefaultValues(params, defaultParameters);
            parametersFrame.setParameters(params);
            parametersFrame.createComponents();
        });

        attachmentsDs.addCollectionChangeListener(e -> {
            List<ReportWithParams> params = new ArrayList<>();
            if (emailTemplate.getEmailBody() != null) {
                params.add(new ReportWithParams(emailTemplate.getEmailBody()));
            }
            if (CollectionUtils.isNotEmpty(emailTemplate.getAttachments())) {
                List<ReportWithParams> attachmentParams = emailTemplate.getAttachments().stream().map(ReportWithParams::new).collect(Collectors.toList());
                params.addAll(attachmentParams);
            }
            fillParamsByDefaultValues(params, defaultParameters);
            parametersFrame.setParameters(params);
            parametersFrame.createComponents();
        });
    }

    @Override
    protected boolean preCommit() {
        for (TemplateParameter templateParameter : new ArrayList<>(defaultParameters)) {
            parametersDs.removeItem(templateParameter);
        }
        List<TemplateParameter> templateParameters = new ArrayList<>();

        for (ReportWithParams reportWithParams : parametersFrame.collectParameters()) {
            Report report = reportWithParams.getReport();
            TemplateParameter templateParameter = metadata.create(TemplateParameter.class);
            parametersDs.addItem(templateParameter);
            templateParameter.setEmailTemplate(getItem());
            templateParameter.setReport(report);
            Map<String, Object> params = reportWithParams.getParams();
            for (String alias : params.keySet()) {
                parametersDs.setItem(templateParameter);
                ReportInputParameter inputParameter = report.getInputParameters().stream()
                        .filter(e -> e.getAlias().equals(alias))
                        .findFirst()
                        .orElse(null);
                if (inputParameter != null) {
                    ParameterValue parameterValue = metadata.create(ParameterValue.class);
                    parameterValuesDs.addItem(parameterValue);
                    parameterValue.setTemplateParameter(templateParameter);
                    parameterValue.setAlias(alias);
                    parameterValue.setParameterType(inputParameter.getType());
                    Class parameterClass = classResolver.resolveClass(inputParameter);
                    String value = reportService.convertToString(parameterClass, params.get(alias));
                    parameterValue.setDefaultValue(value);
                }
            }
            templateParameters.add(templateParameter);
        }
        getItem().setParameters(templateParameters);
        return super.preCommit();
    }
}