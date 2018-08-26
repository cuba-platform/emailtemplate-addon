package com.haulmont.addon.emailtemplates.core;

import com.haulmont.addon.emailtemplates.dto.ReportWithParams;
import com.haulmont.addon.emailtemplates.entity.EmailTemplate;
import com.haulmont.addon.emailtemplates.exceptions.TemplateNotFoundException;
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

@Component(EmailTemplatesAPI.NAME)
public class EmailTemplates implements EmailTemplatesAPI {

    @Inject
    protected ReportingApi reportingApi;
    @Inject
    private Messages messages;

    @Override
    public EmailInfo generateEmail(EmailTemplate emailTemplate, List<ReportWithParams> params) throws TemplateNotFoundException {
        if (emailTemplate != null && emailTemplate.getEmailBody() != null) {
            Report bodyReport = emailTemplate.getEmailBody();
            ReportWithParams bodyReportWithParams = params.stream().filter(e -> e.getReport().equals(bodyReport)).findFirst().orElse(new ReportWithParams(bodyReport));
            List<ReportWithParams> attachmentsWithParams = new ArrayList<>();
            for (Report report : emailTemplate.getAttachments()) {
                ReportWithParams reportWithParams = params.stream().filter(e -> e.getReport().equals(report)).findFirst().orElse(new ReportWithParams(report));
                attachmentsWithParams.add(reportWithParams);
            }
            EmailInfo emailInfo = generateEmailInfoWithoutAttachments(bodyReportWithParams);
            EmailAttachment[] emailAttachments = createEmailAttachments(attachmentsWithParams).toArray(new EmailAttachment[attachmentsWithParams.size()]);
            emailInfo.setAttachments(emailAttachments);
            return emailInfo;
        } else throw new TemplateNotFoundException(messages.getMessage(getClass(), "voidTemplate"));
    }

    @Override
    public EmailInfo generateEmail(EmailTemplate emailTemplate, Map<String, Object> params) throws TemplateNotFoundException {
        List<ReportWithParams> paramList = new ArrayList<>();

        if (emailTemplate != null && emailTemplate.getEmailBody() != null) {
            Report bodyReport = emailTemplate.getEmailBody();
            paramList.add(createParamsMapForReport(bodyReport, params));
            for (Report report : emailTemplate.getAttachments()) {
                paramList.add(createParamsMapForReport(report, params));
            }
        } else throw new TemplateNotFoundException(messages.getMessage(getClass(), "voidTemplate"));

        return generateEmail(emailTemplate, paramList);
    }

    protected EmailInfo generateEmailInfoWithoutAttachments(ReportWithParams reportWithParams) {
        ReportOutputDocument outputDocument = reportingApi.createReport(
                reportWithParams.getReport(),
                reportWithParams.getParams());
        String body = new String(outputDocument.getContent());
        return new EmailInfo(null, outputDocument.getDocumentName(), body, EmailInfo.HTML_CONTENT_TYPE);
    }

    protected List<EmailAttachment> createEmailAttachments(List<ReportWithParams> reportsWithParams) {
        List<EmailAttachment> attachmentsList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(reportsWithParams)) {
            attachmentsList =
                    reportsWithParams.stream().map(reportsWithParam -> createEmailAttachmentByReportAndParams(
                            reportsWithParam.getReport(), reportsWithParam.getParams())).collect(Collectors.toList());

        }
        return attachmentsList;
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
