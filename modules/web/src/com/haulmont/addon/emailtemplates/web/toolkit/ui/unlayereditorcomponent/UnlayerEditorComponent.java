package com.haulmont.addon.emailtemplates.web.toolkit.ui.unlayereditorcomponent;

import com.vaadin.annotations.JavaScript;
import com.vaadin.ui.AbstractJavaScriptComponent;
import elemental.json.JsonObject;

import java.util.Map;
import java.util.stream.Collectors;

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
            if (listener != null) {
                listener.valueChanged(json);
            }
        });

        addFunction("fileUploaded", arguments -> {
            String fileName = arguments.getString(0);
            String fileBase64 = arguments.getString(1);

            if (fileUploadListener != null) {
                fileUploadListener.fileUploaded(fileName, fileBase64);
            }
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

    public interface FileUploadListener {
        void fileUploaded(String name, String fileBase64);
    }

    private ValueChangeListener listener;

    private FileUploadListener fileUploadListener;

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

    public FileUploadListener getFileUploadListener() {
        return fileUploadListener;
    }

    public void setFileUploadListener(FileUploadListener fileUploadListener) {
        this.fileUploadListener = fileUploadListener;
    }

    public void setParameters(Map<String, String> parameters) {
        getState().parameters = parameters.entrySet().stream()
                .map(e -> new NameValue(e.getKey(), "${" + e.getValue() + "}"))
                .collect(Collectors.toList());
    }
}