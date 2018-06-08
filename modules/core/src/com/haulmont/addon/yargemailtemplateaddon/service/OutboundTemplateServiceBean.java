package com.haulmont.addon.yargemailtemplateaddon.service;

import com.haulmont.addon.yargemailtemplateaddon.entity.ContentEmailTemplate;
import com.haulmont.addon.yargemailtemplateaddon.entity.LayoutEmailTemplate;
import com.haulmont.cuba.core.global.EmailAttachment;
import com.haulmont.cuba.core.global.EmailInfo;
import com.haulmont.reports.ReportingApi;
import com.haulmont.reports.entity.Report;
import com.haulmont.yarg.reporting.ReportOutputDocument;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service(OutboundTemplateService.NAME)
public class OutboundTemplateServiceBean implements OutboundTemplateService {

    @Inject
    protected ReportingApi reportingApi;

    @Override
    public EmailInfo generateMessageByTemplate(LayoutEmailTemplate emailTemplate, String addresses, String from, List<Map<String, Object>> param) {
        EmailInfo emailInfo = generateEmailInfoByLayoutTemplate(emailTemplate, addresses, from, param.get(0));
        if (emailTemplate instanceof ContentEmailTemplate) {
            List<Report> attachments = ((ContentEmailTemplate) emailTemplate).getAttachments();
            EmailAttachment[] emailAttachments = createEmailAttachments(attachments, param.subList(1, param.size()-1));
            emailInfo.setAttachments(emailAttachments);
        }
        return emailInfo;
    }

    @Override
    public EmailInfo generateMessageByContentTemplate(ContentEmailTemplate emailTemplate, String addresses, String from, List<Map<String, Object>> param) {
        EmailInfo emailInfo = generateEmailInfoByLayoutTemplate(emailTemplate, addresses, from, param.get(0));
        List<Report> attachments = emailTemplate.getAttachments();
        EmailAttachment[] emailAttachments = createEmailAttachments(attachments, param.subList(1, param.size()-1));
        emailInfo.setAttachments(emailAttachments);
        return emailInfo;
    }

    @Override
    public EmailInfo generateMessageByTemplateWithCustomParams(LayoutEmailTemplate emailTemplate, String addresses, String from, List<Map<String, Object>> param) {
        return generateMessageByTemplate(emailTemplate, addresses, from, param);
    }

    public EmailInfo generateEmailInfoByLayoutTemplate(LayoutEmailTemplate emailTemplate, String addresses, String from, Map<String, Object> param) {
        Report report = emailTemplate.getReport();
        ReportOutputDocument outputDocument = reportingApi.createReport(report, param);
        String body = new String(outputDocument.getContent());
        return new EmailInfo(addresses, outputDocument.getDocumentName(), from, body, null);
    }

    protected EmailAttachment[] createEmailAttachments(List<Report> reportAttachments, List<Map<String, Object>> params) {
        EmailAttachment[] emailAttachments = null;

        if (reportAttachments != null && !reportAttachments.isEmpty()) {
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