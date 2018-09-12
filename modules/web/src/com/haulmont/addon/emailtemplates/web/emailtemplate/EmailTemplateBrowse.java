package com.haulmont.addon.emailtemplates.web.emailtemplate;

import com.haulmont.addon.emailtemplates.entity.EmailTemplate;
import com.haulmont.addon.emailtemplates.entity.OutboundEmail;
import com.haulmont.addon.emailtemplates.web.outboundemail.OutboundEmailEdit;
import com.haulmont.bali.util.ParamsMap;
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

        Action testAction = new ItemTrackingAction(emailTemplatesTable, "testAction").
                withHandler(actionPerformedEvent -> onTestTemplateClick());
        emailTemplatesTable.addAction(testAction);
    }

    protected void onTestTemplateClick() {
        viewEmailTemplate(true);
    }

    protected void onSendEmailClick() {
        viewEmailTemplate(false);
    }


    protected void viewEmailTemplate(Boolean isTest) {
        EmailTemplate template = emailTemplatesTable.getSingleSelected();
        OutboundEmail outboundEmail = metadata.create(OutboundEmail.class);
        outboundEmail.setEmailTemplate(dataManager.reload(template, "emailTemplate-view"));
        openEditor("emailtemplates$OutboundEmail.edit", outboundEmail,
                WindowManager.OpenType.DIALOG, ParamsMap.of(OutboundEmailEdit.IS_TEST, isTest));
    }

}