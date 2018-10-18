package com.haulmont.addon.emailtemplates.service;

import com.haulmont.addon.emailtemplates.bean.TemplateParametersExtractor;
import com.haulmont.addon.emailtemplates.dto.ReportWithParams;
import com.haulmont.addon.emailtemplates.entity.EmailTemplate;
import com.haulmont.addon.emailtemplates.entity.TemplateParameter;
import com.haulmont.addon.emailtemplates.exceptions.ReportParameterTypeChangedException;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service(TemplateParametersExtractorService.NAME)
public class TemplateParametersExtractorServiceBean implements TemplateParametersExtractorService {

    @Inject
    private TemplateParametersExtractor templateParametersExtractor;

    @Override
    public List<ReportWithParams> getParamsFromTemplateDefaultValues(EmailTemplate emailTemplate, List<TemplateParameter> defaultParams)
            throws ReportParameterTypeChangedException {
        return templateParametersExtractor.getParamsFromTemplateDefaultValues(emailTemplate, defaultParams);
    }

    @Override
    public List<ReportWithParams> getParamsFromTemplateDefaultValues(EmailTemplate emailTemplate) throws ReportParameterTypeChangedException {
        return templateParametersExtractor.getParamsFromTemplateDefaultValues(emailTemplate);
    }
}