package com.haulmont.addon.emailtemplates.exceptions;

public class TemplateNotFoundException extends Exception {

    public TemplateNotFoundException() {
    }

    public TemplateNotFoundException(String message) {
        super(message);
    }
}
