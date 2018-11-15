package com.haulmont.addon.emailtemplates.core;

import com.haulmont.addon.emailtemplates.bean.TemplateParametersExtractor;
import com.haulmont.addon.emailtemplates.builder.EmailTemplateBuilder;
import com.haulmont.addon.emailtemplates.builder.EmailTemplateBuilderImpl;
import com.haulmont.addon.emailtemplates.dto.ReportWithParams;
import com.haulmont.addon.emailtemplates.entity.EmailTemplate;
import com.haulmont.addon.emailtemplates.entity.ParameterValue;
import com.haulmont.addon.emailtemplates.entity.TemplateReport;
import com.haulmont.addon.emailtemplates.exceptions.ReportParameterTypeChangedException;
import com.haulmont.addon.emailtemplates.exceptions.TemplateNotFoundException;
import com.haulmont.cuba.core.app.FileStorageAPI;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.*;
import com.haulmont.reports.ReportingApi;
import com.haulmont.reports.entity.Report;
import com.haulmont.reports.entity.ReportInputParameter;
import com.haulmont.yarg.reporting.ReportOutputDocument;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

@Component(EmailTemplatesAPI.NAME)
public class EmailTemplates implements EmailTemplatesAPI {

    private static final Logger LOG = LoggerFactory.getLogger(EmailTemplates.class);

    @Inject
    private DataManager dataManager;
    @Inject
    protected ReportingApi reportingApi;
    @Inject
    private Messages messages;
    @Inject
    private FileStorageAPI fileStorageAPI;
    @Inject
    private TemplateParametersExtractor parametersExtractor;

    @Override
    public EmailInfo generateEmail(EmailTemplate emailTemplate, Collection<ReportWithParams> params) throws TemplateNotFoundException, ReportParameterTypeChangedException {
        if (emailTemplate == null) {
            throw new TemplateNotFoundException(messages.getMessage(EmailTemplates.class, "nullTemplate"));
        }
        if (bodyAndAttachmentsIsEmpty(emailTemplate)) {
            throw new TemplateNotFoundException(messages.getMessage(EmailTemplates.class, "voidTemplate"));
        }
        List<ReportWithParams> parameters = new ArrayList<>(params);

        TemplateReport bodyReport = emailTemplate.getEmailBodyReport();
        ReportWithParams bodyReportWithParams = getReportWithParams(bodyReport, parameters);

        List<ReportWithParams> attachmentsWithParams = new ArrayList<>();
        for (TemplateReport report : emailTemplate.getAttachedTemplateReports()) {
            ReportWithParams reportWithParams = getReportWithParams(report, parameters);
            attachmentsWithParams.add(reportWithParams);
        }
        EmailInfo emailInfo = generateEmailInfoWithoutAttachments(bodyReportWithParams);
        List<EmailAttachment> templateAttachments = new ArrayList<>();
        templateAttachments.addAll(createReportAttachments(attachmentsWithParams));
        templateAttachments.addAll(createFilesAttachments(emailTemplate.getAttachedFiles()));
        EmailAttachment[] emailAttachments = templateAttachments
                .toArray(new EmailAttachment[attachmentsWithParams.size()]);
        emailInfo.setCaption(Boolean.TRUE.equals(emailTemplate.getUseReportSubject()) ?
                emailInfo.getCaption() : emailTemplate.getSubject());
        emailInfo.setAddresses(emailTemplate.getTo());
        emailInfo.setFrom(emailTemplate.getFrom());
        emailInfo.setAttachments(emailAttachments);
        return emailInfo;
    }

    private ReportWithParams getReportWithParams(TemplateReport templateReport, List<ReportWithParams> parameters) throws ReportParameterTypeChangedException {
        ReportWithParams bodyReportWithParams = parametersExtractor.getReportDefaultValues(templateReport.getReport(),
                templateReport.getParameterValues());
        ReportWithParams bodyReportExternalParams = parameters.stream()
                .filter(e -> e.getReport().equals(templateReport))
                .findFirst()
                .orElse(null);
        if (bodyReportExternalParams != null) {
            for (String key : bodyReportExternalParams.getParams().keySet()) {
                bodyReportWithParams.put(key, bodyReportExternalParams.getParams().get(key));
            }
        }
        return bodyReportWithParams;
    }

