package com.haulmont.addon.emailtemplates.service;


import com.haulmont.addon.emailtemplates.dto.ReportWithParams;
import com.haulmont.addon.emailtemplates.entity.EmailTemplate;
import com.haulmont.addon.emailtemplates.entity.ParameterValue;
import com.haulmont.addon.emailtemplates.exceptions.ReportParameterTypeChangedException;
import com.haulmont.reports.entity.Report;

import java.util.List;

public interface TemplateParametersExtractorService {

    String NAME = "emailtemplates_TemplateParametersExtractorService";

    List<ReportWithParams> getTemplateDefaultValues(EmailTemplate emailTemplate)
            throws ReportParameterTypeChangedException;

    ReportWithParams getReportDefaultValues(Report report, List<ParameterValue> parameterValues)
            throws ReportParameterTypeChangedException;
}