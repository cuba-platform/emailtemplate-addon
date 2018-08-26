package com.haulmont.addon.emailtemplates.service;

import com.haulmont.addon.emailtemplates.core.EmailTemplatesAPI;
import com.haulmont.addon.emailtemplates.dto.ReportWithParams;
import com.haulmont.addon.emailtemplates.entity.EmailTemplate;
import com.haulmont.addon.emailtemplates.exceptions.TemplateNotFoundException;
import com.haulmont.cuba.core.global.EmailInfo;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
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
    public EmailInfo generateEmail(EmailTemplate emailTemplate, List<ReportWithParams> params) throws TemplateNotFoundException {
        return emailTemplatesAPI.generateEmail(emailTemplate, params);
    }

}