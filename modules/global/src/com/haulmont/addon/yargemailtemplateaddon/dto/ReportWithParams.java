package com.haulmont.addon.yargemailtemplateaddon.dto;

import com.haulmont.reports.entity.Report;

import java.io.Serializable;
import java.util.Map;

public class ReportWithParams implements Serializable {

    protected Report report;
    protected Map<String, Object> params;

    public ReportWithParams(Report report) {
        this.report = report;
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
        if (params != null) {
            return params.put(key, value);
        } else return null;
    }

    public boolean remove(String key, Object value) {
        if (params != null) {
            return params.remove(key, value);
        } else return false;
    }
}
