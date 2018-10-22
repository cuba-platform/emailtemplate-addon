package com.haulmont.addon.emailtemplates.web.gui.components;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.haulmont.addon.emailtemplates.web.toolkit.ui.unlayereditorcomponent.UnlayerEditorComponent;
import com.haulmont.cuba.web.gui.components.WebAbstractComponent;
import elemental.json.impl.JreJsonFactory;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

public class WebUnlayerTemplateEditor extends WebAbstractComponent<com.haulmont.addon.emailtemplates.web.toolkit.ui.unlayereditorcomponent.UnlayerEditorComponent> implements UnlayerTemplateEditor {

    private static final String DEFAULT_TEMPLATE_LAYOUT = "{\"counters\":{\"u_column\":2,\"u_row\":2},\"body\":{\"rows\":[{\"cells\":[1],\"columns\":[{\"contents\":[],\"values\":{\"_meta\":{\"htmlID\":\"u_column_1\",\"htmlClassNames\":\"u_column\"}}}],\"values\":{\"backgroundColor\":\"\",\"backgroundImage\":{\"url\":\"\",\"fullWidth\":true,\"repeat\":false,\"center\":true,\"cover\":false},\"padding\":\"10px\",\"columnsBackgroundColor\":\"\",\"_meta\":{\"htmlID\":\"u_row_1\",\"htmlClassNames\":\"u_row\"},\"selectable\":true,\"draggable\":true,\"deletable\":true}}],\"values\":{\"backgroundColor\":\"#e7e7e7\",\"backgroundImage\":{\"url\":\"\",\"fullWidth\":true,\"repeat\":false,\"center\":true,\"cover\":false},\"contentWidth\":\"500px\",\"fontFamily\":{\"label\":\"Arial\",\"value\":\"arial,helvetica,sans-serif\"},\"_meta\":{\"htmlID\":\"u_body\",\"htmlClassNames\":\"u_body\"}}}}";

    public WebUnlayerTemplateEditor() {
        this.component = new com.haulmont.addon.emailtemplates.web.toolkit.ui.unlayereditorcomponent.UnlayerEditorComponent();
    }

    @Override
    public JsonObject getJson() {
        return new JsonParser().parse(component.getValue().toJson()).getAsJsonObject();
    }

    @Override
    public void setJson(JsonObject json) {
        setJson(json != null ? json.getAsString() : null);
    }

    @Override
    public void setJson(String json) {
        component.setValue(StringUtils.isNotBlank(json) ? new JreJsonFactory().parse(json) : new JreJsonFactory().parse(DEFAULT_TEMPLATE_LAYOUT));
    }

    @Override
    public String getHTML() {
        return component.getHtml();
    }

    @Override
    public void setListener(UnlayerEditorComponent.ValueChangeListener listener) {
        component.setListener(listener);
    }

    @Override
    public void setParameters(Map<String, String> parameters) {
        component.setParameters(parameters);
    }
}