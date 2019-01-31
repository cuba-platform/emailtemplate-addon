package com.haulmont.addon.emailtemplates.service;

import com.haulmont.addon.emailtemplates.entity.JsonEmailTemplate;
import com.haulmont.addon.emailtemplates.utils.HtmlTemplateUtils;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.reports.ReportingApi;
import com.haulmont.reports.entity.*;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

@Service(TemplateConverterService.NAME)
public class TemplateConverterServiceBean implements TemplateConverterService {

    @Inject
    private Metadata metadata;

    @Inject
    private ReportingApi reportingApi;

    @Override
    public Report convertToReport(JsonEmailTemplate template) {
        String reportXml = template.getReportXml();
        Report report = null;
        if (StringUtils.isNotBlank(reportXml)) {
            report = reportingApi.convertToReport(reportXml);
            report.setXml(reportXml);
        } else {
            report = initReport(template);
        }
        report.setName(template.getName());
        report.setCode(template.getCode());
        updateReportOutputName(report, template);
        report.getDefaultTemplate().setContent(getHtmlReportTemplate(template).getBytes());
        report.setIsTmp(true);
        return report;
    }

    public String getHtmlReportTemplate(JsonEmailTemplate template) {
        String html = template.getHtml();
        if (html == null) {
            html = "";
        } else {
            html = HtmlTemplateUtils.prettyPrintHTML(html);
        }
        return html.replaceAll("\\$\\{([a-zA-Z0-9.]*[^}]*)}", "\\$\\{Root.fields.$1\\}");
    }

    private Report initReport(JsonEmailTemplate jsonTemplate) {
        Report report = metadata.create(Report.class);

        ReportTemplate template = metadata.create(ReportTemplate.class);
        template.setCode(ReportTemplate.DEFAULT_TEMPLATE_CODE);
        template.setReportOutputType(ReportOutputType.HTML);
        template.setReport(report);
        template.setName("template.html");
        String html = jsonTemplate.getHtml();
        if (html != null) {
            template.setContent(html.getBytes());
        }
        report.setTemplates(Collections.singletonList(template));
        report.setDefaultTemplate(template);

        BandDefinition rootDefinition = metadata.create(BandDefinition.class);
        rootDefinition.setReport(report);
        rootDefinition.setName("Root");
        rootDefinition.setPosition(0);
        rootDefinition.setDataSets(new ArrayList<>());
        report.setBands(new HashSet<>());
        report.getBands().add(rootDefinition);

        DataSet dataSet = metadata.create(DataSet.class);
        dataSet.setBandDefinition(rootDefinition);
        dataSet.setType(DataSetType.GROOVY);
        dataSet.setName("Root");
        rootDefinition.getDataSets().add(dataSet);

        rootDefinition.setReport(report);


        report.setName(jsonTemplate.getName());
        report.setReportType(ReportType.SIMPLE);
        report.setIsTmp(true);

        report.setXml(reportingApi.convertToString(report));
        return report;
    }

    public void updateReportOutputName(Report report, JsonEmailTemplate template) {
        BandDefinition rootBandDefinition = IterableUtils.find(report.getBands(), (Predicate) object -> {
            BandDefinition band = (BandDefinition) object;
            return band.getParentBandDefinition() == null;
        });
        DataSet dataSet = rootBandDefinition.getDataSets().stream()
                .filter(e -> "Root".equals(e.getName()))
                .findFirst()
                .orElse(null);
        if (dataSet == null) {
            dataSet = metadata.create(DataSet.class);
            dataSet.setBandDefinition(rootBandDefinition);
            dataSet.setType(DataSetType.GROOVY);
            dataSet.setName("Root");
            rootBandDefinition.getDataSets().add(dataSet);
        }
        String subject = template.getSubject();
        if (StringUtils.isNotBlank(subject)) {
            subject = subject.replaceAll("\\$\\{([a-zA-Z0-9]*)}", "\"+params[\"$1\"]+\"");
            subject = subject.replaceAll("\\$\\{([a-zA-Z0-9]*).([a-zA-Z0-9.]*)}", "\"+params[\"$1\"].$2+\"");
            dataSet.setText("return [[\"__REPORT_FILE_NAME\": \"" + subject + "\"]]");
        } else {
            dataSet.setText("");
        }
    }
}
