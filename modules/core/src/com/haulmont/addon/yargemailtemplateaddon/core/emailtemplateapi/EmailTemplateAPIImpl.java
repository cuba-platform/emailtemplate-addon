package com.haulmont.addon.yargemailtemplateaddon.core.emailtemplateapi;

import com.haulmont.addon.yargemailtemplateaddon.dto.ReportWithParams;
import com.haulmont.addon.yargemailtemplateaddon.entity.ContentEmailTemplate;
import com.haulmont.addon.yargemailtemplateaddon.entity.LayoutEmailTemplate;
import com.haulmont.cuba.core.global.EmailAttachment;
import com.haulmont.cuba.core.global.EmailInfo;
import com.haulmont.reports.ReportingApi;
import com.haulmont.reports.entity.Report;
import com.haulmont.reports.entity.ReportInputParameter;
import com.haulmont.yarg.reporting.ReportOutputDocument;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component(EmailTemplateAPI.NAME)
public class EmailTemplateAPIImpl implements EmailTemplateAPI {

    @Inject
    protected ReportingApi reportingApi;

    @Override
    public EmailInfo generateEmail(LayoutEmailTemplate layoutTemplate, ContentEmailTemplate contentTemplate, List<ReportWithParams> params) {
        Report layoutReport = layoutTemplate.getReport();
        ReportWithParams layoutReportWithParams = params.stream().filter(e -> e.getReport().equals(layoutReport)).findFirst().orElse(null);

        ReportWithParams contentReportWithParams = null;
        if (contentTemplate != null) {
            Report contentReport = contentTemplate.getReport();
            contentReportWithParams = params.stream().filter(e -> e.getReport().equals(contentReport)).findFirst().orElse(null);
        }

        if (layoutReportWithParams != null && contentReportWithParams != null) {
            String body = getEmailBodyByContent(contentReportWithParams);
            layoutReportWithParams.put("content", body);
            params.remove(layoutReportWithParams);
            params.remove(contentReportWithParams);
        }

        EmailInfo emailInfo = generateEmailInfoByLayoutTemplate(layoutReportWithParams);

        if (CollectionUtils.isNotEmpty(params)) {
            EmailAttachment[] emailAttachments = createEmailAttachments(params);
            emailInfo.setAttachments(emailAttachments);
        }
        return emailInfo;
    }

    @Override
    public EmailInfo generateEmail(LayoutEmailTemplate layoutTemplate, ContentEmailTemplate contentTemplate, Map<String, Object> params) {
        Report layoutReport = layoutTemplate.getReport();
        ReportWithParams layoutReportWithParams = createParamsMapForReport(layoutReport, params);

        ReportWithParams contentReportWithParams = null;
        if (contentTemplate != null) {
            Report contentReport = contentTemplate.getReport();
            contentReportWithParams = createParamsMapForReport(contentReport, params);
        }

        if (!layoutReportWithParams.isEmptyParams() && contentReportWithParams != null && !contentReportWithParams.isEmptyParams()) {
            String body = getEmailBodyByContent(contentReportWithParams);
            layoutReportWithParams.put("content", body);
        }

        List<Report> attachments = contentTemplate.getAttachments();
        List<ReportWithParams> withParamsAttachments = attachments.stream().map(a -> createParamsMapForReport(a, params)).collect(Collectors.toList());

        EmailInfo emailInfo = generateEmailInfoByLayoutTemplate(layoutReportWithParams);
        EmailAttachment[] emailAttachments = createEmailAttachments(withParamsAttachments);
        emailInfo.setAttachments(emailAttachments);

        return emailInfo;
    }

    @Override
    public EmailInfo generateEmail(ContentEmailTemplate contentTemplate, List<ReportWithParams> params) {
        Report contentReport = contentTemplate.getReport();
        ReportWithParams contentReportWithParams = params.stream().filter(e -> e.getReport().equals(contentReport)).findFirst().orElse(null);
        params.remove(contentReportWithParams);

        EmailInfo emailInfo = generateEmailInfoByLayoutTemplate(contentReportWithParams);
        if (CollectionUtils.isNotEmpty(params)) {
            EmailAttachment[] emailAttachments = createEmailAttachments(params);
            emailInfo.setAttachments(emailAttachments);
        }
        return emailInfo;
    }

    @Override
    public EmailInfo generateEmail(LayoutEmailTemplate layoutTemplate, String caption, String content) {
        ReportWithParams reportWithParams = new ReportWithParams(layoutTemplate.getReport());
        reportWithParams.put("content", content);
        EmailInfo emailInfo = generateEmailInfoByLayoutTemplate(reportWithParams);
        emailInfo.setCaption(caption);
        return emailInfo;
    }

    protected String getEmailBodyByContent(ReportWithParams reportWithParams) {
        ReportOutputDocument outputDocument = reportingApi.createReport(
                reportWithParams.getReport(),
                reportWithParams.getParams());
        return new String(outputDocument.getContent());
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

    protected ReportWithParams createParamsMapForReport(Report report, Map<String, Object> params) {
        Map<String, Object> paramsMap = new HashMap<>();
        for (ReportInputParameter parameter: report.getInputParameters()) {
            paramsMap.put(parameter.getAlias(), params.get(parameter.getAlias()));
        }
        ReportWithParams reportWithParams = new ReportWithParams(report);
        reportWithParams.setParams(paramsMap);
        return reportWithParams;
    }
}
