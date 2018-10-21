package com.haulmont.addon.emailtemplates.web.emailtemplate;

import com.haulmont.addon.emailtemplates.dto.ReportWithParams;
import com.haulmont.addon.emailtemplates.entity.ParameterValue;
import com.haulmont.addon.emailtemplates.entity.ReportEmailTemplate;
import com.haulmont.addon.emailtemplates.entity.TemplateParameters;
import com.haulmont.addon.emailtemplates.web.frames.EmailTemplateParametersFrame;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.reports.app.service.ReportService;
import com.haulmont.reports.entity.ParameterType;
import com.haulmont.reports.entity.Report;
import com.haulmont.reports.entity.ReportInputParameter;
import com.haulmont.reports.gui.report.run.ParameterClassResolver;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

public class ReportEmailTemplateEdit extends AbstractEditor<ReportEmailTemplate> {

    @Named("fieldGroup.subject")
    private TextField subjectField;
    @Named("fieldGroup.emailBody")
    private LookupPickerField emailBodyField;
    @Inject
    private ScrollBoxLayout propertiesScrollBox;

    @Inject
    private CollectionDatasource<Report, UUID> attachmentsDs;
    @Inject
    private CollectionDatasource<TemplateParameters, UUID> parametersDs;
    @Inject
    private CollectionDatasource<ParameterValue, UUID> parameterValuesDs;

    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    private Metadata metadata;

    @Inject
    protected ReportService reportService;
    @Inject
    protected ParameterClassResolver classResolver;

    protected EmailTemplateParametersFrame parametersFrame;
    protected Collection<TemplateParameters> defaultParameters;
    protected VBoxLayout frameContainer;

    @Override
    public void init(Map<String, Object> params) {
        frameContainer = componentsFactory.createComponent(VBoxLayout.class);
        propertiesScrollBox.add(frameContainer);
    }

    @Override
    protected void postInit() {
        ReportEmailTemplate emailTemplate = getItem();
        defaultParameters = parametersDs.getItems();

        parametersFrame = (EmailTemplateParametersFrame) openFrame(frameContainer, "emailtemplates$parametersFrame",
                ParamsMap.of(EmailTemplateParametersFrame.TEMPLATE, emailTemplate, EmailTemplateParametersFrame.IS_DEFAULT_PARAM_VALUES, true));

        emailBodyField.addValueChangeListener(e -> {
            Report report = (Report) e.getValue();
            if (report != null) {
                subjectField.setValue(report.getName());
            }
            parametersFrame.createComponents();
        });

        attachmentsDs.addCollectionChangeListener(e -> {
            parametersFrame.createComponents();
        });
    }

    @Override
    protected boolean preCommit() {
        for (TemplateParameters templateParameter : new ArrayList<>(defaultParameters)) {
            parametersDs.removeItem(templateParameter);
        }
        List<TemplateParameters> templateParameters = new ArrayList<>();

        for (ReportWithParams reportWithParams : parametersFrame.collectParameters()) {
            Report report = reportWithParams.getReport();
            TemplateParameters templateParameter = metadata.create(TemplateParameters.class);
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
                    parameterValue.setTemplateParameters(templateParameter);
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