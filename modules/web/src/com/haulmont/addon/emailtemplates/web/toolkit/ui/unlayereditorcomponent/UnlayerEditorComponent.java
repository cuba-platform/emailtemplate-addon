package com.haulmont.addon.emailtemplates.web.toolkit.ui.unlayereditorcomponent;

import com.vaadin.annotations.JavaScript;
import com.vaadin.ui.AbstractJavaScriptComponent;
import elemental.json.JsonObject;

@JavaScript({"//editor.unlayer.com/embed.js",
        "unlayereditorcomponent-connector.js"})
public class UnlayerEditorComponent extends AbstractJavaScriptComponent {
    public UnlayerEditorComponent() {
        addFunction("valueChanged", arguments -> {
            JsonObject data = arguments.get(0);
            JsonObject json = data.getObject("design");
            String html = data.getString("html");
            getState(false).json = json;
            getState(false).html = html;

            listener.valueChanged(json);
        });
    }

    @Override
    protected UnlayerEditorComponentState getState() {
        return (UnlayerEditorComponentState) super.getState();
    }

    @Override
    protected UnlayerEditorComponentState getState(boolean markAsDirty) {
        return (UnlayerEditorComponentState) super.getState(markAsDirty);
    }

    public String getHtml() {
        return getState(false).html;
    }

    public interface ValueChangeListener {
        void valueChanged(JsonObject json);
    }

    private ValueChangeListener listener;

    public void setValue(JsonObject value) {
        getState().json = value;
    }

    public JsonObject getValue() {
        return getState(false).json;
    }

    public ValueChangeListener getListener() {
        return listener;
    }

    public void setListener(ValueChangeListener listener) {
        this.listener = listener;
    }
}