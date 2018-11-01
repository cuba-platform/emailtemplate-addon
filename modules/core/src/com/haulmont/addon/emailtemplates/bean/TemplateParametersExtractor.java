package com.haulmont.addon.emailtemplates.bean;

import com.google.common.collect.ImmutableMap;
import com.haulmont.addon.emailtemplates.dto.ReportWithParams;
import com.haulmont.addon.emailtemplates.entity.EmailTemplate;
import com.haulmont.addon.emailtemplates.entity.ParameterValue;
import com.haulmont.addon.emailtemplates.entity.TemplateReport;
import com.haulmont.addon.emailtemplates.exceptions.ReportParameterTypeChangedException;
import com.haulmont.addon.emailtemplates.service.EmailTemplatesService;
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
    protected EmailTemplatesService emailTemplatesService;

    @Inject
    protected ReportService reportService;

    public List<ReportWithParams> getTemplateDefaultValues(EmailTemplate emailTemplate) throws ReportParameterTypeChangedException {
        List<TemplateReport> templateReports = createParamsCollectionByTemplate(emailTemplate);
        List<ReportWithParams> reportWithParams = new ArrayList<>();
        List<String> exceptionMessages = new ArrayList<>();
        for (TemplateReport templateReport : templateReports) {
            try {
                List<ParameterValue> parameterValues = null;
                if (templateReport != null) {
                    parameterValues = templateReport.getParameterValues();
                    reportWithParams.add(getReportDefaultValues(templateReport.getReport(), parameterValues));
                }
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
                        emailTemplatesService.checkParameterTypeChanged(inputParameter, paramValue);
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

    protected List<TemplateReport> createParamsCollectionByTemplate(EmailTemplate emailTemplate) {
        List<TemplateReport> templateReports = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(emailTemplate.getAttachedTemplateReports())) {
            templateReports.addAll(emailTemplate.getAttachedTemplateReports());
        }
        templateReports.add(emailTemplate.getEmailBodyReport());
        return templateReports;
    }

    public Class resolveClass(ReportInputParameter parameter) {
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
