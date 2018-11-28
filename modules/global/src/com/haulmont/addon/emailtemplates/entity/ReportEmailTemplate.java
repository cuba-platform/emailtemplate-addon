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