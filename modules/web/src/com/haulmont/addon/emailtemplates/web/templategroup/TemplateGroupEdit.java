package com.haulmont.addon.emailtemplates.web.templategroup;

import com.haulmont.addon.emailtemplates.entity.TemplateGroup;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;

@UiController("emailtemplates$TemplateGroup.edit")
@UiDescriptor("template-group-edit.xml")
public class TemplateGroupEdit extends AbstractEditor<TemplateGroup> {
}