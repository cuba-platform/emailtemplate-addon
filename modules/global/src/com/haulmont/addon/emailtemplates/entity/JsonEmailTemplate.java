package com.haulmont.addon.emailtemplates.entity;

import com.haulmont.chile.core.annotations.MetaProperty;
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
    @Column(name = "JSON_TEMPLATE")
    protected String jsonBody;

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
        initReport();
    }

    public String getReportXml() {
        return reportXml;
    }

    public JsonEmailTemplate() {
        setType(TemplateType.JSON);
    }

    public String getJsonBody() {
        return jsonBody;
    }

    public void setJsonBody(String jsonBody) {
        this.jsonBody = jsonBody;
    }

    @Override
    public Report getReport() {
        if (report == null) {
            initReport();
        }
        return report;
    }

    private void initReport() {
        report = AppBeans.get(ReportService.class).convertToReport(getReportXml());
        report.setXml(getReportXml());
        String html = getHtml();
        html = html.replaceAll("\\$\\{([a-zA-Z0-9.]*[^}]*)}", "\\$\\{Root.fields.$1\\}");
        report.getDefaultTemplate().setContent(html.getBytes());
        report.setIsTmp(true);
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

    @Override
    @MetaProperty
    public Boolean getUseReportSubject() {
        return true;
    }
}