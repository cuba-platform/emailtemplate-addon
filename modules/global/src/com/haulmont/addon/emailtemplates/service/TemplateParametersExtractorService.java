package com.haulmont.addon.emailtemplates.service;


import com.haulmont.addon.emailtemplates.dto.ReportWithParams;
import com.haulmont.addon.emailtemplates.entity.EmailTemplate;
import com.haulmont.addon.emailtemplates.exceptions.ReportParameterTypeChangedException;

import java.util.List;

public interface TemplateParametersExtractorService {

    String NAME = "emailtemplates_TemplateParametersExtractorService";

    List<ReportWithParams> getParamsFromTemplateDefaultValues(EmailTemplate emailTemplate)
            throws ReportParameterTypeChangedException;
}