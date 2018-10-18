package com.haulmont.addon.emailtemplates.entity;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.reports.entity.Report;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@NamePattern("%s %s %s %s|name,code,group,emailBody")
@Table(name = "EMAILTEMPLATES_EMAIL_TEMPLATE")
@Entity(name = "emailtemplates$EmailTemplate")
public abstract class EmailTemplate extends StandardEntity {

    private static final long serialVersionUID = -6290882811419921297L;

    @NotNull
    @Column(name = "NAME", nullable = false)
    protected String name;

    @Lookup(type = LookupType.DROPDOWN)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_ID")
    protected TemplateGroup group;

    @NotNull
    @Column(name = "TYPE_", nullable = false)
    protected String type;

    @NotNull
    @Column(name = "CODE", nullable = false)
    protected String code;


    @Column(name = "FROM_")
    protected String from;

    @Lob
    @Column(name = "TO_")
    protected String to;

    @Lob
    @Column(name = "CC")
    protected String cc;

    @Lob
    @Column(name = "BCC")
    protected String bcc;

    @Column(name = "SUBJECT")
    protected String subject;

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

    public void setType(TemplateType type) {
        this.type = type == null ? null : type.getId();
    }

    public TemplateType getType() {
        return type == null ? null : TemplateType.fromId(type);
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getFrom() {
        return from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getTo() {
        return to;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getCc() {
        return cc;
    }

    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    public String getBcc() {
        return bcc;
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

    public abstract Report getEmailBodyReport();
}