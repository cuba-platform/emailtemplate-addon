package com.haulmont.addon.emailtemplates.web.screens;

import com.haulmont.addon.emailtemplates.utils.HtmlTemplateUtils;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.SourceCodeEditor;
import com.haulmont.cuba.gui.screen.Screen;
import com.haulmont.cuba.gui.screen.Subscribe;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;

import javax.inject.Inject;

@UiController("emailtemplates$htmlSourceCode")
@UiDescriptor("html-source-code-window.xml")
public class HtmlSourceCodeWindow extends Screen {

    @WindowParam
    protected String html;

    @Inject
    private SourceCodeEditor sourceCode;

    @Subscribe
    protected void onInit(InitEvent event) {
        sourceCode.setValue(HtmlTemplateUtils.prettyPrintHTML(html));
    }
}