    private List<EmailAttachment> createFilesAttachments(List<FileDescriptor> attachedFiles) {
        List<EmailAttachment> attachmentsList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(attachedFiles)) {
            attachmentsList = attachedFiles.stream()
                    .map(this::createEmailAttachmentByFileDescriptor)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

        }
        return attachmentsList;
    }

    private EmailAttachment createEmailAttachmentByFileDescriptor(FileDescriptor fd) {
        try {
            return new EmailAttachment(fileStorageAPI.loadFile(fd), fd.getName(), fd.getName());
        } catch (FileStorageException e) {
            LOG.error("Could not load file from storage", e);
        }
        return null;
    }

    @Override
    public EmailInfo generateEmail(EmailTemplate emailTemplate, Map<String, Object> params) throws TemplateNotFoundException, ReportParameterTypeChangedException {
        if (emailTemplate == null) {
            throw new TemplateNotFoundException(messages.getMessage(EmailTemplates.class, "nullTemplate"));
        }
        if (bodyAndAttachmentsIsEmpty(emailTemplate)) {
            throw new TemplateNotFoundException(messages.getMessage(EmailTemplates.class, "voidTemplate"));
        }

        List<ReportWithParams> paramList = new ArrayList<>();
        Report bodyReport = emailTemplate.getReport();
        paramList.add(createParamsMapForReport(bodyReport, params));
        for (TemplateReport templateReport : emailTemplate.getAttachedTemplateReports()) {
            paramList.add(createParamsMapForReport(templateReport.getReport(), params));
        }
        return generateEmail(emailTemplate, paramList);
    }

    @Override
    public EmailInfo generateEmail(String emailTemplateCode, Map<String, Object> params) throws TemplateNotFoundException, ReportParameterTypeChangedException {
        EmailTemplate emailTemplate = getEmailTemplateByCode(emailTemplateCode);
        return generateEmail(emailTemplate, params);
    }

    @Override
    public EmailInfo generateEmail(String emailTemplateCode, Collection<ReportWithParams> params) throws TemplateNotFoundException, ReportParameterTypeChangedException {
        EmailTemplate emailTemplate = getEmailTemplateByCode(emailTemplateCode);
        return generateEmail(emailTemplate, params);
    }

    protected EmailTemplate getEmailTemplateByCode(String emailTemplateCode) throws TemplateNotFoundException {
        LoadContext<EmailTemplate> loadContext = LoadContext.create(EmailTemplate.class)
                .setQuery(LoadContext.createQuery("select e from emailtemplates$EmailTemplate e where e.code = :code")
                        .setParameter("code", emailTemplateCode));
        EmailTemplate emailTemplate = dataManager.reload(dataManager.load(loadContext), "emailTemplate-view");

        if (emailTemplate == null) {
            throw new TemplateNotFoundException(messages.formatMessage(EmailTemplates.class, "notFoundTemplate", emailTemplateCode));
        }
        return emailTemplate;
    }

    @Override
    public void checkParameterTypeChanged(ReportInputParameter inputParameter, ParameterValue parameterValue)
            throws ReportParameterTypeChangedException {
        if (!Objects.equals(inputParameter.getType(), parameterValue.getParameterType())) {
            throw new ReportParameterTypeChangedException(
                    messages.formatMessage(EmailTemplates.class, "parameterTypeChanged",
                            inputParameter.getReport().getName(), inputParameter.getAlias()));
        }
    }

    @Override
    public EmailTemplateBuilder buildFromTemplate(EmailTemplate emailTemplate) {
        return new EmailTemplateBuilderImpl(emailTemplate);
    }

    @Override
    public EmailTemplateBuilder buildFromTemplate(String code) throws TemplateNotFoundException {
        return new EmailTemplateBuilderImpl(getEmailTemplateByCode(code));
    }

    protected EmailInfo generateEmailInfoWithoutAttachments(ReportWithParams reportWithParams) {
        String body = null;
        String caption = null;
        if (reportWithParams.getReport() != null) {
            ReportOutputDocument outputDocument = reportingApi.createReport(
                    reportWithParams.getReport(),
                    reportWithParams.getParams());
            body = new String(outputDocument.getContent());
            caption = outputDocument.getDocumentName();
        }
        return new EmailInfo(null, caption, body, EmailInfo.HTML_CONTENT_TYPE);
    }

    protected List<EmailAttachment> createReportAttachments(List<ReportWithParams> reportsWithParams) {
        List<EmailAttachment> attachmentsList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(reportsWithParams)) {
            attachmentsList = reportsWithParams.stream()
                    .map(reportsWithParam ->
                            createEmailAttachmentByReportAndParams(
                                    reportsWithParam.getReport(), reportsWithParam.getParams()))
                    .collect(Collectors.toList());

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
        Report body = emailTemplate.getReport();
        List<TemplateReport> attachments = emailTemplate.getAttachedTemplateReports();
        return body == null && attachments.isEmpty();
    }
}
