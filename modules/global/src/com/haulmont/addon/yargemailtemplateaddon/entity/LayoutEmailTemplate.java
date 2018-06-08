package com.haulmont.addon.yargemailtemplateaddon.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import com.haulmont.reports.entity.Report;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@NamePattern("%s %s %s %s|name,code,group,report")
@Table(name = "YET_LAYOUT_EMAIL_TEMPLATE")
@Entity(name = "yet$LayoutEmailTemplate")
public class LayoutEmailTemplate extends StandardEntity {
    private static final long serialVersionUID = -6290882811419921297L;
    @NotNull
    @Column(name = "NAME", nullable = false)
    protected String name;

    @NotNull
    @Column(name = "TYPE_", nullable = false)
    protected String type;

    @Lookup(type = LookupType.DROPDOWN)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_ID")
    protected TemplateGroup group;

    @ManyToOne(fetch = FetchType.LAZY)
    @Lookup(type = LookupType.DROPDOWN)
    @JoinColumn(name = "REPORT_ID")
    protected Report report;

    @NotNull
    @Column(name = "CODE", nullable = false)
    protected String code;

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }


    public void setType(TemplateType type) {
        this.type = type == null ? null : type.getId();
    }

    public TemplateType getType() {
        return type == null ? null : TemplateType.fromId(type);
    }


    public void setGroup(TemplateGroup group) {
        this.group = group;
    }

    public TemplateGroup getGroup() {
        return group;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }



}