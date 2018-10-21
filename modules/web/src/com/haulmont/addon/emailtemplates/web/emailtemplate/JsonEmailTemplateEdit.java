package com.haulmont.addon.emailtemplates.web.emailtemplate;

import com.haulmont.addon.emailtemplates.entity.JsonEmailTemplate;
import com.haulmont.addon.emailtemplates.web.gui.components.UnlayerTemplateEditor;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.FileUploadField;
import com.haulmont.cuba.gui.export.ByteArrayDataProvider;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.upload.FileUploadingAPI;
import org.apache.commons.io.FileUtils;

import javax.inject.Inject;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class JsonEmailTemplateEdit extends AbstractEditor<JsonEmailTemplate> {

    @Inject
    private UnlayerTemplateEditor template;

    @Inject
    protected FileUploadField fileUpload;

    @Inject
    protected FileUploadingAPI fileUploadingApi;

    @Inject
    private ExportDisplay exportDisplay;

    @Override
    protected void postInit() {
        super.postInit();

        template.setJson(getItem().getJsonBody());

        template.setListener(json -> {
            getItem().setJsonBody(json.toJson());
            getItem().setHtml(template.getHTML());
        });

        fileUpload.addFileUploadSucceedListener(fileUploadSucceedEvent -> {
            UUID fileID = fileUpload.getFileId();
            File file = fileUploadingApi.getFile(fileID);
            try {
                byte[] bytes = FileUtils.readFileToByteArray(file);
                fileUploadingApi.deleteFile(fileID);
                getItem().setJsonBody(new String(bytes, StandardCharsets.UTF_8));
                template.setJson(getItem().getJsonBody());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    protected boolean preCommit() {
        getItem().setJsonBody(template.getJson().toString());
        getItem().setHtml(template.getHTML());
        return true;
    }

    public void exportJson() {
        String name = getItem().getName() != null ? getItem().getName() : "template";
        exportDisplay.show(new ByteArrayDataProvider(template.getJson().toString().getBytes()), name + ".json");

    }

    public void exportHtml() {
        openWindow("emailtemplates$htmlSourceCode", WindowManager.OpenType.DIALOG, ParamsMap.of("html", template.getHTML()));
    }

    public void viewHtml() {
        String name = getItem().getName() != null ? getItem().getName() : "template";
        exportDisplay.show(new ByteArrayDataProvider(template.getHTML().getBytes()), name + ".html");
    }


}