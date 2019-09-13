/*
 * Copyright (c) 2008-2019 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.haulmont.addon.emailtemplates.web.screens.templateblock;

import com.haulmont.addon.emailtemplates.entity.TemplateBlock;
import com.haulmont.addon.emailtemplates.web.screens.templateblockgroup.TemplateBlockGroupBrowse;
import com.haulmont.cuba.gui.Screens;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.screen.*;

import javax.inject.Inject;

@UiController("emailtemplates_TemplateBlock.browse")
@UiDescriptor("template-block-browse.xml")
@LookupComponent("templateBlocksTable")
@LoadDataBeforeShow
public class TemplateBlockBrowse extends StandardLookup<TemplateBlock> {

    @Inject
    private Screens screens;

    @Subscribe("groups")
    private void onGroupsClick(Button.ClickEvent event) {
        screens.create(TemplateBlockGroupBrowse.class, OpenMode.NEW_TAB).show();
    }
}