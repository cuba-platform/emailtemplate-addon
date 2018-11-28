package com.haulmont.addon.emailtemplates.service;

import com.haulmont.addon.emailtemplates.entity.JsonEmailTemplate;
import com.haulmont.reports.entity.Report;

public interface TemplateConverterService {

    String NAME = "emailtemplates_TemplateConverterService";

    Report convertToReport(JsonEmailTemplate template);
}
