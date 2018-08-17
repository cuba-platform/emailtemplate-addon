package com.haulmont.addon.emailtemplates.service;

import com.haulmont.addon.emailtemplates.core.EmailTemplateAPI;
import com.haulmont.addon.emailtemplates.dto.ReportWithParams;
import com.haulmont.addon.emailtemplates.entity.ContentEmailTemplate;
import com.haulmont.addon.emailtemplates.entity.LayoutEmailTemplate;
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
    public EmailInfo generateEmail(LayoutEmailTemplate layoutTemplate, ContentEmailTemplate contentTemplate, Map<String, Object> params) throws TemplatesAreNotFoundException {
        return emailTemplateAPI.generateEmail(layoutTemplate, contentTemplate, params);
    }

    @Override
    public EmailInfo generateEmail(LayoutEmailTemplate layoutTemplate, ContentEmailTemplate contentTemplate, List<ReportWithParams> params) throws TemplatesAreNotFoundException {
        return emailTemplateAPI.generateEmail(layoutTemplate, contentTemplate, params);
    }

    @Override
    public EmailInfo generateEmail(ContentEmailTemplate contentTemplate, List<ReportWithParams> params) throws TemplatesAreNotFoundException {
        return emailTemplateAPI.generateEmail(contentTemplate, params);
    }

    @Override
    public EmailInfo generateEmail(LayoutEmailTemplate layoutTemplate, String caption, String content) throws TemplatesAreNotFoundException {
        return emailTemplateAPI.generateEmail(layoutTemplate, caption, content);
    }
}