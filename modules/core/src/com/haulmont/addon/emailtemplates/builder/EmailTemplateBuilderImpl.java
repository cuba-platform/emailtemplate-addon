package com.haulmont.addon.emailtemplates.builder;

import com.haulmont.addon.emailtemplates.core.EmailTemplatesAPI;
import com.haulmont.addon.emailtemplates.dto.ExtendedEmailInfo;
import com.haulmont.addon.emailtemplates.dto.ReportWithParams;
import com.haulmont.addon.emailtemplates.emailer.EmailerAPI;
import com.haulmont.addon.emailtemplates.entity.EmailTemplate;
import com.haulmont.addon.emailtemplates.entity.TemplateReport;
import com.haulmont.addon.emailtemplates.exceptions.ReportParameterTypeChangedException;
import com.haulmont.addon.emailtemplates.exceptions.TemplateNotFoundException;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.EmailException;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.reports.entity.Report;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EmailTemplateBuilderImpl implements EmailTemplateBuilder {

    protected EmailTemplate emailTemplate;

    protected List<ReportWithParams> reportParams = new ArrayList<>();

    protected Metadata metadata = AppBeans.get(Metadata.class);
    protected EmailTemplatesAPI emailTemplates = AppBeans.get(EmailTemplatesAPI.class);
    protected EmailerAPI emailer = AppBeans.get(EmailerAPI.class);

    public EmailTemplateBuilderImpl(EmailTemplate emailTemplate) {
        this.emailTemplate = cloneTemplate(emailTemplate);
    }

    @Override
    public EmailTemplateBuilder setSubject(String subject) {
        emailTemplate.setSubject(subject);
        emailTemplate.setUseReportSubject(false);
        return this;
    }

    @Override
    public EmailTemplateBuilder setFrom(String from) {
        emailTemplate.setFrom(from);
        return this;
    }

    @Override
    public EmailTemplateBuilder addTo(String to) {
        String toAddresses = to;
        if (StringUtils.isNotBlank(emailTemplate.getTo())) {
            toAddresses = emailTemplate.getTo() + ", " + to;
        }
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
        String ccAddresses = cc;
        if (StringUtils.isNotBlank(emailTemplate.getCc())) {
            ccAddresses = emailTemplate.getCc() + ", " + cc;
        }
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
        String bccAddresses = bcc;
        if (StringUtils.isNotBlank(emailTemplate.getBcc())) {
            bccAddresses = emailTemplate.getBcc() + ", " + bcc;
        }
        emailTemplate.setBcc(bccAddresses);
        return this;
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
        ReportWithParams reportWithParams = reportParams.stream()
                .filter(e -> e.getReport().equals(report))
                .findFirst()
                .orElse(null);

        if (reportWithParams == null) {
            reportWithParams = new ReportWithParams(report);
            reportParams.add(reportWithParams);
        }

        reportWithParams.put(key, value);
        return this;
    }

    @Override
    public EmailTemplateBuilder setAttachmentParameters(ReportWithParams reportWithParams) {
        Report report = reportWithParams.getReport();
        ReportWithParams existsParams = reportParams.stream()
                .filter(e -> e.getReport().equals(report))
                .findFirst()
                .orElse(null);

        if (existsParams != null) {
            reportParams.remove(existsParams);
        }
        reportParams.add(reportWithParams);
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
        ReportWithParams reportWithParams = reportParams.stream()
                .filter(e -> e.getReport().equals(emailBodyReport.getReport()))
                .findFirst()
                .orElse(null);

        if (reportWithParams == null) {
            reportWithParams = new ReportWithParams(emailBodyReport.getReport());
            reportParams.add(reportWithParams);
        }

        reportWithParams.put(key, value);

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
        TemplateReport emailBodyReport = emailTemplate.getEmailBodyReport();
        ReportWithParams reportWithParams = reportParams.stream()
                .filter(e -> e.getReport().equals(emailBodyReport.getReport()))
                .findFirst()
                .orElse(null);

        reportParams.clear();
        if (reportWithParams != null) {
            reportParams.add(reportWithParams);
        }
        reportParams.addAll(reportsWithParams);
        return this;
    }

    @Override
    public ExtendedEmailInfo generateEmail() throws ReportParameterTypeChangedException, TemplateNotFoundException {
        return emailTemplates.generateEmail(emailTemplate, reportParams);
    }

    @Override
    public EmailTemplate build() {
        return cloneTemplate(emailTemplate);
    }

    protected EmailTemplate cloneTemplate(EmailTemplate emailTemplate) {
        EmailTemplate clonedTemplate = metadata.create(emailTemplate.getClass());
        BeanUtils.copyProperties(emailTemplate, clonedTemplate);
        List<TemplateReport> attachedTemplateReports = new ArrayList<>();
        List<TemplateReport> templateAttachedTemplateReports = emailTemplate.getAttachedTemplateReports();
        if (templateAttachedTemplateReports != null) {
            for (TemplateReport templateReport : templateAttachedTemplateReports) {
                TemplateReport newTemplateReport = metadata.create(templateReport.getClass());
                BeanUtils.copyProperties(templateReport, newTemplateReport);
                attachedTemplateReports.add(newTemplateReport);
            }
        }
        clonedTemplate.setAttachedTemplateReports(attachedTemplateReports);
        return clonedTemplate;
    }

    @Override
    public void sendEmail() throws TemplateNotFoundException, ReportParameterTypeChangedException, EmailException {
        emailer.sendEmail(generateEmail());
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
        emailer.sendEmailAsync(generateEmail());
    }
}
