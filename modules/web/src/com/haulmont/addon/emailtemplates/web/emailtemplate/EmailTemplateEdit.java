package com.haulmont.addon.emailtemplates.web.emailtemplate;

import com.haulmont.addon.emailtemplates.dto.ReportWithParams;
import com.haulmont.addon.emailtemplates.entity.EmailTemplate;
import com.haulmont.addon.emailtemplates.entity.ParameterValue;
import com.haulmont.addon.emailtemplates.entity.TemplateParameter;
import com.haulmont.addon.emailtemplates.web.editors.ParametersEditor;
import com.haulmont.addon.emailtemplates.web.frames.EmailTemplateParametersFrame;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.LookupPickerField;
import com.haulmont.cuba.gui.components.ScrollBoxLayout;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.components.VBoxLayout;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.reports.entity.ParameterType;
import com.haulmont.reports.entity.Report;
import com.haulmont.reports.entity.ReportInputParameter;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

public class EmailTemplateEdit extends ParametersEditor<EmailTemplate> {

    @Named("fieldGroup.subject")
    private TextField subjectField;
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

    protected EmailTemplateParametersFrame parametersFrame;
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

        List<ReportWithParams> parameters = getParamsOrEmptyByDefaultValues(emailTemplate, defaultParameters);

        parametersFrame = (EmailTemplateParametersFrame) openFrame(frameContainer, "emailTemplateParametersFrame",
                ParamsMap.of(EmailTemplateParametersFrame.PARAMETERS, parameters, EmailTemplateParametersFrame.IS_DEFAULT_PARAM_VALUES, true));

        emailBodyField.addValueChangeListener(e -> {
            Report report = (Report) e.getValue();
            if (report != null) {
                subjectField.setValue(report.getName());
            }
            List<ReportWithParams> params = getParamsOrEmptyByDefaultValues(emailTemplate, defaultParameters);
            parametersFrame.setParameters(params);
            parametersFrame.createComponents();
        });

        attachmentsDs.addCollectionChangeListener(e -> {
            List<ReportWithParams> params = getParamsOrEmptyByDefaultValues(emailTemplate, defaultParameters);
            parametersFrame.setParameters(params);
            parametersFrame.createComponents();
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
                    if (!ParameterType.ENTITY_LIST.equals(inputParameter.getType())) {
                        String value = reportService.convertToString(parameterClass, params.get(alias));
                        parameterValue.setDefaultValue(value);
                    }
                }
            }
            templateParameters.add(templateParameter);
        }
        getItem().setParameters(templateParameters);
        return super.preCommit();
    }
}