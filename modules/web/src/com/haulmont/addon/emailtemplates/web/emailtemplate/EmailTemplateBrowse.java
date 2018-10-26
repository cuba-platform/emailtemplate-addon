package com.haulmont.addon.emailtemplates.web.emailtemplate;

import com.haulmont.addon.emailtemplates.entity.EmailTemplate;
import com.haulmont.addon.emailtemplates.entity.JsonEmailTemplate;
import com.haulmont.addon.emailtemplates.entity.OutboundEmail;
import com.haulmont.addon.emailtemplates.entity.ReportEmailTemplate;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.components.actions.ItemTrackingAction;

import javax.inject.Inject;
import java.util.Map;

public class EmailTemplateBrowse extends AbstractLookup {

    @Inject
    protected Metadata metadata;
    @Inject
    protected DataManager dataManager;
    @Inject
    private GroupTable<EmailTemplate> emailTemplatesTable;

    @Inject
    private PopupButton createBtn;

    protected class TemplateCreateAction<T extends Entity> extends CreateAction {

        private Class<T> clazz;

        public TemplateCreateAction(ListComponent target, Class<T> clazz) {
            super(target);
            this.clazz = clazz;
        }

        @Override
        public void actionPerform(Component component) {
            internalOpenEditor(target.getDatasource(), metadata.create(clazz), null, getWindowParams());
        }

        @Override
        public String getWindowId() {
            return metadata.getClass(clazz).getName() + ".edit";
        }

        @Override
        public String getId() {
            return "create." + metadata.getClass(clazz).getName();
        }

        @Override
        public String getCaption() {
            return getMessage(getId());
        }
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        Action sendAction = new ItemTrackingAction(emailTemplatesTable, "sendAction").
                withHandler(actionPerformedEvent -> onSendEmailClick());
        emailTemplatesTable.addAction(sendAction);

        createBtn.addAction(new TemplateCreateAction(emailTemplatesTable, JsonEmailTemplate.class));
        createBtn.addAction(new TemplateCreateAction(emailTemplatesTable, ReportEmailTemplate.class));

        emailTemplatesTable.addAction(new EditAction(emailTemplatesTable) {

            @Override
            public String getWindowId() {
                EmailTemplate template = emailTemplatesTable.getSingleSelected();
                return template.getMetaClass().getName() + ".edit";
            }
        });
    }

    protected void onSendEmailClick() {
        EmailTemplate template = emailTemplatesTable.getSingleSelected();
        OutboundEmail outboundEmail = metadata.create(OutboundEmail.class);
        outboundEmail.setEmailTemplate(dataManager.reload(template, "emailTemplate-view"));
        openEditor("emailtemplates$OutboundEmail.edit", outboundEmail, WindowManager.OpenType.DIALOG);
    }

    public void onGroupsButtonClick() {
        openWindow("emailtemplates$TemplateGroup.browse", WindowManager.OpenType.NEW_WINDOW);
    }
}