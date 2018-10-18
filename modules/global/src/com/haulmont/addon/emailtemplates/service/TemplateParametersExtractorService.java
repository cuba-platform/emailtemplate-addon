package com.haulmont.addon.emailtemplates.service;


import com.haulmont.addon.emailtemplates.dto.ReportWithParams;
import com.haulmont.addon.emailtemplates.entity.EmailTemplate;
import com.haulmont.addon.emailtemplates.entity.TemplateParameter;
import com.haulmont.addon.emailtemplates.exceptions.ReportParameterTypeChangedException;

import java.util.List;

public interface TemplateParametersExtractorService {

    String NAME = "emailtemplates_TemplateParametersExtractorService";

    List<ReportWithParams> getParamsFromTemplateDefaultValues(EmailTemplate emailTemplate, List<TemplateParameter> defaultParams)
            throws ReportParameterTypeChangedException;

    List<ReportWithParams> getParamsFromTemplateDefaultValues(EmailTemplate emailTemplate)
            throws ReportParameterTypeChangedException;
}