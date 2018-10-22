package com.haulmont.addon.emailtemplates.web.toolkit.ui.unlayereditorcomponent;

import com.vaadin.shared.ui.JavaScriptComponentState;
import elemental.json.JsonObject;

import java.util.List;

public class UnlayerEditorComponentState extends JavaScriptComponentState {

    public JsonObject json;

    public String html;

    public List<NameValue> parameters;
}
