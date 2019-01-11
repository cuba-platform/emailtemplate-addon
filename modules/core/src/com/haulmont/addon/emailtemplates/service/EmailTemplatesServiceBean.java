package com.haulmont.addon.emailtemplates.service;

import com.haulmont.addon.emailtemplates.core.EmailTemplatesAPI;
import com.haulmont.addon.emailtemplates.dto.ExtendedEmailInfo;
import com.haulmont.addon.emailtemplates.dto.ReportWithParams;
import com.haulmont.addon.emailtemplates.entity.EmailTemplate;
import com.haulmont.addon.emailtemplates.entity.ParameterValue;
import com.haulmont.addon.emailtemplates.exceptions.ReportParameterTypeChangedException;
import com.haulmont.addon.emailtemplates.exceptions.TemplateNotFoundException;
import com.haulmont.reports.entity.ReportInputParameter;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Map;

@Service(EmailTemplatesService.NAME)
public class EmailTemplatesServiceBean implements EmailTemplatesService {

    @Inject
    protected EmailTemplatesAPI emailTemplatesAPI;


    @Override
    public ExtendedEmailInfo generateEmail(EmailTemplate emailTemplate, Map<String, Object> params)
            throws TemplateNotFoundException, ReportParameterTypeChangedException {
        return emailTemplatesAPI.generateEmail(emailTemplate, params);
    }

    @Override
    public ExtendedEmailInfo generateEmail(EmailTemplate emailTemplate, Collection<ReportWithParams> params)
            throws TemplateNotFoundException, ReportParameterTypeChangedException {
        return emailTemplatesAPI.generateEmail(emailTemplate, params);
    }

    @Override
    public ExtendedEmailInfo generateEmail(String emailTemplateCode, Map<String, Object> params)
            throws TemplateNotFoundException, ReportParameterTypeChangedException {
        return emailTemplatesAPI.generateEmail(emailTemplateCode, params);
    }

    @Override
    public ExtendedEmailInfo generateEmail(String emailTemplateCode, Collection<ReportWithParams> params)
            throws TemplateNotFoundException, ReportParameterTypeChangedException {
        return emailTemplatesAPI.generateEmail(emailTemplateCode, params);
    }

    @Override
    public void checkParameterTypeChanged(ReportInputParameter inputParameter, ParameterValue parameterValue)
            throws ReportParameterTypeChangedException {
        emailTemplatesAPI.checkParameterTypeChanged(inputParameter, parameterValue);
    }

}