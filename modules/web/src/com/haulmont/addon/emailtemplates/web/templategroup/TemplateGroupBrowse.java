package com.haulmont.addon.emailtemplates.web.templategroup;

import com.haulmont.cuba.gui.screen.*;
import com.haulmont.addon.emailtemplates.entity.TemplateGroup;


@UiController("emailtemplates$TemplateGroup.browse")
@UiDescriptor("template-group-browse.xml")
@LookupComponent("templateGroupsTable")
@LoadDataBeforeShow
public class TemplateGroupBrowse extends StandardLookup<TemplateGroup> {
}