package com.haulmont.addon.emailtemplates.entity;

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

    public JsonEmailTemplate() {
        setType(TemplateType.JSON);
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
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
        return null;
    }
}