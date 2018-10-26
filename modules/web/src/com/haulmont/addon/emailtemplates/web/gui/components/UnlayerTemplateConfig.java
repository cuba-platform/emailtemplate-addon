package com.haulmont.addon.emailtemplates.web.gui.components;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;

@Source(type = SourceType.APP)
public interface UnlayerTemplateConfig extends Config {

    @Property("cuba.addon.emailtemplates.unlayer.projectId")
    Integer getProjectId();
}
