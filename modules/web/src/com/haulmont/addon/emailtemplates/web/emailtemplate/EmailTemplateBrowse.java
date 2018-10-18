package com.haulmont.addon.emailtemplates.web.emailtemplate;

import com.haulmont.addon.emailtemplates.entity.EmailTemplate;
import com.haulmont.addon.emailtemplates.entity.OutboundEmail;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.GroupTable;
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

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        Action sendAction = new ItemTrackingAction(emailTemplatesTable, "sendAction").
                withHandler(actionPerformedEvent -> onSendEmailClick());
        emailTemplatesTable.addAction(sendAction);
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