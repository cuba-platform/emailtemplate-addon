package com.haulmont.addon.emailtemplates.web.emailtemplate;

import com.haulmont.addon.emailtemplates.entity.EmailTemplate;
import com.haulmont.addon.emailtemplates.entity.TemplateReport;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.data.DataSupplier;
import com.haulmont.cuba.gui.model.DataContext;
import com.haulmont.cuba.gui.screen.StandardEditor;
import com.haulmont.cuba.gui.screen.Subscribe;
import com.haulmont.cuba.gui.screen.Target;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractTemplateEditor<T extends EmailTemplate> extends StandardEditor<T> {

    protected List<Entity> entitiesToRemove = new ArrayList<>();
    protected List<Entity> entitiesToUpdate = new ArrayList<>();

    @Inject
    protected DataSupplier dataSupplier;

    @Subscribe(target = Target.DATA_CONTEXT)
    protected void onPreCommit(DataContext.PreCommitEvent event) {
        entitiesToRemove = new ArrayList<>();
        entitiesToUpdate = new ArrayList<>();
        if (!PersistenceHelper.isNew(getEditedEntity())) {
            EmailTemplate original = dataSupplier.reload(getEditedEntity(), "emailTemplate-view");
            EmailTemplate current = getEditedEntity();
            List<TemplateReport> obsoleteTemplateReports = original.getAttachedTemplateReports().stream()
                    .filter(e -> !current.getAttachedTemplateReports().contains(e))
                    .collect(Collectors.toList());
            entitiesToRemove.addAll(obsoleteTemplateReports);
        }
    }

    @Subscribe(target = Target.DATA_CONTEXT)
    protected void onPostCommit(DataContext.PostCommitEvent event) {
        CommitContext commitContext = new CommitContext();
        entitiesToRemove.forEach(commitContext::addInstanceToRemove);
        entitiesToUpdate.forEach(commitContext::addInstanceToCommit);
        dataSupplier.commit(commitContext);
    }
}
