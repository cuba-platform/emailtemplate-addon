package com.haulmont.addon.emailtemplates.entity;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;

import javax.validation.constraints.NotNull;

@NamePattern(" %s %s|from,addressses,layoutTemplate,contentTemplate")
@MetaClass(name = "yet$OutboundEmail")
public class OutboundEmail extends BaseUuidEntity {
    private static final long serialVersionUID = 6494336757048633734L;

    @NotNull
    @MetaProperty(mandatory = true)
    protected String from;

    @NotNull
    @MetaProperty(mandatory = true)
    protected String addressses;


    @Lookup(type = LookupType.DROPDOWN)
    @MetaProperty
    protected LayoutEmailTemplate layoutTemplate;

    @MetaProperty
    protected ContentEmailTemplate contentTemplate;

    public void setContentTemplate(ContentEmailTemplate contentTemplate) {
        this.contentTemplate = contentTemplate;
    }

    public ContentEmailTemplate getContentTemplate() {
        return contentTemplate;
    }


    public void setLayoutTemplate(LayoutEmailTemplate layoutTemplate) {
        this.layoutTemplate = layoutTemplate;
    }

    public LayoutEmailTemplate getLayoutTemplate() {
        return layoutTemplate;
    }


    public void setFrom(String from) {
        this.from = from;
    }


    public String getFrom() {
        return from;
    }

    public void setAddressses(String addressses) {
        this.addressses = addressses;
    }

    public String getAddressses() {
        return addressses;
    }


}