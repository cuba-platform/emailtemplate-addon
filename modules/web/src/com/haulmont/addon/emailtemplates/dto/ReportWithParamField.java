package com.haulmont.addon.emailtemplates.dto;

import com.haulmont.reports.entity.Report;
import com.haulmont.cuba.gui.components.Field;

import java.io.Serializable;
import java.util.Map;

public class ReportWithParamField implements Serializable {
    protected Report report;
    protected Map<String, Field> fields;

    public ReportWithParamField(Report report, Map<String, Field> fields) {
        this.report = report;
        this.fields = fields;
    }

    public Report getReport() {
        return report;
    }

    public Map<String, Field> getFields() {
        return fields;
    }

    public void setFields(Map<String, Field> fields) {
        this.fields = fields;
    }

    public Field put(String key, Field value) {
        return fields.put(key, value);
    }

    public boolean remove(String key, Field value) {
        return fields.remove(key, value);
    }

    public boolean isEmptyParams() {
        return fields.isEmpty();
    }

}
