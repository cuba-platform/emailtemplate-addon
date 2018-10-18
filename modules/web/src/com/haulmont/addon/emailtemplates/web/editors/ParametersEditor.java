package com.haulmont.addon.emailtemplates.web.editors;

import com.haulmont.addon.emailtemplates.dto.ReportWithParams;
import com.haulmont.addon.emailtemplates.entity.EmailTemplate;
import com.haulmont.addon.emailtemplates.entity.TemplateParameter;
import com.haulmont.addon.emailtemplates.exceptions.ReportParameterTypeChangedException;
import com.haulmont.addon.emailtemplates.service.TemplateParametersExtractorService;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.reports.app.service.ReportService;
import com.haulmont.reports.gui.report.run.ParameterClassResolver;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ParametersEditor<T extends Entity> extends AbstractEditor<T> {

    @Inject
    private TemplateParametersExtractorService templateParametersExtractorService;
    @Inject
    protected ReportService reportService;
    @Inject
    protected ParameterClassResolver classResolver;

    protected List<ReportWithParams> getParamsOrEmptyByDefaultValues(EmailTemplate emailTemplate, final Collection<TemplateParameter> defaultParameters) {
        List<TemplateParameter> defaultParams = new ArrayList<>(defaultParameters);
        List<ReportWithParams> params = new ArrayList<>();
        try {
            params = templateParametersExtractorService.getParamsFromTemplateDefaultValues(emailTemplate, defaultParams);
        } catch (ReportParameterTypeChangedException e) {
            showNotification(e.getMessage());
        }
        return params;
    }

    protected List<ReportWithParams> getValidParamsByDefaultValues(EmailTemplate emailTemplate, final Collection<TemplateParameter> defaultParameters)
            throws ReportParameterTypeChangedException {
        List<TemplateParameter> defaultParams = new ArrayList<>(defaultParameters);
        return templateParametersExtractorService.getParamsFromTemplateDefaultValues(emailTemplate, defaultParams);
    }
}
