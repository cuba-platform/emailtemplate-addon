package com.haulmont.addon.emailtemplates.service;

import com.haulmont.addon.emailtemplates.core.EmailTemplateAPI;
import com.haulmont.addon.emailtemplates.dto.ReportWithParams;
import com.haulmont.addon.emailtemplates.entity.EmailTemplate;
import com.haulmont.addon.emailtemplates.exceptions.TemplatesAreNotFoundException;
import com.haulmont.cuba.core.global.EmailInfo;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service(OutboundTemplateService.NAME)
public class OutboundTemplateServiceBean implements OutboundTemplateService {

    @Inject
    protected EmailTemplateAPI emailTemplateAPI;


    @Override
    public EmailInfo generateEmail(EmailTemplate emailTemplate, Map<String, Object> params) throws TemplatesAreNotFoundException {
        return emailTemplateAPI.generateEmail(emailTemplate, params);
    }

    @Override
    public EmailInfo generateEmail(EmailTemplate emailTemplate, List<ReportWithParams> params) throws TemplatesAreNotFoundException {
        return emailTemplateAPI.generateEmail(emailTemplate, params);
    }

}