package com.haulmont.addon.yargemailtemplateaddon.core.emailtemplateapi;

import com.haulmont.addon.yargemailtemplateaddon.dto.ReportWithParams;
import com.haulmont.addon.yargemailtemplateaddon.entity.ContentEmailTemplate;
import com.haulmont.addon.yargemailtemplateaddon.entity.LayoutEmailTemplate;
import com.haulmont.addon.yargemailtemplateaddon.exceptions.TemplatesAreNotFoundException;
import com.haulmont.cuba.core.global.EmailInfo;

import java.util.List;
import java.util.Map;

public interface EmailTemplateAPI {

    String NAME = "yet_EmailTemplateAPI";

    EmailInfo generateEmail(LayoutEmailTemplate layoutTemplate, ContentEmailTemplate contentTemplate, Map<String, Object> params) throws TemplatesAreNotFoundException;

    EmailInfo generateEmail(LayoutEmailTemplate layoutTemplate, ContentEmailTemplate contentTemplate, List<ReportWithParams> params) throws TemplatesAreNotFoundException;

    EmailInfo generateEmail(ContentEmailTemplate contentTemplate, List<ReportWithParams> params) throws TemplatesAreNotFoundException;

    EmailInfo generateEmail(LayoutEmailTemplate layoutTemplate, String caption, String content) throws TemplatesAreNotFoundException;

}
