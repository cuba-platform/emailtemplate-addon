package com.haulmont.addon.yargemailtemplateaddon.core.emailtemplateapi;

import com.haulmont.addon.yargemailtemplateaddon.dto.ReportWithParams;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component(EmailTemplateAPI.NAME)
public class EmailTemplateAPIImpl implements EmailTemplateAPI {

    @Inject
    protected ReportingApi reportingApi;

    @Override
    public EmailInfo generateEmail(LayoutEmailTemplate layoutTemplate, List<ReportWithParams> params) {
        EmailInfo emailInfo = generateEmailInfoByLayoutTemplate(params.get(0));
        if (layoutTemplate instanceof ContentEmailTemplate && params.size() > 1) {
            EmailAttachment[] emailAttachments = createEmailAttachments(params.subList(1, params.size()));
            emailInfo.setAttachments(emailAttachments);
        }
        return emailInfo;
    }

    @Override
    public EmailInfo generateEmail(ContentEmailTemplate contentTemplate, List<ReportWithParams> params) {
        EmailInfo emailInfo = generateEmailInfoByLayoutTemplate(params.get(0));
        if (params.size() > 1) {
            EmailAttachment[] emailAttachments = createEmailAttachments(params.subList(1, params.size()));
            emailInfo.setAttachments(emailAttachments);
        }
        return emailInfo;
    }

    @Override
    public EmailInfo generateEmail(LayoutEmailTemplate layoutTemplate, String caption, String content) {
        ReportWithParams reportWithParams = new ReportWithParams(layoutTemplate.getReport());
        reportWithParams.setParams(ParamsMap.of("content", content));
        EmailInfo emailInfo = generateEmailInfoByLayoutTemplate(reportWithParams);
        emailInfo.setCaption(caption);
        return emailInfo;
    }

    protected EmailInfo generateEmailInfoByLayoutTemplate(ReportWithParams reportWithParams) {
        ReportOutputDocument outputDocument = reportingApi.createReport(
                reportWithParams.getReport(),
                reportWithParams.getParams());
        String body = new String(outputDocument.getContent());
        return new EmailInfo(null, outputDocument.getDocumentName(), body, EmailInfo.HTML_CONTENT_TYPE);
    }

    protected EmailAttachment[] createEmailAttachments(List<ReportWithParams> reportsWithParams) {
        EmailAttachment[] emailAttachments = null;

        if (CollectionUtils.isNotEmpty(reportsWithParams)) {
            List<EmailAttachment> attachmentsList =
                    reportsWithParams.stream().map(reportsWithParam -> createEmailAttachmentByReportAndParams(
                            reportsWithParam.getReport(), reportsWithParam.getParams())).collect(Collectors.toList());
            emailAttachments = attachmentsList.toArray(new EmailAttachment[reportsWithParams.size()]);
        }
        return emailAttachments;
    }

    protected EmailAttachment createEmailAttachmentByReportAndParams(Report report, Map<String, Object> params) {
        ReportOutputDocument outputDocument = reportingApi.createReport(report, params);
        return new EmailAttachment(outputDocument.getContent(), outputDocument.getDocumentName());
    }
}
