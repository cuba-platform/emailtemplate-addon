package com.haulmont.addon.emailtemplates.builder;

import com.haulmont.addon.emailtemplates.bean.TemplateParametersExtractor;
import com.haulmont.addon.emailtemplates.dto.ReportWithParams;
import com.haulmont.addon.emailtemplates.entity.EmailTemplate;
import com.haulmont.addon.emailtemplates.entity.ParameterValue;
import com.haulmont.addon.emailtemplates.entity.TemplateReport;
import com.haulmont.addon.emailtemplates.exceptions.ReportParameterTypeChangedException;
import com.haulmont.addon.emailtemplates.exceptions.TemplateNotFoundException;
import com.haulmont.addon.emailtemplates.service.EmailTemplatesService;
import com.haulmont.cuba.core.app.EmailService;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.EmailException;
import com.haulmont.cuba.core.global.EmailInfo;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.reports.app.service.ReportService;
import com.haulmont.reports.entity.ParameterType;
import com.haulmont.reports.entity.Report;
import com.haulmont.reports.entity.ReportInputParameter;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EmailTemplateBuilderImpl implements EmailTemplateBuilder {

    protected EmailTemplate emailTemplate;

    protected Metadata metadata = AppBeans.get(Metadata.class);
    protected TemplateParametersExtractor extractorService = AppBeans.get(TemplateParametersExtractor.class);
    protected ReportService reportService = AppBeans.get(ReportService.class);
    protected EmailTemplatesService templatesService = AppBeans.get(EmailTemplatesService.class);
    protected EmailService emailService = AppBeans.get(EmailService.class);

    public EmailTemplateBuilderImpl(EmailTemplate emailTemplate) {
        this.emailTemplate = cloneTemplate(emailTemplate);
    }

    @Override
    public EmailTemplateBuilder setSubject(String subject) {
        emailTemplate.setSubject(subject);
        return this;
    }

    @Override
    public EmailTemplateBuilder setFrom(String address) {
        emailTemplate.setFrom(address);
        return this;
    }

    @Override
    public EmailTemplateBuilder addTo(String to) {
        String toAddresses = emailTemplate.getTo() + ", " + to;
        emailTemplate.setTo(toAddresses);
        return this;
    }

    @Override
    public EmailTemplateBuilder setTo(String to) {
        emailTemplate.setTo(to);
        return this;
    }

    @Override
    public EmailTemplateBuilder addCc(String cc) {
        String ccAddresses = emailTemplate.getCc() + ", " + cc;
        emailTemplate.setCc(ccAddresses);
        return this;
    }

    @Override
    public EmailTemplateBuilder setCc(String cc) {
        emailTemplate.setCc(cc);
        return this;
    }

    @Override
    public EmailTemplateBuilder addBcc(String bcc) {
        String bccAddresses = emailTemplate.getBcc() + ", " + bcc;
        emailTemplate.setBcc(bccAddresses);
        return null;
    }

    @Override
    public EmailTemplateBuilder setBcc(String bcc) {
        emailTemplate.setBcc(bcc);
        return this;
    }

    @Override
    public EmailTemplateBuilder addAttachmentReport(Report report) {
        TemplateReport templateReport = metadata.create(TemplateReport.class);
        templateReport.setReport(report);
        emailTemplate.getAttachedTemplateReports().add(templateReport);
        return this;
    }

    @Override
    public EmailTemplateBuilder setAttachmentReports(Collection<Report> reports) {
        List<TemplateReport> templateReports = reports.stream().map(r -> {
            TemplateReport templateReport = metadata.create(TemplateReport.class);
            templateReport.setReport(r);
            return templateReport;
        }).collect(Collectors.toList());
        emailTemplate.setAttachedTemplateReports(templateReports);
        return this;
    }


    @Override
    public EmailTemplateBuilder addAttachmentFile(FileDescriptor file) {
        emailTemplate.getAttachedFiles().add(file);
        return this;
    }

    @Override
    public EmailTemplateBuilder setAttachmentFiles(List<FileDescriptor> files) {
        emailTemplate.setAttachedFiles(files);
        return this;
    }

    @Override
    public EmailTemplateBuilder setAttachmentParameter(Report report, String key, Object value) {
        List<TemplateReport> attachedTemplateReports = emailTemplate.getAttachedTemplateReports();
        List<TemplateReport> templateReports = attachedTemplateReports.stream()
                .filter(e -> e.getReport().equals(report))
                .collect(Collectors.toList());
        attachedTemplateReports.removeAll(templateReports);

        TemplateReport templateReport = metadata.create(TemplateReport.class);
        templateReport.setReport(report);
        List<ParameterValue> values = new ArrayList<>();

        ParameterValue parameterValue = createParameterValue(report, key, value);
        if (parameterValue != null) {
            parameterValue.setTemplateParameters(templateReport);
            values.add(parameterValue);
        }
        templateReport.setParameterValues(values);
        attachedTemplateReports.add(templateReport);
        return this;
    }

    @Override
    public EmailTemplateBuilder setAttachmentParameters(ReportWithParams reportWithParams) {
        Report report = reportWithParams.getReport();
        List<TemplateReport> attachedTemplateReports = emailTemplate.getAttachedTemplateReports();

        List<TemplateReport> templateReports = attachedTemplateReports.stream()
                .filter(e -> e.getReport().equals(report))
                .collect(Collectors.toList());
        attachedTemplateReports.removeAll(templateReports);

        TemplateReport templateReport = metadata.create(TemplateReport.class);
        templateReport.setReport(report);
        List<ParameterValue> values = new ArrayList<>();
        Map<String, Object> reportParams = reportWithParams.getParams();
        for (String alias : reportParams.keySet()) {
            ParameterValue parameterValue = createParameterValue(report, alias, reportParams.get(alias));
            if (parameterValue != null) {
                parameterValue.setTemplateParameters(templateReport);
                values.add(parameterValue);
            }
        }
        templateReport.setParameterValues(values);
        attachedTemplateReports.add(templateReport);
        return this;
    }

    @Override
    public EmailTemplateBuilder setAttachmentParameters(Report report, Map<String, Object> params) {
        ReportWithParams reportWithParams = new ReportWithParams(report);
        reportWithParams.setParams(params);
        setAttachmentParameters(reportWithParams);
        return this;
    }

    @Override
    public EmailTemplateBuilder setBodyParameter(String key, Object value) {
        TemplateReport emailBodyReport = emailTemplate.getEmailBodyReport();
        emailBodyReport.getParameterValues().stream()
                .filter(v -> v.getAlias().equals(key))
                .forEach(v -> {
                    ReportInputParameter inputParameter = emailBodyReport.getReport().getInputParameters().stream()
                            .filter(r -> r.getAlias().equals(key))
                            .findFirst()
                            .orElse(null);
                    if (inputParameter != null) {
                        if (!ParameterType.ENTITY_LIST.equals(inputParameter.getType())) {
                            Class parameterClass = extractorService.resolveClass(inputParameter);
                            String stringValue = reportService.convertToString(parameterClass, value);
                            v.setDefaultValue(stringValue);
                        }
                        v.setParameterType(inputParameter.getType());
                    }
                });
        return this;
    }

    @Override
    public EmailTemplateBuilder setBodyParameters(Map<String, Object> params) {
        for (String alias : params.keySet()) {
            setBodyParameter(alias, params.get(alias));
        }
        return this;
    }

    @Override
    public EmailTemplateBuilder setAttachmentParameters(Collection<ReportWithParams> reportsWithParams) {
        List<TemplateReport> attachedTemplateReports = emailTemplate.getAttachedTemplateReports();

        List<TemplateReport> templateReports = attachedTemplateReports.stream()
                .filter(e -> reportsWithParams.stream()
                        .anyMatch(r -> r.getReport().equals(e.getReport())))
                .collect(Collectors.toList());
        attachedTemplateReports.removeAll(templateReports);

        List<TemplateReport> templateParameters = new ArrayList<>();

        for (ReportWithParams reportWithParams : reportsWithParams) {
            Report report = reportWithParams.getReport();
            TemplateReport templateParameter = metadata.create(TemplateReport.class);
            templateParameter.setReport(report);
            List<ParameterValue> values = new ArrayList<>();
            Map<String, Object> paramValues = reportWithParams.getParams();
            for (String alias : paramValues.keySet()) {
                ParameterValue parameterValue = createParameterValue(report, alias, paramValues.get(alias));
                if (parameterValue != null) {
                    parameterValue.setTemplateParameters(templateParameter);
                    values.add(parameterValue);
                }
            }
            templateParameter.setParameterValues(values);
            templateParameters.add(templateParameter);
        }
        attachedTemplateReports.addAll(templateParameters);
        return this;
    }

    protected ParameterValue createParameterValue(Report report, String key, Object value) {
        ParameterValue parameterValue = null;

        ReportInputParameter inputParameter = report.getInputParameters().stream()
                .filter(e -> e.getAlias().equals(key))
                .findFirst()
                .orElse(null);
        if (inputParameter != null) {
            parameterValue = metadata.create(ParameterValue.class);
            parameterValue.setAlias(key);
            parameterValue.setParameterType(inputParameter.getType());
            if (!ParameterType.ENTITY_LIST.equals(inputParameter.getType())) {
                Class parameterClass = extractorService.resolveClass(inputParameter);
                String stringValue = reportService.convertToString(parameterClass, value);
                parameterValue.setDefaultValue(stringValue);
            }
        }
        return parameterValue;
    }


    @Override
    public EmailInfo generateEmail() throws ReportParameterTypeChangedException, TemplateNotFoundException {
        return templatesService.generateEmail(emailTemplate, extractorService.getTemplateDefaultValues(emailTemplate));
    }

    @Override
    public EmailTemplate build() {
        return cloneTemplate(emailTemplate);
    }

    protected EmailTemplate cloneTemplate(EmailTemplate emailTemplate) {
        EmailTemplate clonedTemplate = metadata.create(emailTemplate.getClass());
        BeanUtils.copyProperties(emailTemplate, clonedTemplate);
        List<TemplateReport> attachedTemplateReports = new ArrayList<>();
        for (TemplateReport templateReport: emailTemplate.getAttachedTemplateReports()) {
            TemplateReport newTemplateReport = metadata.create(templateReport.getClass());
            BeanUtils.copyProperties(templateReport, newTemplateReport);
            attachedTemplateReports.add(newTemplateReport);
        }
        clonedTemplate.setAttachedTemplateReports(attachedTemplateReports);
        return clonedTemplate;
    }

    @Override
    public void sendEmail() throws TemplateNotFoundException, ReportParameterTypeChangedException, EmailException {
        emailService.sendEmail(generateEmail());
    }

    @Override
    public void sendEmail(boolean async) throws TemplateNotFoundException, ReportParameterTypeChangedException, EmailException {
        if (async) {
            sendEmailAsync();
        } else {
            sendEmail();
        }
    }

    @Override
    public void sendEmailAsync() throws TemplateNotFoundException, ReportParameterTypeChangedException {
        emailService.sendEmailAsync(generateEmail());
    }
}
