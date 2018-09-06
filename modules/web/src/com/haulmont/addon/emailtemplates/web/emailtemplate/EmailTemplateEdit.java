package com.haulmont.addon.emailtemplates.web.emailtemplate;

import com.haulmont.addon.emailtemplates.dto.ReportWithParams;
import com.haulmont.addon.emailtemplates.entity.EmailTemplate;
import com.haulmont.addon.emailtemplates.entity.ParameterValue;
import com.haulmont.addon.emailtemplates.entity.TemplateParameter;
import com.haulmont.addon.emailtemplates.web.frames.TemplateParametersFrame;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.reports.app.service.ReportService;
import com.haulmont.reports.entity.Report;
import com.haulmont.reports.entity.ReportInputParameter;
import org.apache.commons.collections4.CollectionUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;
import java.util.stream.Collectors;

public class EmailTemplateEdit extends AbstractEditor<EmailTemplate> {

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
    protected ReportService reportService;
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

        fillDefaultValues(parameters);

        parametersFrame = (TemplateParametersFrame) openFrame(frameContainer,"templateParametersFrame",
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
            parametersFrame.setParameters(params);
            parametersFrame.createComponents();
        });
    }

    @Override
    protected boolean preCommit() {
        for (TemplateParameter templateParameter: new ArrayList<>(defaultParameters)) {
            parametersDs.removeItem(templateParameter);
        }
        List<TemplateParameter> templateParameters = new ArrayList<>();

        for (ReportWithParams reportWithParams: parametersFrame.collectParameters()) {
            Report report = reportWithParams.getReport();
            TemplateParameter templateParameter = metadata.create(TemplateParameter.class);
            parametersDs.addItem(templateParameter);
            templateParameter.setEmailTemplate(getItem());
            templateParameter.setReport(report);
            Map<String, Object> params = reportWithParams.getParams();
            for (String alias: params.keySet()) {
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
                    MetaClass metaClass = metadata.getClass(inputParameter.getEntityMetaClass());
                    if (metaClass != null) {
                        Class javaClass = metaClass.getJavaClass();
                        String value = reportService.convertToString(javaClass, params.get(alias));
                        parameterValue.setDefaultValue(value);
                    }
                }
            }
            templateParameters.add(templateParameter);
        }
        getItem().setParameters(templateParameters);
        return super.preCommit();
    }

    protected void fillDefaultValues(List<ReportWithParams> parameters) {
        List<TemplateParameter> defaultParams = new ArrayList<>(defaultParameters);

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