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

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.reports.entity.Report;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity(name = "emailtemplates$ReportEmailTemplate")
public class ReportEmailTemplate extends EmailTemplate {
    private static final long serialVersionUID = -8316169275125957265L;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMAIL_BODY_REPORT_ID")
    protected TemplateReport emailBodyReport;


    public void setEmailBodyReport(TemplateReport emailBodyReport) {
        this.emailBodyReport = emailBodyReport;
    }

    public TemplateReport getEmailBodyReport() {
        return emailBodyReport;
    }

    public ReportEmailTemplate() {
        setType(TemplateType.REPORT);
    }

    @Override
    public Report getReport() {
        return getEmailBodyReport() != null ? getEmailBodyReport().getReport() : null;
    }
}