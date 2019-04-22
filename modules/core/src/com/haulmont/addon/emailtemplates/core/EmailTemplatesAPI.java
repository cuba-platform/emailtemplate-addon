/*
 * Copyright (c) 2008-2019 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.haulmont.addon.emailtemplates.core;

import com.haulmont.addon.emailtemplates.builder.EmailTemplateBuilder;
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
 * That interface provides converting email template {@link EmailTemplate} with report parameters
 * to cuba email info {@link com.haulmont.cuba.core.global.EmailInfo}.
 * There are two cases to pass the report parameters. It is map to pass non-repeating parameters for all included reports,
 * and list of wrappers {@link ReportWithParams} containing parameters for each report separately.
 * Also interface provides checking that report parameter type {@link com.haulmont.reports.entity.ReportInputParameter} was changed. It compares with
 * parameter type value saved in {@link ParameterValue} entity.
 */
public interface EmailTemplatesAPI {

    String NAME = "emailtemplates_EmailTemplatesAPI";

    /**
     * That method creates {@link com.haulmont.cuba.core.global.EmailInfo} by template and parameters map for all included reports.
     *
     * @param emailTemplate {@link EmailTemplate} entity containing body and attachments reports
     * @param params        map containing parameters for all included reports
     * @return {@link com.haulmont.cuba.core.global.EmailInfo} from cuba emailer
     * @throws TemplateNotFoundException If emailTemplate do not contain reports or null
     */
    EmailInfo generateEmail(EmailTemplate emailTemplate, Map<String, Object> params)
            throws TemplateNotFoundException, ReportParameterTypeChangedException;

    /**
     * That method creates {@link com.haulmont.cuba.core.global.EmailInfo} from template
     * that may contain the same reports with different parameter values.
     *
     * @param emailTemplate {@link EmailTemplate} entity containing body and attachments reports
     * @param params        {@link ReportWithParams} wrapper containing report and its parameters
     * @return {@link com.haulmont.cuba.core.global.EmailInfo} from cuba emailer
     * @throws TemplateNotFoundException If emailTemplate does not contain reports or null
     */
    EmailInfo generateEmail(EmailTemplate emailTemplate, Collection<ReportWithParams> params)
            throws TemplateNotFoundException, ReportParameterTypeChangedException;

    /**
     * That method creates {@link com.haulmont.cuba.core.global.EmailInfo} by template with unique string code.
     *
     * @param emailTemplateCode unique string code of email template
     * @param params            map containing parameters for all included reports
     * @return {@link com.haulmont.cuba.core.global.EmailInfo} from cuba emailer
     * @throws TemplateNotFoundException If emailTemplate does not contain reports or null
     */
    EmailInfo generateEmail(String emailTemplateCode, Map<String, Object> params)
            throws TemplateNotFoundException, ReportParameterTypeChangedException;

    /**
     * That method creates {@link com.haulmont.cuba.core.global.EmailInfo} by template with unique string code.
     *
     * @param emailTemplateCode unique string code of email template
     * @param params            {@link ReportWithParams} wrapper containing report and its parameters
     * @return {@link com.haulmont.cuba.core.global.EmailInfo} from cuba emailer
     * @throws TemplateNotFoundException If emailTemplate does not contain reports or null
     */
    EmailInfo generateEmail(String emailTemplateCode, Collection<ReportWithParams> params)
            throws TemplateNotFoundException, ReportParameterTypeChangedException;

    /**
     * That method checks that the report input parameter did not change own parameter type
     *
     * @param inputParameter {@link com.haulmont.reports.entity.ReportInputParameter} from cuba reporting
     * @param parameterValue entity {@link ParameterValue} to save report parameter default value
     * @throws ReportParameterTypeChangedException If parameter type of inputParameter does not equal to type saved in parameterValue.
     */
    void checkParameterTypeChanged(ReportInputParameter inputParameter, ParameterValue parameterValue)
            throws ReportParameterTypeChangedException;

    /**
     * That method provides {@link EmailTemplateBuilder} fluent builder API for email template.
     *
     * @param emailTemplate {@link EmailTemplate} entity containing body and attachments reports
     * @return {@link EmailTemplateBuilder} fluent template builder API
     */
    EmailTemplateBuilder buildFromTemplate(EmailTemplate emailTemplate);

    /**
     * That method provides {@link EmailTemplateBuilder} fluent builder API by template with unique string code.
     *
     * @param code unique string code of email template*
     * @return {@link EmailTemplateBuilder} fluent template builder API
     * @throws TemplateNotFoundException If emailTemplate does not contain reports or null
     */
    EmailTemplateBuilder buildFromTemplate(String code) throws TemplateNotFoundException;
}
