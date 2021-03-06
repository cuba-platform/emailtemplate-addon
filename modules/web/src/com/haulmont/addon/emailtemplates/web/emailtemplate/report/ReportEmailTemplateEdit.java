/*
 * Copyright (c) 2008-2019 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.haulmont.addon.emailtemplates.web.emailtemplate.report;

import com.haulmont.addon.emailtemplates.entity.ParameterValue;
import com.haulmont.addon.emailtemplates.entity.ReportEmailTemplate;
import com.haulmont.addon.emailtemplates.entity.TemplateReport;
import com.haulmont.addon.emailtemplates.web.emailtemplate.AbstractTemplateEditor;
import com.haulmont.addon.emailtemplates.web.frames.EmailTemplateParametersFrame;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.Fragments;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.components.CheckBox;
import com.haulmont.cuba.gui.components.LookupPickerField;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.components.VBoxLayout;
import com.haulmont.cuba.gui.components.data.options.DatasourceOptions;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.impl.CollectionPropertyDatasourceImpl;
import com.haulmont.cuba.gui.screen.MessageBundle;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;
import com.haulmont.reports.entity.Report;
import com.haulmont.reports.entity.ReportOutputType;
import org.apache.commons.lang3.BooleanUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

@UiController("emailtemplates$ReportEmailTemplate.edit")
@UiDescriptor("email-template-report-edit.xml")
public class ReportEmailTemplateEdit extends AbstractTemplateEditor<ReportEmailTemplate> {

    @Named("defaultGroup.subject")
    private TextField<String> subjectField;

    @Inject
    private LookupPickerField<Report> emailBody;

    @Named("useReportSubjectGroup.useReportSubject")
    private CheckBox useReportSubject;

    @Inject
    private Metadata metadata;

    @Inject
    private Notifications notifications;

    @Inject
    private MessageBundle messageBundle;

    @Inject
    protected VBoxLayout defaultValuesBox;

    @Inject
    protected Fragments fragments;

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

        parametersFrame = fragments.create(this,EmailTemplateParametersFrame.class)
                .setIsDefaultValues(true)
                .setHideReportCaption(true);
        defaultValuesBox.add(parametersFrame);

        parametersFrame.setTemplateReport(getItem().getEmailBodyReport());
        if (getItem().getEmailBodyReport() != null) {
            parametersFrame.createComponents();
        } else {
            parametersFrame.clearComponents();
        }

        emailBody.setOptions(new DatasourceOptions<>((CollectionDatasource) getDsContext().get("emailBodiesDs")));

        emailBody.setValue(getItem().getReport());

        emailBody.addValueChangeListener(e -> {
            Report value = (Report) e.getValue();
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
                        notifications.create(Notifications.NotificationType.ERROR)
                                .withDescription(messageBundle.getMessage("notification.reportIsNotHtml"))
                                .show();
                    }
                } else {
                    getItem().setEmailBodyReport(null);
                    parametersFrame.setTemplateReport(getItem().getEmailBodyReport());
                    parametersFrame.clearComponents();
                    emailBody.setValue(null);
                    notifications.create(Notifications.NotificationType.ERROR)
                            .withDescription(messageBundle.getMessage("notification.reportHasNoDefaultTemplate"))
                            .show();
                }
            } else {
                getItem().setEmailBodyReport(null);
                parametersFrame.setTemplateReport(getItem().getEmailBodyReport());
                parametersFrame.clearComponents();
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