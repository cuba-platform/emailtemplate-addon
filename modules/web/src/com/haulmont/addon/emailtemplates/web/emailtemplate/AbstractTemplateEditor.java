/*
 * Copyright (c) 2008-2019 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.haulmont.addon.emailtemplates.web.emailtemplate;

import com.haulmont.addon.emailtemplates.entity.EmailTemplate;
import com.haulmont.addon.emailtemplates.entity.TemplateReport;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.components.AbstractEditor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractTemplateEditor<T extends EmailTemplate> extends AbstractEditor<T> {

    protected List<Entity> entitiesToRemove = new ArrayList<>();
    protected List<Entity> entitiesToUpdate = new ArrayList<>();

    @Override
    protected boolean preCommit() {
        entitiesToRemove = new ArrayList<>();
        entitiesToUpdate = new ArrayList<>();
        if (!PersistenceHelper.isNew(getItem())) {
            EmailTemplate original = getDsContext().getDataSupplier().reload(getItem(), "emailTemplate-view");
            EmailTemplate current = getItem();
            List<TemplateReport> obsoleteTemplateReports = original.getAttachedTemplateReports().stream()
                    .filter(e -> !current.getAttachedTemplateReports().contains(e))
                    .collect(Collectors.toList());
            entitiesToRemove.addAll(obsoleteTemplateReports);
        }
        return true;
    }

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        if (committed) {
            CommitContext commitContext = new CommitContext();
            entitiesToRemove.forEach(commitContext::addInstanceToRemove);
            entitiesToUpdate.forEach(commitContext::addInstanceToCommit);
            getDsContext().getDataSupplier().commit(commitContext);
        }
        return super.postCommit(committed, close);
    }
}
