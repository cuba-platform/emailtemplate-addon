package com.haulmont.addon.emailtemplates.web.emailtemplate.report;

import com.haulmont.addon.emailtemplates.entity.ParameterValue;
import com.haulmont.addon.emailtemplates.entity.ReportEmailTemplate;
import com.haulmont.addon.emailtemplates.entity.TemplateReport;
import com.haulmont.addon.emailtemplates.web.emailtemplate.StandardTemplateEditor;
import com.haulmont.addon.emailtemplates.web.frames.EmailTemplateParametersFrame;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.Fragments;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.impl.CollectionPropertyDatasourceImpl;
import com.haulmont.cuba.gui.model.DataContext;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.reports.entity.Report;
import com.haulmont.reports.entity.ReportOutputType;
import org.apache.commons.lang3.BooleanUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Consumer;

@UiController("emailtemplates$ReportEmailTemplate.edit")
@UiDescriptor("email-template-report-edit.xml")
@EditedEntityContainer("emailTemplateDs")
@LoadDataBeforeShow
public class ReportEmailTemplateEdit extends StandardTemplateEditor<ReportEmailTemplate> {

    @Named("defaultGroup.subject")
    private TextField subjectField;

    @Inject
    private LookupPickerField emailBody;

    @Inject
    private CollectionDatasource emailBodiesDs;

    @Named("useReportSubjectGroup.useReportSubject")
    private CheckBox useReportSubject;

    @Inject
    private Metadata metadata;

    @Inject
    private Notifications notifications;

    @Inject
    private MessageBundle messageBundle;

    @Inject
    private DataContext dataContext;

    @Inject
    private Fragments fragments;

    @Inject
    protected VBoxLayout defaultValuesBox;

    @Inject
    private CollectionPropertyDatasourceImpl<ParameterValue, UUID> bodyParameterValuesDs;

    protected EmailTemplateParametersFrame parametersFrame;
    protected TemplateReport templateReport;

    @Subscribe
    protected void onAfterInit(AfterInitEvent event) {
        parametersFrame = (EmailTemplateParametersFrame) fragments.create(this,
                "emailtemplates$parametersFrame",
                new MapScreenOptions(ParamsMap.of(EmailTemplateParametersFrame.IS_DEFAULT_PARAM_VALUES, true,
                        EmailTemplateParametersFrame.HIDE_REPORT_CAPTION, true)))
                .init();
        defaultValuesBox.add(parametersFrame.getFragment());

        parametersFrame.setTemplateReport(getEditedEntity().getEmailBodyReport());
        if (getEditedEntity().getEmailBodyReport() != null) {
            parametersFrame.createComponents();
        } else {
            parametersFrame.clearComponents();
        }

        //todo

        emailBody.setOptionsDatasource(emailBodiesDs);
        emailBody.addLookupAction();
        emailBody.addClearAction();
        emailBody.addOpenAction();

        emailBody.setValue(getEditedEntity().getReport());

        emailBody.addValueChangeListener(new Consumer<HasValue.ValueChangeEvent>() {
            @Override
            public void accept(HasValue.ValueChangeEvent valueChangeEvent) {
                Report value = (Report) valueChangeEvent.getValue();
                if (value != null) {
                    Report report = dataSupplier.reload(value, "emailTemplate-view");
                    if (report.getDefaultTemplate() != null) {
                        if (ReportOutputType.HTML == report.getDefaultTemplate().getReportOutputType()) {
                            templateReport = metadata.create(TemplateReport.class);
                            templateReport.setParameterValues(new ArrayList<>());
                            templateReport.setReport(report);

                            getEditedEntity().setEmailBodyReport(templateReport);
                            parametersFrame.setTemplateReport(getEditedEntity().getEmailBodyReport());
                            parametersFrame.createComponents();
                        } else {
                            getEditedEntity().setEmailBodyReport(null);
                            parametersFrame.setTemplateReport(getEditedEntity().getEmailBodyReport());
                            parametersFrame.clearComponents();
                            emailBody.setValue(null);
                            notifications.create(Notifications.NotificationType.ERROR)
                                    .withDescription(messageBundle.getMessage("notification.reportIsNotHtml"))
                                    .show();
                        }
                    } else {
                        getEditedEntity().setEmailBodyReport(null);
                        parametersFrame.setTemplateReport(getEditedEntity().getEmailBodyReport());
                        parametersFrame.clearComponents();
                        emailBody.setValue(null);
                        notifications.create(Notifications.NotificationType.ERROR)
                                .withDescription(messageBundle.getMessage("notification.reportHasNoDefaultTemplate"))
                                .show();
                    }
                } else {
                    getEditedEntity().setEmailBodyReport(null);
                    parametersFrame.setTemplateReport(getEditedEntity().getEmailBodyReport());
                    parametersFrame.clearComponents();
                }
            }
        });

        dataContext.addPreCommitListener(context -> {
            if (templateReport != null)
                context.getModifiedInstances().add(templateReport);
        });

        useReportSubject.addValueChangeListener(e -> {
            setSubjectVisibilty();
            if (BooleanUtils.isTrue((Boolean) e.getValue())) {
                getEditedEntity().setSubject(null);
            }
        });

        setSubjectVisibilty();
    }


    public void setSubjectVisibilty() {
        subjectField.setVisible(BooleanUtils.isNotTrue(getEditedEntity().getUseReportSubject()));
    }

    @Subscribe(target = Target.DATA_CONTEXT)
    protected void onPreCommit(DataContext.PreCommitEvent event) {
        if (!PersistenceHelper.isNew(getEditedEntity())) {
            ReportEmailTemplate original = dataSupplier.reload(getEditedEntity(), "emailTemplate-view");
            ReportEmailTemplate current = getEditedEntity();
            TemplateReport originalEmailBodyReport = original.getEmailBodyReport();
            if (originalEmailBodyReport != null && !originalEmailBodyReport.equals(current.getEmailBodyReport())) {
                entitiesToRemove.addAll(originalEmailBodyReport.getParameterValues());
                entitiesToRemove.add(originalEmailBodyReport);
            }
            if (current.getEmailBodyReport() != null) {
                bodyParameterValuesDs.setModified(true);
            }
        }
    }

    public void runReport() {
    }
}