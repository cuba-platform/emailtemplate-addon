package com.haulmont.addon.yargemailtemplateaddon.web.layoutemailtemplate;

import com.haulmont.addon.yargemailtemplateaddon.entity.ContentEmailTemplate;
import com.haulmont.addon.yargemailtemplateaddon.entity.LayoutEmailTemplate;
import com.haulmont.addon.yargemailtemplateaddon.entity.OutboundEmail;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.GroupDatasource;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

import static com.haulmont.addon.yargemailtemplateaddon.entity.TemplateType.CONTENT;
import static com.haulmont.addon.yargemailtemplateaddon.entity.TemplateType.LAYOUT;

public class LayoutEmailTemplateBrowse extends AbstractLookup {

    @Inject
    protected Metadata metadata;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected GroupDatasource<LayoutEmailTemplate, UUID> layoutEmailTemplatesDs;
    @Inject
    protected GroupTable<LayoutEmailTemplate> layoutEmailTemplatesTable;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        layoutEmailTemplatesTable.addAction(new EditAction(layoutEmailTemplatesTable) {
            @Override
            public String getWindowId() {
                LayoutEmailTemplate rule = layoutEmailTemplatesTable.getSingleSelected();
                if (rule != null) {
                    switch (rule.getType()) {
                        case CONTENT:
                            return "yet$ContentEmailTemplate.edit";
                        case LAYOUT:
                            return "yet$LayoutEmailTemplate.edit";
                    }
                }
                return "yet$LayoutEmailTemplate.browse";
            }
        });
    }

    public void onCreateLayoutEmailTemplate(Component source) {
        LayoutEmailTemplate layoutEmailTemplate = metadata.create(LayoutEmailTemplate.class);
        layoutEmailTemplate.setType(LAYOUT);
        AbstractEditor editor = openEditor("yet$LayoutEmailTemplate.edit", layoutEmailTemplate, WindowManager.OpenType.THIS_TAB);
        editor.addCloseListener(actionId -> layoutEmailTemplatesDs.refresh());
    }

    public void onCreateContentEmailTemplate(Component source) {
        ContentEmailTemplate contentEmailTemplate = metadata.create(ContentEmailTemplate.class);
        contentEmailTemplate.setType(CONTENT);
        AbstractEditor editor = openEditor("yet$ContentEmailTemplate.edit", contentEmailTemplate, WindowManager.OpenType.THIS_TAB);
        editor.addCloseListener(actionId -> layoutEmailTemplatesDs.refresh());
    }

    public void onTestTemplateClick() {
        onSendEmailClick();
    }

    public void onSendEmailClick() {
        LayoutEmailTemplate template = layoutEmailTemplatesTable.getSingleSelected();
        OutboundEmail outboundEmail = metadata.create(OutboundEmail.class);
        if (template != null) {
            outboundEmail.setTemplate(dataManager.reload(template, "emailTemplate-view"));
            openEditor("yet$OutboundEmail.edit", outboundEmail, WindowManager.OpenType.DIALOG);
        }
    }
}