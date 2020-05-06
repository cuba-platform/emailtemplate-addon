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
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.security.app.role.annotation.EntityAccess;
import com.haulmont.cuba.security.app.role.annotation.EntityAttributeAccess;
import com.haulmont.cuba.security.app.role.annotation.Role;
import com.haulmont.cuba.security.app.role.annotation.ScreenAccess;
import com.haulmont.cuba.security.entity.EntityOp;
import com.haulmont.cuba.security.role.EntityAttributePermissionsContainer;
import com.haulmont.cuba.security.role.EntityPermissionsContainer;
import com.haulmont.cuba.security.role.ScreenPermissionsContainer;
import com.haulmont.reports.entity.Report;
import com.haulmont.reports.entity.ReportInputParameter;
import com.haulmont.reports.entity.ReportValueFormat;
import com.haulmont.reports.role.ReportsMinimalRoleDefinition;

@Role(name = "email-templates-admin")
public class EmailTemplatesAdminRole extends ReportsMinimalRoleDefinition {

    @Override
    public String getLocName() {
        return "Email templates admin";
    }

    @EntityAccess(entityClass = Report.class,
            operations = {EntityOp.READ})
    @EntityAccess(entityClass = ReportValueFormat.class,
            operations = {EntityOp.CREATE, EntityOp.READ, EntityOp.UPDATE, EntityOp.DELETE})
    @EntityAccess(entityClass = ReportInputParameter.class,
            operations = {EntityOp.CREATE, EntityOp.READ, EntityOp.UPDATE, EntityOp.DELETE})
    @EntityAccess(entityClass = FileDescriptor.class,
            operations = {EntityOp.CREATE, EntityOp.READ, EntityOp.UPDATE, EntityOp.DELETE})
    @EntityAccess(entityClass = TemplateReport.class,
            operations = {EntityOp.CREATE, EntityOp.READ, EntityOp.UPDATE, EntityOp.DELETE})
    @EntityAccess(entityClass = TemplateGroup.class,
            operations = {EntityOp.CREATE, EntityOp.READ, EntityOp.UPDATE, EntityOp.DELETE})
    @EntityAccess(entityClass = TemplateBlockGroup.class,
            operations = {EntityOp.CREATE, EntityOp.READ, EntityOp.UPDATE, EntityOp.DELETE})
    @EntityAccess(entityClass = TemplateBlock.class,
            operations = {EntityOp.CREATE, EntityOp.READ, EntityOp.UPDATE, EntityOp.DELETE})
    @EntityAccess(entityClass = ParameterValue.class,
            operations = {EntityOp.CREATE, EntityOp.READ, EntityOp.UPDATE, EntityOp.DELETE})
    @EntityAccess(entityClass = ReportEmailTemplate.class,
            operations = {EntityOp.CREATE, EntityOp.READ, EntityOp.UPDATE, EntityOp.DELETE})
    @EntityAccess(entityClass = EmailTemplate.class,
            operations = {EntityOp.CREATE, EntityOp.READ, EntityOp.UPDATE, EntityOp.DELETE})
    @EntityAccess(entityClass = JsonEmailTemplate.class,
            operations = {EntityOp.CREATE, EntityOp.READ, EntityOp.UPDATE, EntityOp.DELETE})
    @Override
    public EntityPermissionsContainer entityPermissions() {
        return super.entityPermissions();
    }

    @EntityAttributeAccess(entityClass = ReportValueFormat.class, modify = "*")
    @EntityAttributeAccess(entityClass = ReportInputParameter.class, modify = "*")
    @EntityAttributeAccess(entityClass = Report.class, modify = "*")
    @EntityAttributeAccess(entityClass = FileDescriptor.class, modify = "*")
    @EntityAttributeAccess(entityClass = TemplateReport.class, modify = "*")
    @EntityAttributeAccess(entityClass = TemplateGroup.class, modify = "*")
    @EntityAttributeAccess(entityClass = TemplateBlockGroup.class, modify = "*")
    @EntityAttributeAccess(entityClass = TemplateBlock.class, modify = "*")
    @EntityAttributeAccess(entityClass = ParameterValue.class, modify = "*")
    @EntityAttributeAccess(entityClass = ReportEmailTemplate.class, modify = "*")
    @EntityAttributeAccess(entityClass = EmailTemplate.class, modify = "*")
    @EntityAttributeAccess(entityClass = JsonEmailTemplate.class, modify = "*")
    @Override
    public EntityAttributePermissionsContainer entityAttributePermissions() {
        return super.entityAttributePermissions();
    }

    @ScreenAccess(screenIds = {"administration",
            "emailtemplates$TemplateGroup.edit",
            "emailtemplates$TemplateGroup.browse",
            "emailtemplates_TemplateBlock.edit",
            "emailtemplates_TemplateBlock.browse",
            "emailtemplates_CustomTemplateBlock.edit",
            "emailtemplates_TemplateBlockGroup.browse",
            "emailtemplates$htmlSourceCode",
            "emailtemplates$EmailTemplate.send",
            "emailtemplates$ReportEmailTemplate.edit",
            "emailtemplates$EmailTemplate.browse",
            "emailtemplates$JsonEmailTemplate.edit",
            "sys$FileDescriptor.edit",
            "report$Report.browse",
            "report$inputParameters",
            "report$inputParametersFrame",
            "report$ReportInputParameter.edit",
            "report$ReportValueFormat.edit"})
    @Override
    public ScreenPermissionsContainer screenPermissions() {
        return super.screenPermissions();
    }
}
