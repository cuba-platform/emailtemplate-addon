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

package com.haulmont.addon.emailtemplates.service;


import com.haulmont.addon.emailtemplates.dto.ReportWithParams;
import com.haulmont.addon.emailtemplates.entity.EmailTemplate;
import com.haulmont.addon.emailtemplates.entity.ParameterValue;
import com.haulmont.addon.emailtemplates.exceptions.ReportParameterTypeChangedException;
import com.haulmont.reports.entity.ParameterType;
import com.haulmont.reports.entity.Report;
import com.haulmont.reports.entity.ReportInputParameter;

import java.util.List;

public interface TemplateParametersExtractorService {

    String NAME = "emailtemplates_TemplateParametersExtractorService";

    List<ReportWithParams> getTemplateDefaultValues(EmailTemplate emailTemplate)
            throws ReportParameterTypeChangedException;

    ReportWithParams getReportDefaultValues(Report report, List<ParameterValue> parameterValues)
            throws ReportParameterTypeChangedException;

    Class resolveClass(ReportInputParameter parameter);

    String convertToString(ParameterType parameterType, Class parameterClass, Object paramValue);

    Object convertFromString(ParameterType parameterType, Class parameterClass, String paramValueStr);
}