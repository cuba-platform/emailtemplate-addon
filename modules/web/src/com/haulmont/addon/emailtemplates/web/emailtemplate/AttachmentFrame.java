package com.haulmont.addon.emailtemplates.web.emailtemplate;

import com.haulmont.addon.emailtemplates.entity.EmailTemplate;
import com.haulmont.addon.emailtemplates.web.frames.EmailTemplateParametersFrame;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.gui.AppConfig;
import com.haulmont.cuba.gui.components.AbstractFrame;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.VBoxLayout;
import com.haulmont.cuba.gui.components.actions.ItemTrackingAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.reports.entity.Report;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class AttachmentFrame extends AbstractFrame {

    @Inject
    private Datasource<EmailTemplate> emailTemplateDs;

    @Inject
    private Table<FileDescriptor> filesTable;

    @Inject
    private CollectionDatasource<Report, UUID> attachedReportsDs;

    @Inject
    private CollectionDatasource<FileDescriptor, UUID> attachedFilesDs;

    @Inject
    private VBoxLayout defaultValuesBox;

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

        EmailTemplate template = emailTemplateDs.getItem();
        parametersFrame = (EmailTemplateParametersFrame) openFrame(defaultValuesBox, "emailtemplates$parametersFrame",
                ParamsMap.of(EmailTemplateParametersFrame.TEMPLATE, template,
                        EmailTemplateParametersFrame.IS_DEFAULT_PARAM_VALUES, true,
                        EmailTemplateParametersFrame.HIDE_REPORT_CAPTION, true));

        attachedReportsDs.addItemChangeListener(e -> {
            Report report = e.getItem();
            if (report != null) {
                report = getDsContext().getDataSupplier().reload(report, "emailTemplate-view");
                parametersFrame.setReport(report);
                parametersFrame.createComponents();
            } else {
                parametersFrame.clearComponents();
            }
        });

    }
}
