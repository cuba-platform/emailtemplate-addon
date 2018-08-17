package com.haulmont.addon.emailtemplates.dto;

import com.haulmont.reports.entity.Report;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ReportWithParams implements Serializable {

    protected Report report;
    protected Map<String, Object> params;

    public ReportWithParams(Report report) {
        this.report = report;
        params = new HashMap<>();
    }

    public Report getReport() {
        return report;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public Object put(String key, Object value) {
        return params.put(key, value);
    }

    public boolean remove(String key, Object value) {
        return params.remove(key, value);
    }

    public boolean isEmptyParams() {
        return params.isEmpty();
    }

}
