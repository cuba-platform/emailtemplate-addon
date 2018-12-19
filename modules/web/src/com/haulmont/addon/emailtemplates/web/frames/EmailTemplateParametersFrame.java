package com.haulmont.addon.emailtemplates.web.frames;

import com.haulmont.addon.emailtemplates.dto.ReportWithParamField;
import com.haulmont.addon.emailtemplates.dto.ReportWithParams;
import com.haulmont.addon.emailtemplates.entity.ParameterValue;
import com.haulmont.addon.emailtemplates.entity.TemplateReport;
import com.haulmont.addon.emailtemplates.exceptions.ReportParameterTypeChangedException;
import com.haulmont.addon.emailtemplates.service.TemplateParametersExtractorService;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.DataSupplier;
import com.haulmont.cuba.gui.screen.ScreenFragment;
import com.haulmont.cuba.gui.screen.Subscribe;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.reports.app.service.ReportService;
import com.haulmont.reports.entity.Report;
import com.haulmont.reports.entity.ReportInputParameter;
import com.haulmont.reports.gui.report.run.ParameterClassResolver;
import com.haulmont.reports.gui.report.run.ParameterFieldCreator;
import com.haulmont.reports.gui.report.validators.ReportParamFieldValidator;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;

import javax.inject.Inject;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@UiController("emailtemplates$parametersFrame")
@UiDescriptor("email-template-parameters-frame.xml")
public class EmailTemplateParametersFrame extends ScreenFragment {
    public static final String IS_DEFAULT_PARAM_VALUES = "isDefault";
    public static final String HIDE_REPORT_CAPTION = "hideReportCaption";
    public static final String TEMPLATE = "emailTemplate";
    public static final String TEMPLATE_REPORTS = "templateReports";
    public static final String TEMPLATE_REPORT = "templateReport";

    @WindowParam(name = IS_DEFAULT_PARAM_VALUES, required = true)
    protected Boolean isDefaultValues;

    @WindowParam(name = HIDE_REPORT_CAPTION)
    protected Boolean hideReportCaption = false;

    @Inject
    protected GridLayout parametersGrid;
    @Inject
    protected Metadata metadata;
    @Inject
    private Notifications notifications;
    @Inject
    protected DataSupplier dataSupplier;
    @Inject
    private ComponentsFactory componentsFactory;
    @Inject
    protected ParameterClassResolver classResolver;

    @Inject
    private TemplateParametersExtractorService templateParametersExtractorService;

    @Inject
    protected ParameterClassResolver parameterClassResolver;

    @WindowParam
    private List<TemplateReport> templateReports = new ArrayList<>();

    @WindowParam
    private TemplateReport templateReport;

    protected List<ReportWithParamField> parameterComponents = new ArrayList<>();

    protected ParameterFieldCreator parameterFieldCreator = new ParameterFieldCreator((AbstractFrame) getFragment());

    @Subscribe
    protected void onInit(InitEvent event) {
        if (templateReport != null) {
            setTemplateReport(templateReport);
        }
    }

    private List<ReportWithParams> getTemplateDefaultValues() throws ReportParameterTypeChangedException {
        List<ReportWithParams> reportWithParams = new ArrayList<>();
        for (TemplateReport templateReport : templateReports) {
            reportWithParams.add(templateParametersExtractorService.getReportDefaultValues(templateReport.getReport(),
                    templateReport.getParameterValues()));
        }
        return reportWithParams;
    }

    public void setTemplateReport(TemplateReport templateReport) {
        if (templateReport != null) {
            this.templateReports = Collections.singletonList(templateReport);
        }
    }

