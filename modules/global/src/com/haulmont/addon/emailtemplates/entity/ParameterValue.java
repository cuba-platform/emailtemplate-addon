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

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.reports.entity.ParameterType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@NamePattern("%s %s %s|parameterType,alias,defaultValue")
@Table(name = "EMAILTEMPLATES_PARAMETER_VALUE")
@Entity(name = "emailtemplates$ParameterValue")
public class ParameterValue extends StandardEntity {
    private static final long serialVersionUID = -4649653855490046044L;

    @NotNull
    @Column(name = "PARAMETER_TYPE", nullable = false)
    protected Integer parameterType;

    @NotNull
    @Column(name = "ALIAS", nullable = false)
    protected String alias;

    @Column(name = "DEFAULT_VALUE")
    protected String defaultValue;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TEMPLATE_PARAMETER_ID")
    protected TemplateReport templateParameters;
    public TemplateReport getTemplateParameters() {
        return templateParameters;
    }

    public void setTemplateParameters(TemplateReport templateParameters) {
        this.templateParameters = templateParameters;
    }

    public void setParameterType(ParameterType parameterType) {
        this.parameterType = parameterType == null ? null : parameterType.getId();
    }

    public ParameterType getParameterType() {
        return parameterType == null ? null : ParameterType.fromId(parameterType);
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDefaultValue() {
        return defaultValue;
    }


}