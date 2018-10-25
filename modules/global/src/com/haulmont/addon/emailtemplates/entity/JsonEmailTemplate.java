package com.haulmont.addon.emailtemplates.entity;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.reports.app.service.ReportService;
import com.haulmont.reports.entity.Report;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;

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
    }

    public String getJsonBody() {
        return jsonBody;
    }

    public void setJsonBody(String jsonBody) {
        this.jsonBody = jsonBody;
    }

    @Override
    public Report getReport() {
        Report report = AppBeans.get(ReportService.class).convertToReport(getReportXml());
        String html = getHtml();
        html = html.replaceAll("\\$\\{([a-zA-Z0-9.]*[^}]*)}","\\$\\{Root.fields.$1\\}");
        report.getDefaultTemplate().setContent(html.getBytes());
        report.setIsTmp(true);
        return report;
    }
}