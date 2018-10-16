package com.haulmont.addon.emailtemplates.core;

import com.haulmont.addon.emailtemplates.dto.EmailTemplateBuilder;
import com.haulmont.addon.emailtemplates.dto.ReportWithParams;
import com.haulmont.addon.emailtemplates.entity.EmailTemplate;
import com.haulmont.addon.emailtemplates.entity.ParameterValue;
import com.haulmont.addon.emailtemplates.exceptions.ReportParameterTypeChangedException;
import com.haulmont.addon.emailtemplates.exceptions.TemplateNotFoundException;
import com.haulmont.cuba.core.global.EmailAttachment;
import com.haulmont.cuba.core.global.EmailInfo;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.reports.ReportingApi;
import com.haulmont.reports.entity.Report;
import com.haulmont.reports.entity.ReportInputParameter;
import com.haulmont.yarg.reporting.ReportOutputDocument;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

@Component(EmailTemplatesAPI.NAME)
public class EmailTemplates implements EmailTemplatesAPI {

    @Inject
    protected ReportingApi reportingApi;
    @Inject
    private Messages messages;

    @Override
    public EmailInfo generateEmail(EmailTemplate emailTemplate, Collection<ReportWithParams> params) throws TemplateNotFoundException {
        if (emailTemplate == null) {
            throw new TemplateNotFoundException(messages.getMessage(EmailTemplates.class, "nullTemplate"));
        }
        if (bodyAndAttachmentsIsEmpty(emailTemplate)) {
            throw new TemplateNotFoundException(messages.getMessage(EmailTemplates.class,"voidTemplate"));
        }
        List<ReportWithParams> parameters = new ArrayList<>(params);

        Report bodyReport = emailTemplate.getEmailBody();
        ReportWithParams bodyReportWithParams = parameters.stream()
                .filter(e -> e.getReport().equals(bodyReport))
                .findFirst()
                .orElse(new ReportWithParams(bodyReport));
        parameters.remove(bodyReportWithParams);
        List<ReportWithParams> attachmentsWithParams = new ArrayList<>();
        for (Report report : emailTemplate.getAttachments()) {
            ReportWithParams reportWithParams = parameters.stream()
                    .filter(e -> e.getReport().equals(report))
                    .findFirst()
                    .orElse(new ReportWithParams(report));
            parameters.remove(bodyReportWithParams);
            attachmentsWithParams.add(reportWithParams);
        }
        EmailInfo emailInfo = generateEmailInfoWithoutAttachments(bodyReportWithParams);
        EmailAttachment[] emailAttachments = createEmailAttachments(attachmentsWithParams).toArray(new EmailAttachment[attachmentsWithParams.size()]);
        emailInfo.setCaption(emailTemplate.getSubject());
        emailInfo.setAttachments(emailAttachments);
        return emailInfo;
    }

    @Override
    public EmailInfo generateEmail(EmailTemplate emailTemplate, Map<String, Object> params) throws TemplateNotFoundException {
        if (emailTemplate == null) {
            throw new TemplateNotFoundException(messages.getMessage(EmailTemplates.class, "nullTemplate"));
        }
        if (bodyAndAttachmentsIsEmpty(emailTemplate)) {
            throw new TemplateNotFoundException(messages.getMessage(EmailTemplates.class, "voidTemplate"));
        }

        List<ReportWithParams> paramList = new ArrayList<>();
        Report bodyReport = emailTemplate.getEmailBody();
        paramList.add(createParamsMapForReport(bodyReport, params));
        for (Report report : emailTemplate.getAttachments()) {
            paramList.add(createParamsMapForReport(report, params));
        }
        return generateEmail(emailTemplate, paramList);
    }

    @Override
    public void checkParameterTypeChanged(ReportInputParameter inputParameter, ParameterValue parameterValue)
            throws ReportParameterTypeChangedException {
        if (! Objects.equals(inputParameter.getType(), parameterValue.getParameterType())) {
            throw new ReportParameterTypeChangedException();
        }
    }

    @Override
    public EmailTemplateBuilder buildFromTemplate(EmailTemplate emailTemplate) {
        return null;
    }

    @Override
    public EmailTemplateBuilder buildFromTemplate(String code) {
        return null;
    }

    protected EmailInfo generateEmailInfoWithoutAttachments(ReportWithParams reportWithParams) {
        String body = null;
        if (reportWithParams.getReport() != null) {
            ReportOutputDocument outputDocument = reportingApi.createReport(
                    reportWithParams.getReport(),
                    reportWithParams.getParams());
            body = new String(outputDocument.getContent());
        }
        return new EmailInfo(null, null, body, EmailInfo.HTML_CONTENT_TYPE);
    }

    protected List<EmailAttachment> createEmailAttachments(List<ReportWithParams> reportsWithParams) {
        List<EmailAttachment> attachmentsList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(reportsWithParams)) {
            attachmentsList =
                    reportsWithParams.stream()
                            .map(reportsWithParam ->
                                    createEmailAttachmentByReportAndParams(
                            reportsWithParam.getReport(), reportsWithParam.getParams())).collect(Collectors.toList());

        }
        return attachmentsList;
    }

    protected EmailAttachment createEmailAttachmentByReportAndParams(Report report, Map<String, Object> params) {
        ReportOutputDocument outputDocument = reportingApi.createReport(report, params);
        return new EmailAttachment(outputDocument.getContent(), outputDocument.getDocumentName());
    }

    protected ReportWithParams createParamsMapForReport(Report report, Map<String, Object> params) {
        ReportWithParams reportWithParams = new ReportWithParams(report);
        if (MapUtils.isNotEmpty(params)) {
            Map<String, Object> paramsMap = new HashMap<>();
            for (ReportInputParameter parameter : report.getInputParameters()) {
                paramsMap.put(parameter.getAlias(), params.get(parameter.getAlias()));
            }
            reportWithParams.setParams(paramsMap);
        }
        return reportWithParams;
    }

    protected boolean bodyAndAttachmentsIsEmpty(EmailTemplate emailTemplate) {
        Report body = emailTemplate.getEmailBody();
        List<Report> attachments = emailTemplate.getAttachments();
        Report notNullReport = null;
        if (attachments != null) {
            notNullReport = attachments.stream()
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);
        }
        return body == null && notNullReport == null;
    }
}
