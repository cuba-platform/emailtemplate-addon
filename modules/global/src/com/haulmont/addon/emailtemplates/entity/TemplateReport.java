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
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.reports.entity.Report;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@NamePattern(" |report,parameterValues")
@Table(name = "EMAILTEMPLATES_TEMPLATE_REPORT")
@Entity(name = "emailtemplates$TemplateReport")
public class TemplateReport extends StandardEntity {
    private static final long serialVersionUID = -3260053745502523549L;

    @Column(name = "NAME")
    protected String name;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "REPORT_ID")
    @OnDeleteInverse(DeletePolicy.DENY)
    protected Report report;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMAIL_TEMPLATE_ID")
    protected EmailTemplate emailTemplate;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "templateParameters",cascade = CascadeType.ALL)
    protected List<ParameterValue> parameterValues;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    public void setParameterValues(List<ParameterValue> parameterValues) {
        this.parameterValues = parameterValues;
    }

    public List<ParameterValue> getParameterValues() {
        return parameterValues;
    }


    public void setReport(Report report) {
        this.report = report;
    }

    public Report getReport() {
        return report;
    }


    public void setEmailTemplate(EmailTemplate emailTemplate) {
        this.emailTemplate = emailTemplate;
    }

    public EmailTemplate getEmailTemplate() {
        return emailTemplate;
    }




}