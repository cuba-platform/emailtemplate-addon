package com.haulmont.addon.emailtemplates.entity;

import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.reports.entity.Report;

import javax.persistence.*;

@Entity(name = "emailtemplates$ReportEmailTemplate")
public class ReportEmailTemplate extends EmailTemplate {
    private static final long serialVersionUID = -8316169275125957265L;

    @Column(name = "USE_REPORT_SUBJECT")
    protected Boolean useReportSubject;

    @OnDeleteInverse(DeletePolicy.UNLINK)
    @Lookup(type = LookupType.SCREEN, actions = {"lookup", "open"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEMPLATE_REPORT_ID")
    protected Report emailBody;

    public ReportEmailTemplate() {
        setType(TemplateType.REPORT);
    }

    public Boolean getUseReportSubject() {
        return useReportSubject;
    }

    public void setUseReportSubject(Boolean useReportSubject) {
        this.useReportSubject = useReportSubject;
    }

    public Report getEmailBody() {
        return emailBody;
    }

    public void setEmailBody(Report emailBody) {
        this.emailBody = emailBody;
    }

    @Override
    public Report getEmailBodyReport() {
        return getEmailBody();
    }
}