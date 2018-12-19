package com.haulmont.addon.emailtemplates.web.emailtemplate.attachment;

import com.haulmont.addon.emailtemplates.entity.EmailTemplate;
import com.haulmont.addon.emailtemplates.entity.TemplateReport;
import com.haulmont.addon.emailtemplates.web.frames.EmailTemplateParametersFrame;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.AppConfig;
import com.haulmont.cuba.gui.Fragments;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.VBoxLayout;
import com.haulmont.cuba.gui.components.actions.AddAction;
import com.haulmont.cuba.gui.components.actions.ItemTrackingAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.DataSupplier;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.reports.entity.Report;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.UUID;

@UiController("emailtemplates$attachmentFrame")
@UiDescriptor("email-template-attachments.xml")
public class AttachmentFrame extends ScreenFragment {

    @Inject
    protected Metadata metadata;

    @Inject
    private DataSupplier dataSupplier;

    @Inject
    private Fragments fragments;

    @Inject
    private Datasource<EmailTemplate> emailTemplateDs;

    @Inject
    private Table<FileDescriptor> filesTable;

    @Inject
    private CollectionDatasource<TemplateReport, UUID> attachedReportsDs;

    @Inject
    private Table<TemplateReport> reportsTable;

    @Inject
    private VBoxLayout defaultValuesBox;

    @Inject
    private FieldGroup attachmentGroup;

    protected EmailTemplateParametersFrame parametersFrame;

    @Subscribe
    protected void onInit(InitEvent event) {
        filesTable.addAction(new ItemTrackingAction("download") {
            @Override
            public void actionPerform(Component component) {
                FileDescriptor fileDescriptor = filesTable.getSingleSelected();
                if (fileDescriptor != null) {
                    AppConfig.createExportDisplay(getFragment()).show(fileDescriptor, null);
                }
            }
        });

        parametersFrame = (EmailTemplateParametersFrame) fragments.create(this,
                "emailtemplates$parametersFrame",
                new MapScreenOptions(ParamsMap.of(EmailTemplateParametersFrame.IS_DEFAULT_PARAM_VALUES, true,
                        EmailTemplateParametersFrame.HIDE_REPORT_CAPTION, true)))
                .init();
        defaultValuesBox.add(parametersFrame.getFragment());

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
                report = dataSupplier.reload(report, "emailTemplate-view");
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
