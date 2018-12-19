package com.haulmont.addon.emailtemplates.web.templategroup;

import com.haulmont.cuba.gui.screen.*;

@UiController("emailtemplates$TemplateGroup.browse")
@UiDescriptor("template-group-browse.xml")
@LookupComponent("templateGroupsTable")
@LoadDataBeforeShow
public class TemplateGroupBrowse extends StandardLookup {
}