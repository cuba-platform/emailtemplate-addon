package com.haulmont.addon.emailtemplates.web.screens;

import com.haulmont.addon.emailtemplates.utils.HtmlTemplateUtils;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.SourceCodeEditor;
import com.haulmont.cuba.gui.screen.Subscribe;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;

import javax.inject.Inject;
import java.util.Map;

@UiController("emailtemplates$htmlSourceCode")
@UiDescriptor("html-source-code-window.xml")
public class HtmlSourceCodeWindow extends AbstractWindow {

    @WindowParam
    protected String html;

    @Inject
    private SourceCodeEditor sourceCode;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        sourceCode.setValue(HtmlTemplateUtils.prettyPrintHTML(html));
    }

    public String getValue() {
        return sourceCode.getValue();
    }

    @Subscribe("windowCommit")
    private void onWindowCommitClick(Button.ClickEvent event) {
        close(WINDOW_COMMIT_AND_CLOSE_ACTION);
    }

    @Subscribe("windowClose")
    private void onWindowCloseClick(Button.ClickEvent event) {
        close(WINDOW_CLOSE_ACTION);
    }


}