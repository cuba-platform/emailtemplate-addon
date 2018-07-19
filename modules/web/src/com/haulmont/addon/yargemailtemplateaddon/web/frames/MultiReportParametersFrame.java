package com.haulmont.addon.yargemailtemplateaddon.web.frames;

import com.haulmont.addon.yargemailtemplateaddon.dto.ReportWithParams;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.DataSupplier;
import com.haulmont.reports.app.service.ReportService;
import com.haulmont.reports.entity.Report;
import com.haulmont.reports.entity.ReportInputParameter;
import com.haulmont.reports.gui.report.run.ParameterClassResolver;
import com.haulmont.reports.gui.report.run.ParameterFieldCreator;
import com.haulmont.reports.gui.report.validators.ReportParamFieldValidator;
import groovy.lang.Tuple2;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;

import javax.inject.Inject;
import java.util.*;

public class MultiReportParametersFrame extends AbstractFrame {
    public static final String REPORT_PARAMETER = "reports";

    @Inject
    protected GridLayout parametersGrid;
    @Inject
    protected ReportService reportService;
    @Inject
    protected DataSupplier dataSupplier;

    protected List<Report> reports;

    protected Report layoutReport;

    protected List<Tuple2<Report, Map<String, Field>>> parameterComponents = new ArrayList<>();

    protected ParameterFieldCreator parameterFieldCreator = new ParameterFieldCreator(this);

    protected ParameterClassResolver parameterClassResolver = new ParameterClassResolver();

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        reports = (List<Report>) params.get(REPORT_PARAMETER);

        if (CollectionUtils.isEmpty(reports)) {
            return;
        }
        createComponents();
    }

    public void setLayoutReport(Report report) {
        layoutReport = report;
    }

    public void createComponents() {
        parameterComponents.clear();
        parametersGrid.removeAll();

        List<Report> allReports = new ArrayList<>(reports);
        if (layoutReport != null) {
            allReports.add(layoutReport);
        }
        parametersGrid.setRows(getRowCountForParameters(allReports));

        int currentGridRow = 0;
        for (Report report: allReports) {
            if (!report.getIsTmp()) {
                report = dataSupplier.reload(report, ReportService.MAIN_VIEW_NAME);
            }

            if (report != null) {
                if (CollectionUtils.isNotEmpty(report.getInputParameters())) {
                    Map<String, Field> componentsMap = new HashMap<>();
                    for (ReportInputParameter parameter : report.getInputParameters()) {
                        if (BooleanUtils.isNotTrue(parameter.getHidden())) {
                            componentsMap.put(parameter.getAlias(), createComponent(parameter, currentGridRow));
                            currentGridRow++;
                        }
                    }
                    parameterComponents.add(new Tuple2<>(report, componentsMap));
                }
            }
        }
    }

    protected int getRowCountForParameters(List<Report> allReports) {
        int rowsCount = 0;
        for (Report report: allReports) {
            if (!report.getIsTmp()) {
                report = dataSupplier.reload(report, ReportService.MAIN_VIEW_NAME);
            }
            if (report != null) {
                if (CollectionUtils.isNotEmpty(report.getInputParameters())) {
                    for (ReportInputParameter parameter : report.getInputParameters()) {
                        if (BooleanUtils.isNotTrue(parameter.getHidden())) {
                            rowsCount++;
                        }
                    }
                }
            }
        }
        return rowsCount;
    }

    public List<ReportWithParams> collectParameters() {
        List<ReportWithParams> reportDataList = new ArrayList<>();
        for (Tuple2<Report, Map<String, Field>> reportWithParams : parameterComponents) {
            ReportWithParams reportData = new ReportWithParams(reportWithParams.getFirst());
            for (String paramName: reportWithParams.getSecond().keySet()) {
                Field parameterField = reportWithParams.getSecond().get(paramName);
                Object value = parameterField.getValue();
                reportData.put(paramName, value);
            }

            reportDataList.add(reportData);
        }
        return reportDataList;
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