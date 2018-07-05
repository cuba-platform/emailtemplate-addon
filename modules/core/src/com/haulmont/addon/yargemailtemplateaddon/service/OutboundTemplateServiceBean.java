package com.haulmont.addon.yargemailtemplateaddon.service;

import com.haulmont.addon.yargemailtemplateaddon.core.emailtemplateapi.EmailTemplateAPI;
import com.haulmont.addon.yargemailtemplateaddon.dto.ReportWithParams;
import com.haulmont.addon.yargemailtemplateaddon.entity.ContentEmailTemplate;
import com.haulmont.addon.yargemailtemplateaddon.entity.LayoutEmailTemplate;
import com.haulmont.cuba.core.global.EmailInfo;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service(OutboundTemplateService.NAME)
public class OutboundTemplateServiceBean implements OutboundTemplateService {

    @Inject
    protected EmailTemplateAPI emailTemplateAPI;

    @Override
    public EmailInfo generateEmail(LayoutEmailTemplate layoutTemplate, List<ReportWithParams> params) {
        return emailTemplateAPI.generateEmail(layoutTemplate, params);
    }

    @Override
    public EmailInfo generateEmail(ContentEmailTemplate contentTemplate, List<ReportWithParams> params) {
        return emailTemplateAPI.generateEmail(contentTemplate, params);
    }

    @Override
    public EmailInfo generateEmail(LayoutEmailTemplate layoutTemplate, String caption, String content) {
        return emailTemplateAPI.generateEmail(layoutTemplate, caption, content);
    }
}