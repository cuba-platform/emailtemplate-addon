package com.haulmont.addon.emailtemplates.web.screens;

import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.SourceCodeEditor;

import javax.inject.Inject;
import java.util.Map;

public class HtmlSourceCodeWindow extends AbstractWindow {

    @WindowParam
    protected String html;

    @Inject
    private SourceCodeEditor sourceCode;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        sourceCode.setValue(html);
    }
}