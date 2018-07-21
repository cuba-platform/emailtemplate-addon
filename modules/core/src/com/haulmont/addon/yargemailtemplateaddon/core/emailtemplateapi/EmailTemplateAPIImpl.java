package com.haulmont.addon.yargemailtemplateaddon.core.emailtemplateapi;

import com.haulmont.addon.yargemailtemplateaddon.dto.ReportWithParams;
import com.haulmont.addon.yargemailtemplateaddon.entity.ContentEmailTemplate;
import com.haulmont.addon.yargemailtemplateaddon.entity.LayoutEmailTemplate;
import com.haulmont.addon.yargemailtemplateaddon.exceptions.TemplatesIsNotFoundException;
import com.haulmont.cuba.core.global.EmailAttachment;
import com.haulmont.cuba.core.global.EmailInfo;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.reports.ReportingApi;
import com.haulmont.reports.entity.Report;
import com.haulmont.reports.entity.ReportInputParameter;
import com.haulmont.yarg.reporting.ReportOutputDocument;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component(EmailTemplateAPI.NAME)
public class EmailTemplateAPIImpl implements EmailTemplateAPI {

    @Inject
    protected ReportingApi reportingApi;
    @Inject
    private Messages messages;

    @Override
    public EmailInfo generateEmail(LayoutEmailTemplate layoutTemplate, ContentEmailTemplate contentTemplate, List<ReportWithParams> params) throws TemplatesIsNotFoundException {
        ReportWithParams layoutReportWithParams = null;
        if (layoutTemplate != null) {
            Report layoutReport = layoutTemplate.getReport();
            layoutReportWithParams = params.stream().filter(e -> e.getReport().equals(layoutReport)).findFirst().orElse(new ReportWithParams(layoutReport));
        }

        ReportWithParams contentReportWithParams = null;
        String body = null;
        EmailAttachment[] emailAttachments = null;
        if (contentTemplate != null) {
            Report contentReport = contentTemplate.getReport();
            contentReportWithParams = params.stream().filter(e -> e.getReport().equals(contentReport)).findFirst().orElse(new ReportWithParams(contentReport));
            List<ReportWithParams> attachmentsWithParams = new ArrayList<>();
            for (Report report : contentTemplate.getAttachments()) {
                ReportWithParams reportWithParams = params.stream().filter(e -> e.getReport().equals(report)).findFirst().orElse(new ReportWithParams(report));
                attachmentsWithParams.add(reportWithParams);
            }
            emailAttachments = createEmailAttachments(attachmentsWithParams);
            body = getEmailBodyByContent(contentReportWithParams);
        }

        EmailInfo emailInfo = null;
        if (layoutReportWithParams != null) {
            layoutReportWithParams.put("content", body);
            emailInfo = generateEmailInfoByLayoutTemplate(layoutReportWithParams);
        } else if (contentReportWithParams != null) {
            emailInfo = generateEmailInfoByLayoutTemplate(contentReportWithParams);
        } else throw new TemplatesIsNotFoundException(messages.getMessage(getClass(), "voidTemplates"));

        emailInfo.setAttachments(emailAttachments);

        return emailInfo;
    }

    @Override
    public EmailInfo generateEmail(LayoutEmailTemplate layoutTemplate, ContentEmailTemplate contentTemplate, Map<String, Object> params) throws TemplatesIsNotFoundException {
        List<ReportWithParams> paramList = new ArrayList<>();

        Report layoutReport = layoutTemplate.getReport();
        paramList.add(createParamsMapForReport(layoutReport, params));

        if (contentTemplate != null) {
            Report contentReport = contentTemplate.getReport();
            paramList.add(createParamsMapForReport(contentReport, params));
            for (Report report : contentTemplate.getAttachments()) {
                paramList.add(createParamsMapForReport(report, params));
            }
        }
        return generateEmail(layoutTemplate, contentTemplate, paramList);
    }

    @Override
    public EmailInfo generateEmail(ContentEmailTemplate contentTemplate, List<ReportWithParams> params) throws TemplatesIsNotFoundException {
        if (contentTemplate == null) {
            throw new TemplatesIsNotFoundException(messages.getMessage(getClass(), "voidContentTemplate"));
        }
        Report contentReport = contentTemplate.getReport();
        ReportWithParams contentReportWithParams = params.stream().filter(e -> e.getReport().equals(contentReport)).findFirst().orElse(new ReportWithParams(contentReport));
        List<ReportWithParams> attachmentsWithParams = new ArrayList<>();
        for (Report report : contentTemplate.getAttachments()) {
            ReportWithParams reportWithParams = params.stream().filter(e -> e.getReport().equals(report)).findFirst().orElse(new ReportWithParams(report));
            attachmentsWithParams.add(reportWithParams);
        }
        EmailAttachment[] emailAttachments = createEmailAttachments(attachmentsWithParams);

        EmailInfo emailInfo = generateEmailInfoByLayoutTemplate(contentReportWithParams);
        emailInfo.setAttachments(emailAttachments);
        return emailInfo;
    }

    @Override
    public EmailInfo generateEmail(LayoutEmailTemplate layoutTemplate, String caption, String content) throws TemplatesIsNotFoundException {
        if (layoutTemplate == null) {
            throw new TemplatesIsNotFoundException(messages.getMessage(getClass(), "voidLayoutTemplate"));
        }
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
        for (ReportInputParameter parameter : report.getInputParameters()) {
            paramsMap.put(parameter.getAlias(), params.get(parameter.getAlias()));
        }
        ReportWithParams reportWithParams = new ReportWithParams(report);
        reportWithParams.setParams(paramsMap);
        return reportWithParams;
    }
}
