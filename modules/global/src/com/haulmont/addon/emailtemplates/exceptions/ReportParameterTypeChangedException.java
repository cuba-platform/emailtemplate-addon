package com.haulmont.addon.emailtemplates.exceptions;

import com.haulmont.cuba.core.global.SupportedByClient;

@SupportedByClient
public class ReportParameterTypeChangedException extends Exception {

    public ReportParameterTypeChangedException() {
    }

    public ReportParameterTypeChangedException(String message) {
        super(message);
    }
}
