package com.haulmont.addon.emailtemplates.core;

import com.haulmont.addon.emailtemplates.dto.ReportWithParams;
import com.haulmont.addon.emailtemplates.entity.ContentEmailTemplate;
import com.haulmont.addon.emailtemplates.entity.LayoutEmailTemplate;
import com.haulmont.addon.emailtemplates.exceptions.TemplatesAreNotFoundException;
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
