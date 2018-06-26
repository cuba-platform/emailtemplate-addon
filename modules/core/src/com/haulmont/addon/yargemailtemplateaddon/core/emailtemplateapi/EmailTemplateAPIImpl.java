package com.haulmont.addon.yargemailtemplateaddon.core.emailtemplateapi;

import com.haulmont.addon.yargemailtemplateaddon.entity.ContentEmailTemplate;
import com.haulmont.addon.yargemailtemplateaddon.entity.LayoutEmailTemplate;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.EmailAttachment;
import com.haulmont.cuba.core.global.EmailInfo;
import com.haulmont.reports.ReportingApi;
import com.haulmont.reports.entity.Report;
import com.haulmont.yarg.reporting.ReportOutputDocument;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component(EmailTemplateAPI.NAME)
public class EmailTemplateAPIImpl implements EmailTemplateAPI {

    @Inject
    protected ReportingApi reportingApi;

    @Override
    public EmailInfo generateEmail(LayoutEmailTemplate layoutTemplate, List<Map<String, Object>> params) {
        EmailInfo emailInfo = generateEmailInfoByLayoutTemplate(layoutTemplate, params.get(0));
        if (layoutTemplate instanceof ContentEmailTemplate && params.size() > 1) {
            List<Report> attachments = ((ContentEmailTemplate) layoutTemplate).getAttachments();
            EmailAttachment[] emailAttachments = createEmailAttachments(attachments, params.subList(1, params.size()));
            emailInfo.setAttachments(emailAttachments);
        }
        return emailInfo;
    }

    @Override
    public EmailInfo generateEmail(ContentEmailTemplate contentTemplate, List<Map<String, Object>> params) {
        EmailInfo emailInfo = generateEmailInfoByLayoutTemplate(contentTemplate, params.get(0));
        if (params.size() > 1) {
            List<Report> attachments = contentTemplate.getAttachments();
            EmailAttachment[] emailAttachments = createEmailAttachments(attachments, params.subList(1, params.size()));
            emailInfo.setAttachments(emailAttachments);
        }
        return emailInfo;
    }

    @Override
    public EmailInfo generateEmail(LayoutEmailTemplate layoutTemplate, String caption, String content) {
        EmailInfo emailInfo = generateEmailInfoByLayoutTemplate(layoutTemplate, ParamsMap.of("content", content));
        emailInfo.setCaption(caption);
        return emailInfo;
    }

    protected EmailInfo generateEmailInfoByLayoutTemplate(LayoutEmailTemplate emailTemplate, Map<String, Object> param) {
        Report report = emailTemplate.getReport();
        ReportOutputDocument outputDocument = reportingApi.createReport(report, param);
        String body = new String(outputDocument.getContent());
        return new EmailInfo(null, outputDocument.getDocumentName(), body, EmailInfo.HTML_CONTENT_TYPE);
    }

    protected EmailAttachment[] createEmailAttachments(List<Report> reportAttachments, List<Map<String, Object>> params) {
        EmailAttachment[] emailAttachments = null;

        if (CollectionUtils.isNotEmpty(reportAttachments)) {
            List<EmailAttachment> attachmentsList =
                    IntStream.range(0, Math.min(reportAttachments.size(), params.size()))
                            .mapToObj(i -> createEmailAttachmentByReportAndParams(reportAttachments.get(i), params.get(i))).collect(Collectors.toList());
            emailAttachments = attachmentsList.toArray(new EmailAttachment[Math.min(reportAttachments.size(), params.size())]);
        }
        return emailAttachments;
    }

    protected EmailAttachment createEmailAttachmentByReportAndParams(Report report, Map<String, Object> params) {
        ReportOutputDocument outputDocument = reportingApi.createReport(report, params);
        return new EmailAttachment(outputDocument.getContent(), outputDocument.getDocumentName());
    }
}
