package com.haulmont.addon.emailtemplates.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import com.haulmont.reports.entity.Report;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@NamePattern("%s %s %s %s|name,code,group,emailBody")
@Table(name = "EMAILTEMPLATES_EMAIL_TEMPLATE")
@Entity(name = "emailtemplates$EmailTemplate")
public class EmailTemplate extends StandardEntity {
    private static final long serialVersionUID = -6290882811419921297L;
    @NotNull
    @Column(name = "NAME", nullable = false)
    protected String name;


    @Lookup(type = LookupType.DROPDOWN)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_ID")
    protected TemplateGroup group;


    @Lookup(type = LookupType.DROPDOWN)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "EMAIL_BODY_ID")
    protected Report emailBody;

    @JoinTable(name = "EMAILTEMPLATES_LAYOUT_EMAIL_TEMPLATE_REPORT_LINK",
        joinColumns = @JoinColumn(name = "LAYOUT_EMAIL_TEMPLATE_ID"),
        inverseJoinColumns = @JoinColumn(name = "REPORT_ID"))
    @ManyToMany
    protected List<Report> attachments;

    @NotNull
    @Column(name = "CODE", nullable = false)
    protected String code;


    public void setAttachments(List<Report> attachments) {
        this.attachments = attachments;
    }

    public List<Report> getAttachments() {
        return attachments;
    }


    public void setEmailBody(Report emailBody) {
        this.emailBody = emailBody;
    }

    public Report getEmailBody() {
        return emailBody;
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