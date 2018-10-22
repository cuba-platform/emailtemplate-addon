package com.haulmont.addon.emailtemplates.bean;

import com.google.common.collect.ImmutableMap;
import com.haulmont.addon.emailtemplates.dto.ReportWithParams;
import com.haulmont.addon.emailtemplates.entity.EmailTemplate;
import com.haulmont.addon.emailtemplates.entity.ParameterValue;
import com.haulmont.addon.emailtemplates.entity.TemplateParameters;
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
import java.util.*;


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

    public List<ReportWithParams> getTemplateDefaultValues(EmailTemplate emailTemplate) throws ReportParameterTypeChangedException {
        List<Report> reports = createParamsCollectionByTemplate(emailTemplate);
        List<ReportWithParams> reportWithParams = new ArrayList<>();
        List<String> exceptionMessages = new ArrayList<>();
        List<TemplateParameters> templateParameters = emailTemplate.getParameters() != null ? emailTemplate.getParameters() : Collections.emptyList();
        for (Report report : reports) {
            try {
                TemplateParameters parameters = templateParameters.stream()
                        .filter(e -> report.equals(e.getReport()))
                        .findFirst()
                        .orElse(null);
                List<ParameterValue> parameterValues = null;
                if (parameters != null) {
                    parameterValues = parameters.getParameterValues();
                }
                reportWithParams.add(getReportDefaultValues(report, parameterValues));
            } catch (ReportParameterTypeChangedException e) {
                exceptionMessages.add(e.getMessage());
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
        return reportWithParams;
    }

    public ReportWithParams getReportDefaultValues(Report report, List<ParameterValue> parameterValues) throws ReportParameterTypeChangedException {
        ReportWithParams paramsData = new ReportWithParams(report);
        List<String> exceptionMessages = new ArrayList<>();
        if (parameterValues != null) {
            Report reportFromXml = reportService.convertToReport(report.getXml());
            for (ParameterValue paramValue : parameterValues) {
                String alias = paramValue.getAlias();
                String stringValue = paramValue.getDefaultValue();

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
        if (!exceptionMessages.isEmpty()) {
            StringBuilder messages = new StringBuilder();
            exceptionMessages.forEach(m -> {
                messages.append(m);
                messages.append("\n");
            });
            throw new ReportParameterTypeChangedException(messages.toString());
        }
        return paramsData;
    }

    protected List<Report> createParamsCollectionByTemplate(EmailTemplate emailTemplate) {
        List<Report> parameters = new ArrayList<>();
        if (emailTemplate.getEmailBodyReport() != null) {
            parameters.add(emailTemplate.getEmailBodyReport());
        }
        if (CollectionUtils.isNotEmpty(emailTemplate.getAttachedReports())) {
            parameters.addAll(emailTemplate.getAttachedReports());
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
