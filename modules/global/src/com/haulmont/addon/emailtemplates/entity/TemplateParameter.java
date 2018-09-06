package com.haulmont.addon.emailtemplates.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import com.haulmont.reports.entity.ParameterType;
import com.haulmont.reports.entity.Report;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import java.util.List;
import javax.persistence.OneToMany;

@NamePattern(" |report,parameterValues")
@Table(name = "EMAILTEMPLATES_TEMPLATE_PARAMETER")
@Entity(name = "emailtemplates$TemplateParameter")
public class TemplateParameter extends StandardEntity {
    private static final long serialVersionUID = -3260053745502523549L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "REPORT_ID")
    protected Report report;



    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "EMAIL_TEMPLATE_ID")
    protected EmailTemplate emailTemplate;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "templateParameter")
    protected List<ParameterValue> parameterValues;

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