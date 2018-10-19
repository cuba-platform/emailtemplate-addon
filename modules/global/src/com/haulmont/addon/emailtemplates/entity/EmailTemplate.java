package com.haulmont.addon.emailtemplates.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import com.haulmont.reports.entity.Report;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;

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


    @NotNull
    @Column(name = "CODE", nullable = false, unique = true)
    protected String code;


    @Column(name = "SENDER")
    protected String sender;

    @Column(name = "ADDRESSES")
    protected String addresses;

    @Column(name = "SUBJECT")
    protected String subject;

    @OnDeleteInverse(DeletePolicy.UNLINK)
    @Lookup(type = LookupType.SCREEN, actions = {"lookup"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMAIL_BODY_ID")
    protected Report emailBody;

    @OnDeleteInverse(DeletePolicy.UNLINK)
    @JoinTable(name = "EMAILTEMPLATES_LAYOUT_EMAIL_TEMPLATE_REPORT_LINK",
        joinColumns = @JoinColumn(name = "LAYOUT_EMAIL_TEMPLATE_ID"),
        inverseJoinColumns = @JoinColumn(name = "REPORT_ID"))
    @ManyToMany
    protected List<Report> attachments;



    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "emailTemplate")
    protected List<TemplateParameter> parameters;

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSender() {
        return sender;
    }

    public void setAddresses(String addresses) {
        this.addresses = addresses;
    }

    public String getAddresses() {
        return addresses;
    }


    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }


    public void setParameters(List<TemplateParameter> parameters) {
        this.parameters = parameters;
    }

    public List<TemplateParameter> getParameters() {
        return parameters;
    }


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