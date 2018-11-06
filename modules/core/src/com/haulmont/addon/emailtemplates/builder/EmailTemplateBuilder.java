package com.haulmont.addon.emailtemplates.builder;

import com.haulmont.addon.emailtemplates.core.EmailTemplatesAPI;
import com.haulmont.addon.emailtemplates.dto.ReportWithParams;
import com.haulmont.addon.emailtemplates.entity.EmailTemplate;
import com.haulmont.addon.emailtemplates.entity.ParameterValue;
import com.haulmont.addon.emailtemplates.entity.TemplateReport;
import com.haulmont.addon.emailtemplates.exceptions.ReportParameterTypeChangedException;
import com.haulmont.addon.emailtemplates.exceptions.TemplateNotFoundException;
import com.haulmont.cuba.core.app.EmailService;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.EmailException;
import com.haulmont.cuba.core.global.EmailInfo;
import com.haulmont.reports.entity.Report;

import java.util.Collection;
import java.util.List;
import java.util.Map;
/**
 * That interface provides abilities of builder pattern to create filled email template {@link EmailTemplate}.
 * There are a lot of 'set' and 'add' intermediate methods and five terminal methods.
 * The implementation must contain copy of email template that have to filled by terminal methods.
 */
public interface EmailTemplateBuilder {

