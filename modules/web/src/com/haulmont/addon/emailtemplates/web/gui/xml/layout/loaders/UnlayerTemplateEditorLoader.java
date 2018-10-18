package com.haulmont.addon.emailtemplates.web.gui.xml.layout.loaders;

import com.haulmont.addon.emailtemplates.web.gui.components.UnlayerTemplateEditor;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.xml.layout.loaders.AbstractComponentLoader;

public class UnlayerTemplateEditorLoader extends AbstractComponentLoader<UnlayerTemplateEditor> {
    @Override
    public void createComponent() {
        resultComponent = factory.createComponent(UnlayerTemplateEditor.class);
        loadId(resultComponent, element);
    }

    @Override
    public void loadComponent() {
        assignFrame(resultComponent);
        loadWidth(resultComponent, element, Component.AUTO_SIZE);
        loadHeight(resultComponent, element, Component.AUTO_SIZE);

        loadVisible(resultComponent, element);
        loadEnable(resultComponent, element);
        loadStyleName(resultComponent, element);


    }
}
