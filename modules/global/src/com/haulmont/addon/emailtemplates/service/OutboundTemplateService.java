package com.haulmont.addon.emailtemplates.service;


import com.haulmont.addon.emailtemplates.dto.ReportWithParams;
import com.haulmont.addon.emailtemplates.entity.EmailTemplate;
import com.haulmont.addon.emailtemplates.entity.ParameterValue;
import com.haulmont.addon.emailtemplates.exceptions.ReportParameterTypeChangedException;
import com.haulmont.addon.emailtemplates.exceptions.TemplateNotFoundException;
import com.haulmont.cuba.core.global.EmailInfo;
import com.haulmont.reports.entity.ReportInputParameter;

import java.util.Collection;
import java.util.Map;

public interface OutboundTemplateService {
    String NAME = "emailtemplates_OutboundTemplateService";


    EmailInfo generateEmail(EmailTemplate emailTemplate, Map<String, Object> params) throws TemplateNotFoundException;

    EmailInfo generateEmail(EmailTemplate emailTemplate, Collection<ReportWithParams> params) throws TemplateNotFoundException;

    EmailInfo generateEmail(String emailTemplateCode) throws TemplateNotFoundException, ReportParameterTypeChangedException;

    EmailInfo generateEmail(String emailTemplateCode, Map<String, Object> params) throws TemplateNotFoundException;

    EmailInfo generateEmail(String emailTemplateCode, Collection<ReportWithParams> params) throws TemplateNotFoundException;

    void checkParameterTypeChanged(ReportInputParameter inputParameter, ParameterValue parameterValue) throws ReportParameterTypeChangedException;

}