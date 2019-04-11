package com.haulmont.addon.emailtemplates.web.emailtemplate.browse;

import com.haulmont.addon.emailtemplates.entity.EmailTemplate;
import com.haulmont.addon.emailtemplates.entity.JsonEmailTemplate;
import com.haulmont.addon.emailtemplates.entity.ReportEmailTemplate;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.Screens;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.components.actions.ItemTrackingAction;
import com.haulmont.cuba.gui.screen.MapScreenOptions;
import com.haulmont.cuba.gui.screen.OpenMode;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;

import javax.inject.Inject;
import java.util.Map;

@UiController("emailtemplates$EmailTemplate.browse")
@UiDescriptor("email-template-browse.xml")
public class EmailTemplateBrowse extends AbstractLookup {

    @Inject
    private Screens screens;
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
        if (template != null) {
            template = dataManager.reload(template, "emailTemplate-view");
            screens.create("emailtemplates$EmailTemplate.send",
                    OpenMode.DIALOG,
                    new MapScreenOptions(ParamsMap.of("emailTemplate", template)))
                    .show();
        }
    }

    public void onGroupsButtonClick() {
        screens.create("emailtemplates$TemplateGroup.browse", OpenMode.NEW_WINDOW)
                .show();
    }
}