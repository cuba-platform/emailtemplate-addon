package com.haulmont.addon.emailtemplates.web.gui.components;

import com.google.gson.JsonObject;
import com.haulmont.addon.emailtemplates.web.toolkit.ui.unlayereditorcomponent.UnlayerEditorComponent;
import com.haulmont.cuba.gui.components.Component;

import java.util.Map;

public interface UnlayerTemplateEditor extends Component, Component.BelongToFrame {
    String NAME = "unlayerTemplateEditor";

    JsonObject getJson();

    void setJson(JsonObject json);

    void setJson(String json);

    String getHTML();

    void setListener(UnlayerEditorComponent.ValueChangeListener listener);

    void setParameters(Map<String, String> parameters);
}