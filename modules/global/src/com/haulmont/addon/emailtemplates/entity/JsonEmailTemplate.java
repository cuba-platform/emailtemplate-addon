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

package com.haulmont.addon.emailtemplates.entity;

import com.haulmont.addon.emailtemplates.service.TemplateConverterService;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.reports.app.service.ReportService;
import com.haulmont.reports.entity.Report;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Transient;
import java.util.ArrayList;

@Entity(name = "emailtemplates$JsonEmailTemplate")
public class JsonEmailTemplate extends EmailTemplate {
    private static final long serialVersionUID = 4012242076593058570L;

    @Lob
    @Column(name = "HTML")
    protected String html;

    @Lob
    @Column(name = "REPORT_XML")
    protected String reportXml;

    @Transient
    private TemplateReport templateReport;

    @Transient
    private Report report;

    public void setHtml(String html) {
        this.html = html;
    }

    public String getHtml() {
        return html;
    }


    public void setReportXml(String reportXml) {
        this.reportXml = reportXml;
    }

    public String getReportXml() {
        return reportXml;
    }

    public JsonEmailTemplate() {
        setType(TemplateType.JSON);
        setUseReportSubject(true);
    }

    @Override
    public Report getReport() {
        if (report == null) {
            initReport();
        }
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
        setReportXml(AppBeans.get(ReportService.class).convertToString(report));
    }

    public void initReport() {
        report = AppBeans.get(TemplateConverterService.class).convertToReport(this);
    }

    @Override
    public TemplateReport getEmailBodyReport() {
        if (templateReport == null) {
            templateReport = AppBeans.get(Metadata.class).create(TemplateReport.class);
            templateReport.setReport(getReport());
            templateReport.setParameterValues(new ArrayList<>());
        }
        return templateReport;
    }
}