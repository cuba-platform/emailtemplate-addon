package com.haulmont.addon.emailtemplates.web.templategroup;

import com.haulmont.cuba.gui.screen.*;
import com.haulmont.addon.emailtemplates.entity.TemplateGroup;


@UiController("emailtemplates$TemplateGroup.edit")
@UiDescriptor("template-group-edit.xml")
@EditedEntityContainer("templateGroupDc")
@LoadDataBeforeShow
public class TemplateGroupEdit extends StandardEditor<TemplateGroup> {
}