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

import com.haulmont.addon.emailtemplates.bean.TemplateParametersExtractor;
import com.haulmont.addon.emailtemplates.dto.ReportWithParams;
import com.haulmont.addon.emailtemplates.entity.EmailTemplate;
import com.haulmont.addon.emailtemplates.entity.ParameterValue;
import com.haulmont.addon.emailtemplates.exceptions.ReportParameterTypeChangedException;
import com.haulmont.reports.entity.ParameterType;
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

    @Override
    public String convertToString(ParameterType parameterType, Class parameterClass, Object paramValue) {
        return templateParametersExtractor.convertToString(parameterType, parameterClass, paramValue);
    }

    @Override
    public Object convertFromString(ParameterType parameterType, Class parameterClass, String paramValueStr) {
        return templateParametersExtractor.convertFromString(parameterType, parameterClass, paramValueStr);
    }


}