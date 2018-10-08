package com.haulmont.addon.emailtemplates.web.editors;

import com.haulmont.addon.emailtemplates.dto.ReportWithParams;
import com.haulmont.addon.emailtemplates.entity.ParameterValue;
import com.haulmont.addon.emailtemplates.entity.TemplateParameter;
import com.haulmont.addon.emailtemplates.exceptions.ReportParameterTypeChangedException;
import com.haulmont.addon.emailtemplates.service.OutboundTemplateService;
import com.haulmont.addon.emailtemplates.web.emailtemplate.EmailTemplateEdit;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.reports.app.service.ReportService;
import com.haulmont.reports.entity.Report;
import com.haulmont.reports.entity.ReportInputParameter;
import com.haulmont.reports.gui.report.run.ParameterClassResolver;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ParametersEditor<T extends Entity> extends AbstractEditor<T> {

    @Inject
    private OutboundTemplateService outboundTemplateService;
    @Inject
    protected ReportService reportService;
    @Inject
    protected ParameterClassResolver classResolver;

    protected void fillParamsByDefaultValues(List<ReportWithParams> parameters, final Collection<TemplateParameter> defaultParameters) {
        List<TemplateParameter> defaultParams = new ArrayList<>(defaultParameters);

        for (ReportWithParams paramsData: parameters) {
            TemplateParameter templateParameter = defaultParams.stream()
                    .filter(e -> e.getReport().equals(paramsData.getReport()))
                    .findFirst()
                    .orElse(null);
            defaultParams.remove(templateParameter);
            if (templateParameter != null) {
                Report report = templateParameter.getReport();
                for (ParameterValue paramValue: templateParameter.getParameterValues()) {
                    String alias = paramValue.getAlias();
                    String stringValue = paramValue.getDefaultValue();

                    Report reportFromXml = reportService.convertToReport(report.getXml());
                    ReportInputParameter inputParameter = reportFromXml.getInputParameters().stream()
                            .filter(e -> e.getAlias().equals(alias))
                            .findFirst()
                            .orElse(null);
                    if (inputParameter != null) {
                        try {
                            outboundTemplateService.checkParameterTypeChanged(inputParameter, paramValue);
                            Class parameterClass = classResolver.resolveClass(inputParameter);
                            Object value = reportService.convertFromString(parameterClass, stringValue);
                            paramsData.put(alias, value);
                        } catch (ReportParameterTypeChangedException e) {
                            showNotification(messages.formatMessage(EmailTemplateEdit.class, "parameterTypeChanged", report.getName(), alias));
                            paramsData.put(alias, null);
                        }
                    }
                }
            }
        }
    }
}
