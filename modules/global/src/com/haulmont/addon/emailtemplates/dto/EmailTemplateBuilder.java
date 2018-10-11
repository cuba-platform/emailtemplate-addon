package com.haulmont.addon.emailtemplates.dto;

import com.haulmont.addon.emailtemplates.entity.EmailTemplate;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.EmailInfo;
import com.haulmont.reports.entity.Report;

import java.io.File;
import java.util.Collection;
import java.util.Map;

public interface EmailTemplateBuilder {

    EmailTemplateBuilder setSubject(String subject);

    EmailTemplateBuilder setFrom(String address);

    EmailTemplateBuilder addAddressee(String addressee);

    EmailTemplateBuilder setAddressee(String addressee);

    EmailTemplateBuilder setCopyAddressee(String addressee);

    EmailTemplateBuilder setHiddenCopyAddressee(String addressee);

    EmailTemplateBuilder addAttachmentReport(Report report);

    EmailTemplateBuilder setAttachmentReports(Collection<Report> reports);

    EmailTemplateBuilder setAttachmentReportsWithParams(Collection<ReportWithParams> reportsWithParams);

    EmailTemplateBuilder addAttachment(FileDescriptor fileDescriptor);

    EmailTemplateBuilder addAttachment(File file);

    EmailTemplateBuilder addAttachment(byte[] bytes);

    EmailTemplateBuilder setParameter(String key, Object value);

    EmailTemplateBuilder setParameter(Report report, String key, Object value);

    EmailTemplateBuilder addParameters(Map<String, Object> params);

    EmailTemplateBuilder setParameters(ReportWithParams params);

    EmailTemplateBuilder setParameters(Map<String, Object> params);

    EmailTemplateBuilder setParameters(Collection<ReportWithParams> params);

    EmailInfo generateEmail();

    EmailTemplate getTemplate();

    void sendEmail();

    void sendEmail(boolean async);

    void sendEmailAsync();
}
