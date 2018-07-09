package com.haulmont.addon.yargemailtemplateaddon.dto;

import com.haulmont.reports.entity.Report;
import org.apache.commons.collections4.MapUtils;

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
        return MapUtils.emptyIfNull(params);
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public Object put(String key, Object value) {
        return MapUtils.emptyIfNull(params).put(key, value);
    }

    public boolean remove(String key, Object value) {
        return MapUtils.emptyIfNull(params).remove(key, value);
    }
}
