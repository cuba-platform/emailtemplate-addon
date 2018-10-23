package com.haulmont.addon.emailtemplates.service;

import com.haulmont.addon.emailtemplates.core.EmailTemplatesAPI;
import com.haulmont.addon.emailtemplates.dto.ReportWithParams;
import com.haulmont.addon.emailtemplates.entity.EmailTemplate;
import com.haulmont.addon.emailtemplates.entity.ParameterValue;
import com.haulmont.addon.emailtemplates.exceptions.ReportParameterTypeChangedException;
import com.haulmont.addon.emailtemplates.exceptions.TemplateNotFoundException;
import com.haulmont.cuba.core.global.EmailInfo;
import com.haulmont.reports.entity.ReportInputParameter;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Map;

@Service(OutboundTemplateService.NAME)
public class OutboundTemplateServiceBean implements OutboundTemplateService {

    @Inject
    protected EmailTemplatesAPI emailTemplatesAPI;


    @Override
    public EmailInfo generateEmail(EmailTemplate emailTemplate, Map<String, Object> params) throws TemplateNotFoundException {
        return emailTemplatesAPI.generateEmail(emailTemplate, params);
    }

    @Override
    public EmailInfo generateEmail(EmailTemplate emailTemplate, Collection<ReportWithParams> params) throws TemplateNotFoundException {
        return emailTemplatesAPI.generateEmail(emailTemplate, params);
    }

    @Override
    public EmailInfo generateEmail(String emailTemplateCode) throws TemplateNotFoundException, ReportParameterTypeChangedException {
        return emailTemplatesAPI.generateEmail(emailTemplateCode);
    }

    @Override
    public EmailInfo generateEmail(String emailTemplateCode, Map<String, Object> params) throws TemplateNotFoundException {
        return emailTemplatesAPI.generateEmail(emailTemplateCode, params);
    }

    @Override
    public EmailInfo generateEmail(String emailTemplateCode, Collection<ReportWithParams> params) throws TemplateNotFoundException {
        return emailTemplatesAPI.generateEmail(emailTemplateCode, params);
    }

    @Override
    public void checkParameterTypeChanged(ReportInputParameter inputParameter, ParameterValue parameterValue)
            throws ReportParameterTypeChangedException {
        emailTemplatesAPI.checkParameterTypeChanged(inputParameter, parameterValue);
    }

}