package com.haulmont.addon.emailtemplates.exceptions;

import com.haulmont.cuba.core.global.SupportedByClient;

@SupportedByClient
public class TemplateNotFoundException extends Exception {

    public TemplateNotFoundException() {
    }

    public TemplateNotFoundException(String message) {
        super(message);
    }
}