    public void createComponents() {
        clearComponents();
        try {
            List<ReportWithParams> parameters = getTemplateDefaultValues();

            if (parameters == null) {
                return;
            }

            List<Report> reports = parameters.stream()
                    .map(ReportWithParams::getReport)
                    .collect(Collectors.toList());

            parametersGrid.setRows(getRowCountForParameters(reports));

            int currentGridRow = 0;
            for (ReportWithParams reportData : parameters) {
                Report report = reportData.getReport();
                if (report != null && !report.getIsTmp()) {
                    report = dataSupplier.reload(report, ReportService.MAIN_VIEW_NAME);
                }

                if (report != null) {
                    if (CollectionUtils.isNotEmpty(report.getInputParameters())) {
                        if (BooleanUtils.isNotTrue(hideReportCaption)) {
                            createReportNameLabel(report, currentGridRow);
                            currentGridRow++;
                        }
                        Map<String, Field> componentsMap = new HashMap<>();
                        for (ReportInputParameter parameter : report.getInputParameters()) {
                            if (BooleanUtils.isNotTrue(parameter.getHidden())) {
                                componentsMap.put(parameter.getAlias(), createComponent(parameter, reportData.getParams(), currentGridRow));
                                currentGridRow++;
                            }
                        }
                        parameterComponents.add(new ReportWithParamField(report, componentsMap));
                    }
                }
            }
        } catch (ReportParameterTypeChangedException e) {
            notifications.create().withDescription(e.getMessage()).show();
        }
    }

    public void clearComponents() {
        parameterComponents.clear();
        parametersGrid.removeAll();
    }

    protected int getRowCountForParameters(List<Report> reports) {
        int rowsCount = 0;
        for (Report report : reports) {
            if (report != null && !report.getIsTmp()) {
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
        for (ReportWithParamField fieldValue : parameterComponents) {
            ReportWithParams reportData = new ReportWithParams(fieldValue.getReport());
            for (String paramName : fieldValue.getFields().keySet()) {
                Field parameterField = fieldValue.getFields().get(paramName);
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
        parametersGrid.add(label, 0, currentGridRow, 1, currentGridRow);
    }

    protected Field createComponent(ReportInputParameter parameter, Map<String, Object> values, int currentGridRow) {
        Field field = parameterFieldCreator.createField(parameter);
        if (BooleanUtils.isTrue(isDefaultValues)) {
            field.setRequired(false);
        }

        Object value = null;
        if (MapUtils.isNotEmpty(values)) {
            value = values.get(parameter.getAlias());
        }

        if (value == null && parameter.getDefaultValue() != null) {
            Class parameterClass = parameterClassResolver.resolveClass(parameter);
            if (parameterClass != null) {
                value = templateParametersExtractorService.convertFromString(parameter.getType(), parameterClass, parameter.getDefaultValue());
            }
        }

        if (field instanceof TokenList) {
            CollectionDatasource datasource = (CollectionDatasource) field.getDatasource();
            if (value instanceof Collection) {
                Collection collection = (Collection) value;
                for (Object selected : collection) {
                    datasource.includeItem((Entity) selected);
                }
            }
        } else {
            field.setValue(value);
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

        field.addValueChangeListener(new Consumer<HasValue.ValueChangeEvent>() {
            @Override
            public void accept(HasValue.ValueChangeEvent valueChangeEvent) {
                Object fieldValue = valueChangeEvent.getValue();
                updateDefaultValue(parameter, fieldValue);
            }
        });

        if (field instanceof TokenList) {
            TokenList tokenList = (TokenList) field;
            tokenList.getDatasource().addCollectionChangeListener(e -> {
                Collection items = e.getDs().getItems();
                updateDefaultValue(parameter, items);
            });
        }

        return field;
    }

    private void updateDefaultValue(ReportInputParameter parameter, Object fieldValue) {
        String alias = parameter.getAlias();
        Report report = parameter.getReport();
        TemplateReport templateReport = templateReports.stream()
                .filter(e -> e.getReport().equals(report))
                .findFirst()
                .orElse(null);
        if (fieldValue == null && templateReport == null) {
            return;
        }
        ParameterValue parameterValue = templateReport.getParameterValues().stream()
                .filter(pv -> pv.getAlias().equals(alias))
                .findFirst()
                .orElse(null);
        if (parameterValue == null) {
            parameterValue = metadata.create(ParameterValue.class);
            parameterValue.setAlias(alias);
            parameterValue.setParameterType(parameter.getType());
            parameterValue.setTemplateParameters(templateReport);
            templateReport.getParameterValues().add(parameterValue);
        }
        Class parameterClass = classResolver.resolveClass(parameter);
        String stringValue = templateParametersExtractorService.convertToString(parameter.getType(), parameterClass, fieldValue);
        parameterValue.setDefaultValue(stringValue);
    }

}