package com.haulmont.addon.yargemailtemplateaddon.web.frames;

import com.haulmont.addon.yargemailtemplateaddon.dto.ReportWithParams;
import com.haulmont.addon.yargemailtemplateaddon.entity.ContentEmailTemplate;
import com.haulmont.addon.yargemailtemplateaddon.entity.LayoutEmailTemplate;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.DataSupplier;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.reports.app.service.ReportService;
import com.haulmont.reports.entity.Report;
import com.haulmont.reports.entity.ReportInputParameter;
import com.haulmont.reports.gui.report.run.ParameterClassResolver;
import com.haulmont.reports.gui.report.run.ParameterFieldCreator;
import com.haulmont.reports.gui.report.validators.ReportParamFieldValidator;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;

import javax.inject.Inject;
import java.util.*;

public class MultiReportParametersFrame extends AbstractFrame {
    public static final String TEMPLATE = "template";

    @Inject
    protected GridLayout parametersGrid;
    @Inject
    protected ReportService reportService;
    @Inject
    protected DataSupplier dataSupplier;
    @Inject
    private ComponentsFactory componentsFactory;

    protected LayoutEmailTemplate layoutTemplate;
    protected ContentEmailTemplate contentTemplate;

    protected Set<Report> reports = new HashSet<>();

    protected Map<Report, Map<String, Field>> parameterComponents = new HashMap<>();

    protected ParameterFieldCreator parameterFieldCreator = new ParameterFieldCreator(this);

    protected ParameterClassResolver parameterClassResolver = new ParameterClassResolver();

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        LayoutEmailTemplate template = (LayoutEmailTemplate) params.get(TEMPLATE);

        if (template instanceof ContentEmailTemplate) {
            contentTemplate = (ContentEmailTemplate) template;
        } else {
            layoutTemplate = template;
        }

        if (layoutTemplate != null) {
            reports.add(layoutTemplate.getReport());
        }
        if (contentTemplate != null) {
            reports.add(contentTemplate.getReport());
            if (contentTemplate.getAttachments() != null) {
                reports.addAll(contentTemplate.getAttachments());
            }
        }

        if (CollectionUtils.isEmpty(reports)) {
            return;
        }
        createComponents();
    }

    public void setLayoutReport(LayoutEmailTemplate layoutTemplate) {
        if (this.layoutTemplate != null) {
            reports.remove(this.layoutTemplate.getReport());
        }
        this.layoutTemplate = layoutTemplate;
        if (layoutTemplate == null) {
            return;
        }
        reports.add(layoutTemplate.getReport());
    }

    public void createComponents() {
        parameterComponents.clear();
        parametersGrid.removeAll();

        parametersGrid.setRows(getRowCountForParameters());

        int currentGridRow = 0;
        for (Report report: reports) {
            if (!report.getIsTmp()) {
                report = dataSupplier.reload(report, ReportService.MAIN_VIEW_NAME);
            }

            if (report != null) {
                if (CollectionUtils.isNotEmpty(report.getInputParameters())) {
                    createReportNameLabel(report, currentGridRow);
                    currentGridRow++;
                    Map<String, Field> componentsMap = new HashMap<>();
                    for (ReportInputParameter parameter : report.getInputParameters()) {
                        if (BooleanUtils.isNotTrue(parameter.getHidden())) {
                            componentsMap.put(parameter.getAlias(), createComponent(parameter, currentGridRow));
                            currentGridRow++;
                        }
                    }
                    parameterComponents.put(report, componentsMap);
                }
            }
        }
    }

    protected int getRowCountForParameters() {
        int rowsCount = 0;
        for (Report report: reports) {
            if (!report.getIsTmp()) {
                report = dataSupplier.reload(report, ReportService.MAIN_VIEW_NAME);
            }
            if (report != null) {
                if (CollectionUtils.isNotEmpty(report.getInputParameters())) {
                    rowsCount++;
                    for (ReportInputParameter parameter : report.getInputParameters()) {
                        if (BooleanUtils.isNotTrue(parameter.getHidden())) {
                            rowsCount++;
                        }
                    }
                }
            }
        }
        return rowsCount == 0 ? 1 : rowsCount;
    }

    public List<ReportWithParams> collectParameters() {
        List<ReportWithParams> reportDataList = new ArrayList<>();
        for (Report report : parameterComponents.keySet()) {
            ReportWithParams reportData = new ReportWithParams(report);
            for (String paramName: parameterComponents.get(report).keySet()) {
                Field parameterField = parameterComponents.get(report).get(paramName);
                Object value = parameterField.getValue();
                reportData.put(paramName, value);
            }

            reportDataList.add(reportData);
        }
        return reportDataList;
    }

    protected void createReportNameLabel(Report report, int currentGridRow) {
        Label label = componentsFactory.createComponent(Label.class);
        label.setWidth(Component.AUTO_SIZE);
        label.setValue(report.getName());
        parametersGrid.add(label, 0, currentGridRow);
    }

    protected Field createComponent(ReportInputParameter parameter, int currentGridRow) {
        Field field = parameterFieldCreator.createField(parameter);
        field.setWidth("400px");

        Object value = null;

        if (parameter.getDefaultValue() != null) {
            Class parameterClass = parameterClassResolver.resolveClass(parameter);
            if (parameterClass != null) {
                value = reportService.convertFromString(parameterClass, parameter.getDefaultValue());
            }
        }

        if (!(field instanceof TokenList)) {
            field.setValue(value);
        } else {
            CollectionDatasource datasource = (CollectionDatasource) field.getDatasource();
            if (value instanceof Collection) {
                Collection collection = (Collection) value;
                for (Object selected : collection) {
                    datasource.includeItem((Entity) selected);
                }
            }
        }

        if (BooleanUtils.isTrue(parameter.getValidationOn())) {
            field.addValidator(new ReportParamFieldValidator(parameter));
        }

        Label label = parameterFieldCreator.createLabel(parameter, field);
        label.setStyleName("c-report-parameter-caption");

        if (currentGridRow == 0) {
            field.requestFocus();
        }

        parametersGrid.add(label, 0, currentGridRow);
        parametersGrid.add(field, 1, currentGridRow);

        return field;
    }

}