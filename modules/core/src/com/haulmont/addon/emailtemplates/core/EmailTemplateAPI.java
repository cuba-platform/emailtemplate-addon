package com.haulmont.addon.emailtemplates.core;

import com.haulmont.addon.emailtemplates.dto.ReportWithParams;
import com.haulmont.addon.emailtemplates.entity.EmailTemplate;
import com.haulmont.addon.emailtemplates.exceptions.TemplatesAreNotFoundException;
import com.haulmont.cuba.core.global.EmailInfo;

import java.util.List;
import java.util.Map;

public interface EmailTemplateAPI {

    String NAME = "yet_EmailTemplateAPI";

    EmailInfo generateEmail(EmailTemplate emailTemplate, Map<String, Object> params) throws TemplatesAreNotFoundException;

    EmailInfo generateEmail(EmailTemplate emailTemplate, List<ReportWithParams> params) throws TemplatesAreNotFoundException;

}
