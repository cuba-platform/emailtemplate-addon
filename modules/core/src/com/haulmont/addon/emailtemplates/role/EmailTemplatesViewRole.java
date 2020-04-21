/*
 * Copyright (c) 2008-2020 Haulmont.
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

package com.haulmont.addon.emailtemplates.role;

import com.haulmont.addon.emailtemplates.entity.*;
import com.haulmont.cuba.security.app.role.AnnotatedRoleDefinition;
import com.haulmont.cuba.security.app.role.annotation.*;
import com.haulmont.cuba.security.entity.EntityOp;
import com.haulmont.cuba.security.role.EntityAttributePermissionsContainer;
import com.haulmont.cuba.security.role.EntityPermissionsContainer;
import com.haulmont.cuba.security.role.ScreenComponentPermissionsContainer;
import com.haulmont.cuba.security.role.ScreenPermissionsContainer;

@Role(name = "Can send email templates")
public class EmailTemplatesViewRole extends AnnotatedRoleDefinition {

    @EntityAccess(entityClass = TemplateReport.class, operations = EntityOp.READ)
    @EntityAccess(entityClass = ReportEmailTemplate.class, operations = EntityOp.READ)
    @EntityAccess(entityClass = JsonEmailTemplate.class, operations = EntityOp.READ)
    @EntityAccess(entityClass = EmailTemplate.class, operations = {EntityOp.READ, EntityOp.UPDATE})
    @EntityAccess(entityClass = ParameterValue.class, operations = EntityOp.READ)
    @EntityAccess(entityClass = TemplateGroup.class, operations = EntityOp.READ)
    @Override
    public EntityPermissionsContainer entityPermissions() {
        return super.entityPermissions();
    }

    @EntityAttributeAccess(entityClass = TemplateReport.class, modify = "*")
    @EntityAttributeAccess(entityClass = ReportEmailTemplate.class, modify = "*")
    @EntityAttributeAccess(entityClass = JsonEmailTemplate.class, modify = "*")
    @EntityAttributeAccess(entityClass = EmailTemplate.class, modify = "*")
    @EntityAttributeAccess(entityClass = ParameterValue.class, modify = "*")
    @EntityAttributeAccess(entityClass = TemplateGroup.class, modify = "*")
    @Override
    public EntityAttributePermissionsContainer entityAttributePermissions() {
        return super.entityAttributePermissions();
    }

    @ScreenAccess(screenIds = {"administration", "emailtemplates$EmailTemplate.send", "emailtemplates$EmailTemplate.browse"})
    @Override
    public ScreenPermissionsContainer screenPermissions() {
        return super.screenPermissions();
    }

    @ScreenComponentAccess(screenId = "emailtemplates$EmailTemplate.browse", deny = {"createBtn", "editBtn", "removeBtn", "groupsButton", "blocksButton"})
    @Override
    public ScreenComponentPermissionsContainer screenComponentPermissions() {
        return super.screenComponentPermissions();
    }
}
