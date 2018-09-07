package com.haulmont.addon.emailtemplates.handlers;

import com.haulmont.addon.emailtemplates.exceptions.TemplateNotFoundException;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.exception.AbstractGenericExceptionHandler;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.inject.Inject;

@Component
public class TemplateNotFoundExceptionHandler extends AbstractGenericExceptionHandler {

    @Inject
    private Logger log;

    public TemplateNotFoundExceptionHandler() {
        super(TemplateNotFoundException.class.getName());
    }

    @Override
    protected void doHandle(String className, String message, @Nullable Throwable throwable, WindowManager windowManager) {
        windowManager.showNotification(message);
        log.warn(message);
    }
}
