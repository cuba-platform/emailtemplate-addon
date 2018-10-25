package com.haulmont.addon.emailtemplates.core;

import com.haulmont.addon.emailtemplates.dto.EmailTemplateBuilder;
import com.haulmont.addon.emailtemplates.dto.ReportWithParams;
import com.haulmont.addon.emailtemplates.entity.EmailTemplate;
import com.haulmont.addon.emailtemplates.entity.ParameterValue;
import com.haulmont.addon.emailtemplates.exceptions.ReportParameterTypeChangedException;
import com.haulmont.addon.emailtemplates.exceptions.TemplateNotFoundException;
import com.haulmont.cuba.core.global.EmailInfo;
import com.haulmont.reports.entity.ReportInputParameter;

import java.util.Collection;
import java.util.Map;

/**
 * That interface provides converting email template {@link EmailTemplate} with report parameters to cuba email info {@link EmailInfo}.
 * There are two cases to pass the report parameters. It is map to pass non-repeating parameters for all included reports,
 * and list of wrappers {@link ReportWithParams} containing parameters for each report separately.
 * Also interface provides checking that report parameter type {@link com.haulmont.reports.entity.ReportInputParameter} was changed. It compares with
 * parameter type value saved in {@link ParameterValue} entity.
 */
public interface EmailTemplatesAPI {

    String NAME = "emailtemplates_EmailTemplatesAPI";

    /**
     * That method creates {@link EmailInfo} by template and parameters map for all included reports.
     *
     * @param emailTemplate {@link EmailTemplate} entity containing body and attachments reports
     * @param params        map containing parameters for all included reports
     * @return {@link EmailInfo} from cuba emailer
     * @throws TemplateNotFoundException If emailTemplate do not contain reports or null
     */
    EmailInfo generateEmail(EmailTemplate emailTemplate, Map<String, Object> params) throws TemplateNotFoundException;

    /**
     * That method creates {@link EmailInfo} from template that may contain the same reports with different parameter values.
     *
     * @param emailTemplate {@link EmailTemplate} entity containing body and attachments reports
     * @param params        {@link ReportWithParams} wrapper containing report and its parameters
     * @return {@link EmailInfo} from cuba emailer
     * @throws TemplateNotFoundException If emailTemplate does not contain reports or null
     */
    EmailInfo generateEmail(EmailTemplate emailTemplate, Collection<ReportWithParams> params) throws TemplateNotFoundException;

    /**
     * That method creates {@link EmailInfo} by template with unique string code.
     *
     * @param emailTemplateCode unique string code of email template
     * @param params            map containing parameters for all included reports
     * @return {@link EmailInfo} from cuba emailer
     * @throws TemplateNotFoundException If emailTemplate does not contain reports or null
     */
    EmailInfo generateEmail(String emailTemplateCode, Map<String, Object> params) throws TemplateNotFoundException;

    /**
     * That method creates {@link EmailInfo} by template with unique string code.
     *
     * @param emailTemplateCode unique string code of email template
     * @param params            {@link ReportWithParams} wrapper containing report and its parameters
     * @return {@link EmailInfo} from cuba emailer
     * @throws TemplateNotFoundException If emailTemplate does not contain reports or null
     */
    EmailInfo generateEmail(String emailTemplateCode, Collection<ReportWithParams> params) throws TemplateNotFoundException;

    /**
     * That method checks that the report input parameter did not change own parameter type
     *
     * @param inputParameter {@link com.haulmont.reports.entity.ReportInputParameter} from cuba reporting
     * @param parameterValue entity {@link ParameterValue} to save report parameter default value
     * @throws ReportParameterTypeChangedException If parameter type of inputParameter does not equal to type saved in parameterValue.
     */
    void checkParameterTypeChanged(ReportInputParameter inputParameter, ParameterValue parameterValue) throws ReportParameterTypeChangedException;

    EmailTemplateBuilder buildFromTemplate(EmailTemplate emailTemplate);

    EmailTemplateBuilder buildFromTemplate(String code);
}
