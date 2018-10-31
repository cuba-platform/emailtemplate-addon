package com.haulmont.addon.emailtemplates.dto;

import com.haulmont.addon.emailtemplates.entity.EmailTemplate;
import com.haulmont.addon.emailtemplates.entity.TemplateGroup;
import com.haulmont.addon.emailtemplates.exceptions.ReportParameterTypeChangedException;
import com.haulmont.addon.emailtemplates.exceptions.TemplateNotFoundException;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.EmailException;
import com.haulmont.cuba.core.global.EmailInfo;
import com.haulmont.reports.entity.Report;

import java.io.File;
import java.util.Collection;
import java.util.Map;

public interface EmailTemplateBuilder {

    EmailTemplateBuilder setGroup(TemplateGroup group);

    EmailTemplateBuilder setSubject(String subject);

    EmailTemplateBuilder setFrom(String address);

    EmailTemplateBuilder addAddressee(String addressee);

    EmailTemplateBuilder setAddressee(String addressee);

    EmailTemplateBuilder setCopyAddressee(String addressee);

    EmailTemplateBuilder setHiddenCopyAddressee(String addressee);

    EmailTemplateBuilder addAttachmentReport(Report report);

    EmailTemplateBuilder setAttachmentReports(Collection<Report> reports);

    EmailTemplateBuilder addAttachmentFile(FileDescriptor fileDescriptor);

    EmailTemplateBuilder addAttachmentFile(File file);

    EmailTemplateBuilder addAttachmentFile(byte[] bytes);

    EmailTemplateBuilder setAttachmentParameter(String key, Object value);

    EmailTemplateBuilder setBodyParameter(String key, Object value);

    EmailTemplateBuilder addAttachmentParameter(String key, Object value);

    EmailTemplateBuilder addBodyParameter(String key, Object value);

    EmailTemplateBuilder setAttachmentParameter(Report report, String key, Object value);

    EmailTemplateBuilder setBodyParameter(Report report, String key, Object value);

    EmailTemplateBuilder addAttachmentParameters(Map<String, Object> params);

    EmailTemplateBuilder addBodyParameters(Map<String, Object> params);

    EmailTemplateBuilder setAttachmentParameters(ReportWithParams reportWithParams);

    EmailTemplateBuilder setBodyParameters(ReportWithParams reportWithParams);

    EmailTemplateBuilder setAttachmentParameters(Map<String, Object> params);

    EmailTemplateBuilder setBodyParameters(Map<String, Object> params);

    EmailTemplateBuilder setAttachmentParameters(Collection<ReportWithParams> reportsWithParams);

    EmailInfo generateEmail() throws ReportParameterTypeChangedException, TemplateNotFoundException;

    EmailTemplate build();

    void sendEmail() throws TemplateNotFoundException, ReportParameterTypeChangedException, EmailException;

    void sendEmail(boolean async) throws TemplateNotFoundException, ReportParameterTypeChangedException, EmailException;

    void sendEmailAsync() throws TemplateNotFoundException, ReportParameterTypeChangedException;
}
