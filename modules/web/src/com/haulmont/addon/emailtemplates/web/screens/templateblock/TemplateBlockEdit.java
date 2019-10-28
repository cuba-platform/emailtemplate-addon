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
import com.haulmont.addon.emailtemplates.utils.HtmlTemplateUtils;
import com.haulmont.addon.grapesjs.web.gui.components.GrapesJsNewsletterHtmlEditor;
import com.haulmont.cuba.gui.components.HasValue;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.components.SourceCodeEditor;
import com.haulmont.cuba.gui.icons.CubaIcon;
import com.haulmont.cuba.gui.screen.*;

import javax.inject.Inject;
import java.util.Map;
import java.util.TreeMap;

@UiController("emailtemplates_TemplateBlock.edit")
@UiDescriptor("template-block-edit.xml")
@EditedEntityContainer("templateBlockDc")
@LoadDataBeforeShow
public class TemplateBlockEdit extends StandardEditor<TemplateBlock> {

    @Inject
    private SourceCodeEditor htmlTemplate;

    @Inject
    protected LookupField<String> iconLookup;

    @Inject
    private GrapesJsNewsletterHtmlEditor visualTemplate;

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        initIconLookup();
    }

    protected void initIconLookup() {
        Map<String, String> optionsMap = new TreeMap<>();
        for (CubaIcon cubaIcon : CubaIcon.values()) {
            if (cubaIcon.source().startsWith("font-icon:")) {
                String caption = cubaIcon.source().substring("font-icon:".length());
                optionsMap.put(caption, cubaIcon.source());
            }
        }
        iconLookup.setOptionsMap(optionsMap);
        iconLookup.setOptionIconProvider(iconName -> iconName);
    }

    @Subscribe("htmlTemplate")
    public void onHtmlTemplateValueChange(HasValue.ValueChangeEvent<String> event) {
        visualTemplate.setValue(htmlTemplate.getValue());
    }

    @Subscribe("visualTemplate")
    public void onTemplateEditorValueChange(HasValue.ValueChangeEvent<String> event) {
        htmlTemplate.setValue(HtmlTemplateUtils.prettyPrintHTML(event.getValue()));
    }


}