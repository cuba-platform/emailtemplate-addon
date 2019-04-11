package com.haulmont.addon.emailtemplates.web.emailtemplate.attachment;

import com.haulmont.addon.emailtemplates.entity.EmailTemplate;
import com.haulmont.addon.emailtemplates.entity.TemplateReport;
import com.haulmont.addon.emailtemplates.web.frames.EmailTemplateParametersFrame;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.AppConfig;
import com.haulmont.cuba.gui.Fragments;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.AddAction;
import com.haulmont.cuba.gui.components.actions.ItemTrackingAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.reports.entity.Report;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class AttachmentFrame extends AbstractFrame {

    @Inject
    protected Metadata metadata;

    @Inject
    protected Fragments fragments;

    @Inject
    private Datasource<EmailTemplate> emailTemplateDs;

    @Inject
    private Table<FileDescriptor> filesTable;

    @Inject
    private CollectionDatasource<TemplateReport, UUID> attachedReportsDs;

    @Inject
    private Table<TemplateReport> reportsTable;

    @Inject
    private CollectionDatasource<FileDescriptor, UUID> attachedFilesDs;

    @Inject
    private VBoxLayout defaultValuesBox;

    @Inject
    private FieldGroup attachmentGroup;

    protected EmailTemplateParametersFrame parametersFrame;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        filesTable.addAction(new ItemTrackingAction("download") {
            @Override
            public void actionPerform(Component component) {
                FileDescriptor fileDescriptor = filesTable.getSingleSelected();
                if (fileDescriptor != null) {
                    AppConfig.createExportDisplay(getFrame()).show(fileDescriptor, null);
                }
            }
        });

        parametersFrame = fragments.create(this, EmailTemplateParametersFrame.class)
                .setHideReportCaption(true)
                .setIsDefaultValues(true)
                .createComponents();
        defaultValuesBox.add(parametersFrame);

        attachedReportsDs.addItemChangeListener(e -> {
            TemplateReport templateReport = e.getItem();
            if (templateReport != null) {
                parametersFrame.setTemplateReport(templateReport);
                parametersFrame.createComponents();
                attachmentGroup.setVisible(true);
            } else {
                parametersFrame.clearComponents();
                attachmentGroup.setVisible(false);
            }
        });

        reportsTable.addAction(new AddAction(reportsTable, items -> {
            for (Object o : items) {
                Report report = (Report) o;
                report = getDsContext().getDataSupplier().reload(report, "emailTemplate-view");
                TemplateReport templateReport = metadata.create(TemplateReport.class);
                templateReport.setReport(report);
                templateReport.setEmailTemplate(emailTemplateDs.getItem());
                templateReport.setParameterValues(new ArrayList<>());
                attachedReportsDs.addItem(templateReport);
            }
        }) {
            @Override
            public String getWindowId() {
                return "report$Report.browse";
            }
        });

    }
}
