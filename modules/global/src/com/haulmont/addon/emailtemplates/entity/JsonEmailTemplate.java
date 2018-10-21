package com.haulmont.addon.emailtemplates.entity;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.reports.app.service.ReportService;
import com.haulmont.reports.entity.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import java.util.Collections;

@Entity(name = "emailtemplates$JsonEmailTemplate")
public class JsonEmailTemplate extends EmailTemplate {
    private static final long serialVersionUID = 4012242076593058570L;

    @Lob
    @Column(name = "JSON_TEMPLATE")
    protected String jsonBody;

    @Lob
    @Column(name = "HTML")
    protected String html;

    public JsonEmailTemplate() {
        setType(TemplateType.JSON);
    }

    public String getJsonBody() {
        return jsonBody;
    }

    public void setJsonBody(String jsonBody) {
        this.jsonBody = jsonBody;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    @Override
    public Report getEmailBodyReport() {
        Metadata metadata = AppBeans.get(Metadata.class);
        Report report = metadata.create(Report.class);

        ReportTemplate template = metadata.create(ReportTemplate.class);
        template.setCode(ReportTemplate.DEFAULT_TEMPLATE_CODE);
        template.setReportOutputType(ReportOutputType.HTML);
        template.setReport(report);
        template.setName("template.html");
        String html = getHtml();
        if (html != null) {
            template.setContent(html.getBytes());
        }
        report.setTemplates(Collections.singletonList(template));
        report.setDefaultTemplate(template);

        BandDefinition bandDefinition = metadata.create(BandDefinition.class);
        bandDefinition.setReport(report);
        report.setBands(Collections.singleton(bandDefinition));

        report.setName(getName());
        report.setReportType(ReportType.SIMPLE);
        report.setIsTmp(true);

        report.setXml(AppBeans.get(ReportService.class).convertToString(report));
        return report;
    }
}