package com.haulmont.addon.emailtemplates.service;

import com.haulmont.addon.emailtemplates.bean.TemplateParametersExtractor;
import com.haulmont.addon.emailtemplates.dto.ReportWithParams;
import com.haulmont.addon.emailtemplates.entity.EmailTemplate;
import com.haulmont.addon.emailtemplates.entity.ParameterValue;
import com.haulmont.addon.emailtemplates.exceptions.ReportParameterTypeChangedException;
import com.haulmont.reports.entity.Report;
import com.haulmont.reports.entity.ReportInputParameter;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service(TemplateParametersExtractorService.NAME)
public class TemplateParametersExtractorServiceBean implements TemplateParametersExtractorService {

    @Inject
    private TemplateParametersExtractor templateParametersExtractor;

    @Override
    public List<ReportWithParams> getTemplateDefaultValues(EmailTemplate emailTemplate)
            throws ReportParameterTypeChangedException {
        return templateParametersExtractor.getTemplateDefaultValues(emailTemplate);
    }

    @Override
    public ReportWithParams getReportDefaultValues(Report report, List<ParameterValue> parameterValues)
            throws ReportParameterTypeChangedException {
        return templateParametersExtractor.getReportDefaultValues(report, parameterValues);
    }

    @Override
    public Class resolveClass(ReportInputParameter parameter) {
        return templateParametersExtractor.resolveClass(parameter);
    }


}