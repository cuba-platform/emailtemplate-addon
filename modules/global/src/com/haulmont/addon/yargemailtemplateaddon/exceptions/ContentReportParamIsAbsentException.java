package com.haulmont.addon.yargemailtemplateaddon.exceptions;

public class ContentReportParamIsAbsentException extends Exception {

    public ContentReportParamIsAbsentException() {
    }

    public ContentReportParamIsAbsentException(String message) {
        super(message);
    }

    public ContentReportParamIsAbsentException(String message, Throwable cause) {
        super(message, cause);
    }
}
