package com.haulmont.addon.emailtemplates.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.reports.entity.ParameterType;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import com.haulmont.chile.core.annotations.NamePattern;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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
    protected TemplateParameter templateParameter;

    public void setTemplateParameter(TemplateParameter templateParameter) {
        this.templateParameter = templateParameter;
    }

    public TemplateParameter getTemplateParameter() {
        return templateParameter;
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