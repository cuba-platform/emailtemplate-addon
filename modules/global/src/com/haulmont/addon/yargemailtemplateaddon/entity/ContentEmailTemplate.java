package com.haulmont.addon.yargemailtemplateaddon.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.reports.entity.Report;

import javax.persistence.*;
import java.util.List;
import com.haulmont.cuba.core.entity.StandardEntity;

@NamePattern("%s    |name,code,group,report,attachments")
@Entity(name = "yet$ContentEmailTemplate")
public class ContentEmailTemplate extends LayoutEmailTemplate {
    private static final long serialVersionUID = -1797196186118627176L;

    @JoinTable(name = "YET_CONTENT_EMAIL_TEMPLATE_REPORT_LINK",
        joinColumns = @JoinColumn(name = "CONTENT_EMAIL_TEMPLATE_ID"),
        inverseJoinColumns = @JoinColumn(name = "REPORT_ID"))
    @ManyToMany
    protected List<Report> attachments;

    public void setAttachments(List<Report> attachments) {
        this.attachments = attachments;
    }

    public List<Report> getAttachments() {
        return attachments;
    }


}