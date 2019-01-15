package com.haulmont.addon.emailtemplates.web.emailtemplate.report;

import com.haulmont.addon.emailtemplates.entity.ParameterValue;
import com.haulmont.addon.emailtemplates.entity.ReportEmailTemplate;
import com.haulmont.addon.emailtemplates.entity.TemplateReport;
import com.haulmont.addon.emailtemplates.web.emailtemplate.AbstractTemplateEditor;
import com.haulmont.addon.emailtemplates.web.frames.EmailTemplateParametersFrame;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.impl.CollectionPropertyDatasourceImpl;
import com.haulmont.reports.entity.Report;
import com.haulmont.reports.entity.ReportOutputType;
import org.apache.commons.lang3.BooleanUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class ReportEmailTemplateEdit extends AbstractTemplateEditor<ReportEmailTemplate> {

    @Named("defaultGroup.subject")
    private TextField subjectField;

    @Inject
    private LookupPickerField emailBody;

    @Named("useReportSubjectGroup.useReportSubject")
    private CheckBox useReportSubject;

    @Inject
    private Metadata metadata;

    @Inject
    protected VBoxLayout defaultValuesBox;

    @Inject
    private CollectionPropertyDatasourceImpl<ParameterValue, UUID> bodyParameterValuesDs;

    protected EmailTemplateParametersFrame parametersFrame;
    protected TemplateReport templateReport;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
    }

    @Override
    protected void postInit() {
        parametersFrame = (EmailTemplateParametersFrame) openFrame(defaultValuesBox, "emailtemplates$parametersFrame",
                ParamsMap.of(EmailTemplateParametersFrame.IS_DEFAULT_PARAM_VALUES, true,
                        EmailTemplateParametersFrame.HIDE_REPORT_CAPTION, true));

        parametersFrame.setTemplateReport(getItem().getEmailBodyReport());
        if (getItem().getEmailBodyReport() != null) {
            parametersFrame.createComponents();
        } else {
            parametersFrame.clearComponents();
        }

        emailBody.setOptionsDatasource((CollectionDatasource) getDsContext().get("emailBodiesDs"));
        emailBody.addLookupAction();
        emailBody.addClearAction();
        emailBody.addOpenAction();

        emailBody.setValue(getItem().getReport());

        emailBody.addValueChangeListener(new Consumer<HasValue.ValueChangeEvent>() {
            @Override
            public void accept(HasValue.ValueChangeEvent valueChangeEvent) {
                Report value = (Report) valueChangeEvent.getValue();
                if (value != null) {
                    Report report = getDsContext().getDataSupplier().reload(value, "emailTemplate-view");
                    if (report.getDefaultTemplate() != null) {
                        if (ReportOutputType.HTML == report.getDefaultTemplate().getReportOutputType()) {
                            templateReport = metadata.create(TemplateReport.class);
                            templateReport.setParameterValues(new ArrayList<>());
                            templateReport.setReport(report);

                            getItem().setEmailBodyReport(templateReport);
                            parametersFrame.setTemplateReport(getItem().getEmailBodyReport());
                            parametersFrame.createComponents();
                        } else {
                            getItem().setEmailBodyReport(null);
                            parametersFrame.setTemplateReport(getItem().getEmailBodyReport());
                            parametersFrame.clearComponents();
                            emailBody.setValue(null);
                            showNotification(getMessage("notification.reportIsNotHtml"), NotificationType.ERROR);
                        }
                    } else {
                        getItem().setEmailBodyReport(null);
                        parametersFrame.setTemplateReport(getItem().getEmailBodyReport());
                        parametersFrame.clearComponents();
                        emailBody.setValue(null);
                        showNotification(getMessage("notification.reportHasNoDefaultTemplate"), NotificationType.ERROR);
                    }
                } else {
                    getItem().setEmailBodyReport(null);
                    parametersFrame.setTemplateReport(getItem().getEmailBodyReport());
                    parametersFrame.clearComponents();
                }
            }
        });

        getDsContext().addBeforeCommitListener(context -> {
            if (templateReport != null)
                context.getCommitInstances().add(templateReport);
        });

        useReportSubject.addValueChangeListener(e -> {
            setSubjectVisibilty();
            if (BooleanUtils.isTrue((Boolean) e.getValue())) {
                getItem().setSubject(null);
            }
        });

        setSubjectVisibilty();
    }

    public void setSubjectVisibilty() {
        subjectField.setVisible(BooleanUtils.isNotTrue(getItem().getUseReportSubject()));
    }

    @Override
    protected boolean preCommit() {
        super.preCommit();
        if (!PersistenceHelper.isNew(getItem())) {
            ReportEmailTemplate original = getDsContext().getDataSupplier().reload(getItem(), "emailTemplate-view");
            ReportEmailTemplate current = getItem();
            TemplateReport originalEmailBodyReport = original.getEmailBodyReport();
            if (originalEmailBodyReport != null && !originalEmailBodyReport.equals(current.getEmailBodyReport())) {
                entitiesToRemove.addAll(originalEmailBodyReport.getParameterValues());
                entitiesToRemove.add(originalEmailBodyReport);
            }
            if (current.getEmailBodyReport() != null) {
                bodyParameterValuesDs.setModified(true);
            }
        }
        return true;
    }

    public void runReport() {
    }
}