    /**
     * That terminal method fills subject property of email template {@link EmailTemplate}.
     *
     * @param subject {@link String}
     * @return {@link EmailTemplateBuilder}
     */
    EmailTemplateBuilder setSubject(String subject);
    /**
     * That terminal method fills 'from' property of email template {@link EmailTemplate}.
     *
     * @param from {@link String}
     * @return {@link EmailTemplateBuilder}
     */
    EmailTemplateBuilder setFrom(String from);
    /**
     * That terminal method fills 'to' property of email template {@link EmailTemplate}.
     * Addresses is added to already existed by the concatenation with ',' symbol.
     *
     * @param to {@link String}
     * @return {@link EmailTemplateBuilder}
     */
    EmailTemplateBuilder addTo(String to);
    /**
     * That terminal method fills 'to' property of email template {@link EmailTemplate}.
     *
     * @param to {@link String}
     * @return {@link EmailTemplateBuilder}
     */
    EmailTemplateBuilder setTo(String to);
    /**
     * That terminal method fills 'cc' property of email template {@link EmailTemplate}.
     * Addresses is added to already existed by the concatenation with ',' symbol.
     *
     * @param cc {@link String}
     * @return {@link EmailTemplateBuilder}
     */
    EmailTemplateBuilder addCc(String cc);
    /**
     * That terminal method fills 'cc' property of email template {@link EmailTemplate}.
     *
     * @param cc {@link String}
     * @return {@link EmailTemplateBuilder}
     */
    EmailTemplateBuilder setCc(String cc);
    /**
     * That terminal method fills 'bcc' property of email template {@link EmailTemplate}.
     * Addresses is added to already existed by the concatenation with ',' symbol.
     *
     * @param bcc {@link String}
     * @return {@link EmailTemplateBuilder}
     */
    EmailTemplateBuilder addBcc(String bcc);
    /**
     * That terminal method fills 'bcc' property of email template {@link EmailTemplate}.
     *
     * @param bcc {@link String}
     * @return {@link EmailTemplateBuilder}
     */
    EmailTemplateBuilder setBcc(String bcc);
    /**
     * That terminal method fills 'attachedTemplateReports' property of email template {@link EmailTemplate}
     * by one {@link TemplateReport} for that report.
     *
     * @param report {@link Report}
     * @return {@link EmailTemplateBuilder}
     */
    EmailTemplateBuilder addAttachmentReport(Report report);
    /**
     * That terminal method fills 'attachedTemplateReports' property of email template {@link EmailTemplate}
     * by collection of {@link TemplateReport} for each reports.
     *
     * @param reports {@link Collection<Report>}
     * @return {@link EmailTemplateBuilder}
     */
    EmailTemplateBuilder setAttachmentReports(Collection<Report> reports);
    /**
     * That terminal method fills 'attachedFiles' property of email template {@link EmailTemplate}.
     *
     * @param file {@link FileDescriptor}
     * @return {@link EmailTemplateBuilder}
     */
    EmailTemplateBuilder addAttachmentFile(FileDescriptor file);
    /**
     * That terminal method fills 'attachedFiles' property of email template {@link EmailTemplate}.
     *
     * @param files {@link List<FileDescriptor>}
     * @return {@link EmailTemplateBuilder}
     */
    EmailTemplateBuilder setAttachmentFiles(List<FileDescriptor> files);
    /**
     * That terminal method fills {@link TemplateReport} for child entity of email template {@link EmailTemplate}.
     * If there are no reports with parameter with alias 'key', do nothing.
     *
     * @param key {@link String} is alias property of {@link ParameterValue}
     * @param value {@link Object} will be converted to defaultValue property of {@link ParameterValue}
     * @return {@link EmailTemplateBuilder}
     */
    EmailTemplateBuilder setBodyParameter(String key, Object value);
    /**
     * That terminal method fills {@link TemplateReport} of child entity of email template {@link EmailTemplate}.
     * If there are no reports with parameter with aliases that contained in key set, do nothing.
     *
     * @param params {@link Map}
     * @return {@link EmailTemplateBuilder}
     */
    EmailTemplateBuilder setBodyParameters(Map<String, Object> params);
    /**
     * That terminal method fills 'attachedTemplateReports' property of email template {@link EmailTemplate}.
     * Method removes all {@link TemplateReport} with same report if they exist.
     *
     * @param key {@link String} is alias property of {@link ParameterValue}
     * @param value {@link Object} will be converted to defaultValue property of {@link ParameterValue}
     * @return {@link EmailTemplateBuilder}
     */
    EmailTemplateBuilder setAttachmentParameter(Report report, String key, Object value);
    /**
     * That terminal method fills 'attachedTemplateReports' property of email template {@link EmailTemplate}.
     * Method removes all {@link TemplateReport} with same report if they exist.
     *
     * @param reportWithParams {@link ReportWithParams}
     * @return {@link EmailTemplateBuilder}
     */
    EmailTemplateBuilder setAttachmentParameters(ReportWithParams reportWithParams);
    /**
     * That terminal method fills 'attachedTemplateReports' property of email template {@link EmailTemplate}.
     * Method removes all {@link TemplateReport} with same report if they exist.
     *
     * @param report {@link Report}
     * @param params {@link Map}
     * @return {@link EmailTemplateBuilder}
     */
    EmailTemplateBuilder setAttachmentParameters(Report report, Map<String, Object> params);
    /**
     * That terminal method fills 'attachedTemplateReports' property of email template {@link EmailTemplate}.
     * Method removes all {@link TemplateReport} with same report if they exist.
     *
     * @param reportsWithParams {@link Collection<ReportWithParams>}
     * @return {@link EmailTemplateBuilder}
     */
    EmailTemplateBuilder setAttachmentParameters(Collection<ReportWithParams> reportsWithParams);
    /**
     * That intermediate method creates {@link EmailInfo} by filled email template using {@link EmailTemplatesAPI}.
     *
     * @return {@link EmailInfo} from cuba emailer
     * @throws ReportParameterTypeChangedException If parameter type of inputParameter does not equal to type saved in parameterValue.
     * @throws TemplateNotFoundException If emailTemplate does not contain reports or null.
     */
    EmailInfo generateEmail() throws ReportParameterTypeChangedException, TemplateNotFoundException;
    /**
     * That intermediate method creates copy of filled {@link EmailTemplate}
     *
     * @return {@link EmailTemplate} from cuba emailer
     */
    EmailTemplate build();
    /**
     * That intermediate method sends filled email template {@link EmailTemplate} using {@link EmailService}.
     *
     * @throws ReportParameterTypeChangedException If parameter type of inputParameter does not equal to type saved in parameterValue.
     * @throws TemplateNotFoundException If emailTemplate does not contain reports or null.
     */
    void sendEmail() throws TemplateNotFoundException, ReportParameterTypeChangedException, EmailException;
    /**
     * That intermediate method sends filled email template {@link EmailTemplate} using {@link EmailService}.
     *
     * @param async Provides choice of selecting asynchronous option.
     * @throws ReportParameterTypeChangedException If parameter type of inputParameter does not equal to type saved in parameterValue.
     * @throws TemplateNotFoundException If emailTemplate does not contain reports or null.
     */
    void sendEmail(boolean async) throws TemplateNotFoundException, ReportParameterTypeChangedException, EmailException;
    /**
     * That intermediate method asynchronously sends filled email template {@link EmailTemplate} using {@link EmailService}.
     *
     * @throws ReportParameterTypeChangedException If parameter type of inputParameter does not equal to type saved in parameterValue.
     * @throws TemplateNotFoundException If emailTemplate does not contain reports or null.
     */
    void sendEmailAsync() throws TemplateNotFoundException, ReportParameterTypeChangedException;
}
