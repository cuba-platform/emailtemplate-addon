package com.haulmont.addon.emailtemplates.bean;

import com.google.common.collect.ImmutableMap;
import com.haulmont.addon.emailtemplates.dto.ReportWithParams;
import com.haulmont.addon.emailtemplates.entity.EmailTemplate;
import com.haulmont.addon.emailtemplates.entity.ParameterValue;
import com.haulmont.addon.emailtemplates.entity.TemplateParameter;
import com.haulmont.addon.emailtemplates.exceptions.ReportParameterTypeChangedException;
import com.haulmont.addon.emailtemplates.service.OutboundTemplateService;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.Scripting;
import com.haulmont.reports.app.service.ReportService;
import com.haulmont.reports.entity.ParameterType;
import com.haulmont.reports.entity.Report;
import com.haulmont.reports.entity.ReportInputParameter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Component(TemplateParametersExtractor.NAME)
public class TemplateParametersExtractor {

    public static final String NAME = "emailtemplates_TemplateParametersConverter";

    protected Map<ParameterType, Class> primitiveParameterTypeMapping = new ImmutableMap.Builder<ParameterType, Class>()
            .put(ParameterType.BOOLEAN, Boolean.class)
            .put(ParameterType.DATE, Date.class)
            .put(ParameterType.DATETIME, Date.class)
            .put(ParameterType.TEXT, String.class)
            .put(ParameterType.NUMERIC, Double.class)
            .put(ParameterType.TIME, Date.class)
            .build();

    @Inject
    protected Scripting scripting;
    @Inject
    protected Metadata metadata;

    @Inject
    protected OutboundTemplateService outboundTemplateService;
    @Inject
    protected ReportService reportService;

    public List<ReportWithParams> getParamsFromTemplateDefaultValues(EmailTemplate emailTemplate, List<TemplateParameter> defaultParams) throws ReportParameterTypeChangedException {
        List<ReportWithParams> parameters = createParamsCollectionByTemplate(emailTemplate);
        List<String> exceptionMessages = new ArrayList<>();

        for (ReportWithParams paramsData: parameters) {
            TemplateParameter templateParameter = defaultParams.stream()
                    .filter(e -> e.getReport().equals(paramsData.getReport()))
                    .findFirst()
                    .orElse(null);
            defaultParams.remove(templateParameter);
            if (templateParameter != null) {
                Report report = templateParameter.getReport();
                for (ParameterValue paramValue: templateParameter.getParameterValues()) {
                    String alias = paramValue.getAlias();
                    String stringValue = paramValue.getDefaultValue();

                    Report reportFromXml = reportService.convertToReport(report.getXml());
                    ReportInputParameter inputParameter = reportFromXml.getInputParameters().stream()
                            .filter(e -> e.getAlias().equals(alias))
                            .findFirst()
                            .orElse(null);
                    if (inputParameter != null) {
                        try {
                            outboundTemplateService.checkParameterTypeChanged(inputParameter, paramValue);
                        } catch (ReportParameterTypeChangedException e) {
                            exceptionMessages.add(e.getMessage());
                        }
                        Class parameterClass = resolveClass(inputParameter);
                        Object value = reportService.convertFromString(parameterClass, stringValue);
                        paramsData.put(alias, value);
                    }
                }
            }
        }
        if (!exceptionMessages.isEmpty()) {
            StringBuilder messages = new StringBuilder();
            exceptionMessages.forEach(m -> {
                messages.append(m);
                messages.append("\n");
            });
            throw new ReportParameterTypeChangedException(messages.toString());
        }
        return parameters;
    }

    public List<ReportWithParams> getParamsFromTemplateDefaultValues(EmailTemplate emailTemplate)
            throws ReportParameterTypeChangedException {
        List<TemplateParameter> defaultParams = new ArrayList<>(emailTemplate.getParameters());
        return getParamsFromTemplateDefaultValues(emailTemplate, defaultParams);
    }

    protected List<ReportWithParams> createParamsCollectionByTemplate(EmailTemplate emailTemplate) {
        List<ReportWithParams> parameters = new ArrayList<>();
        if (emailTemplate.getEmailBody() != null) {
            parameters.add(new ReportWithParams(emailTemplate.getEmailBody()));
        }
        if (CollectionUtils.isNotEmpty(emailTemplate.getAttachments())) {
            List<ReportWithParams> attachmentParams = emailTemplate.getAttachments().stream()
                    .map(ReportWithParams::new)
                    .collect(Collectors.toList());
            parameters.addAll(attachmentParams);
        }
        return parameters;
    }

    protected Class resolveClass(ReportInputParameter parameter) {
        Class aClass = primitiveParameterTypeMapping.get(parameter.getType());
        if (aClass == null) {
            if (parameter.getType() == ParameterType.ENTITY || parameter.getType() == ParameterType.ENTITY_LIST) {
                MetaClass metaClass = metadata.getSession().getClass(parameter.getEntityMetaClass());
                if (metaClass != null) {
                    return metaClass.getJavaClass();
                } else {
                    return null;
                }
            } else if (parameter.getType() == ParameterType.ENUMERATION) {
                if (StringUtils.isNotBlank(parameter.getEnumerationClass())) {
                    return scripting.loadClass(parameter.getEnumerationClass());
                }
            }
        }
        return aClass;
    }
}
