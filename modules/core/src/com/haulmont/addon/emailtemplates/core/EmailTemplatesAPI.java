package com.haulmont.addon.emailtemplates.core;

import com.haulmont.addon.emailtemplates.dto.ReportWithParams;
import com.haulmont.addon.emailtemplates.entity.EmailTemplate;
import com.haulmont.addon.emailtemplates.exceptions.TemplateNotFoundException;
import com.haulmont.cuba.core.global.EmailInfo;

import java.util.List;
import java.util.Map;

public interface EmailTemplatesAPI {

    String NAME = "emailtemplates_EmailTemplatesAPI";

    EmailInfo generateEmail(EmailTemplate emailTemplate, Map<String, Object> params) throws TemplateNotFoundException;

    EmailInfo generateEmail(EmailTemplate emailTemplate, List<ReportWithParams> params) throws TemplateNotFoundException;

}
