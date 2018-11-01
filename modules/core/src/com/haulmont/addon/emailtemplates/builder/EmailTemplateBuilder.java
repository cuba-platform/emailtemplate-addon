package com.haulmont.addon.emailtemplates.builder;

import com.haulmont.addon.emailtemplates.dto.ReportWithParams;
import com.haulmont.addon.emailtemplates.entity.EmailTemplate;
import com.haulmont.addon.emailtemplates.exceptions.ReportParameterTypeChangedException;
import com.haulmont.addon.emailtemplates.exceptions.TemplateNotFoundException;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.EmailException;
import com.haulmont.cuba.core.global.EmailInfo;
import com.haulmont.reports.entity.Report;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface EmailTemplateBuilder {

    EmailTemplateBuilder setSubject(String subject);

    EmailTemplateBuilder setFrom(String address);

    EmailTemplateBuilder addTo(String to);

    EmailTemplateBuilder setTo(String to);

    EmailTemplateBuilder addCc(String cc);

    EmailTemplateBuilder setCc(String cc);

    EmailTemplateBuilder addBcc(String bcc);

    EmailTemplateBuilder setBcc(String bcc);

    EmailTemplateBuilder addAttachmentReport(Report report);

    EmailTemplateBuilder setAttachmentReports(Collection<Report> reports);

    EmailTemplateBuilder addAttachmentFile(FileDescriptor file);

    EmailTemplateBuilder setAttachmentFiles(List<FileDescriptor> files);

    EmailTemplateBuilder setBodyParameter(String key, Object value);

    EmailTemplateBuilder setBodyParameters(Map<String, Object> params);

    EmailTemplateBuilder setAttachmentParameter(Report report, String key, Object value);

    EmailTemplateBuilder setAttachmentParameters(ReportWithParams reportWithParams);

    EmailTemplateBuilder setAttachmentParameters(Report report, Map<String, Object> params);

    EmailTemplateBuilder setAttachmentParameters(Collection<ReportWithParams> reportsWithParams);

    EmailInfo generateEmail() throws ReportParameterTypeChangedException, TemplateNotFoundException;

    EmailTemplate build();

    void sendEmail() throws TemplateNotFoundException, ReportParameterTypeChangedException, EmailException;

    void sendEmail(boolean async) throws TemplateNotFoundException, ReportParameterTypeChangedException, EmailException;

    void sendEmailAsync() throws TemplateNotFoundException, ReportParameterTypeChangedException;
}
