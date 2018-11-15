package com.haulmont.addon.emailtemplates.web.emailtemplate;

import com.haulmont.addon.emailtemplates.entity.EmailTemplate;
import com.haulmont.addon.emailtemplates.entity.TemplateReport;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.gui.components.AbstractEditor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractTemplateEditor<T extends EmailTemplate> extends AbstractEditor<T> {

    protected List<Entity> entityToRemove = new ArrayList<>();

    @Override
    protected boolean preCommit() {
        entityToRemove = new ArrayList<>();
        EmailTemplate original = getDsContext().getDataSupplier().reload(getItem(), "emailTemplate-view");
        if (original != null) {
            EmailTemplate current = getItem();
            List<TemplateReport> obsoleteTemplateReports = original.getAttachedTemplateReports().stream()
                    .filter(e -> !current.getAttachedTemplateReports().contains(e))
                    .collect(Collectors.toList());
            entityToRemove.addAll(obsoleteTemplateReports);
        }
        return true;
    }

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        if (committed) {
            CommitContext commitContext = new CommitContext();
            entityToRemove.forEach(commitContext::addInstanceToRemove);
            getDsContext().getDataSupplier().commit(commitContext);
        }
        return super.postCommit(committed, close);
    }
}
