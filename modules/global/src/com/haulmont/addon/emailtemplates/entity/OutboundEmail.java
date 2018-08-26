package com.haulmont.addon.emailtemplates.entity;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;

import javax.validation.constraints.NotNull;

@NamePattern(" %s %s|from,addresses,emailTemplate,contentTemplate")
@MetaClass(name = "emailtemplates$OutboundEmail")
public class OutboundEmail extends BaseUuidEntity {
    private static final long serialVersionUID = 6494336757048633734L;

    @NotNull
    @MetaProperty(mandatory = true)
    protected String from;


    @NotNull
    @MetaProperty(mandatory = true)
    protected String addresses;

    @Lookup(type = LookupType.DROPDOWN)
    @MetaProperty
    protected EmailTemplate emailTemplate;

    public void setAddresses(String addresses) {
        this.addresses = addresses;
    }

    public String getAddresses() {
        return addresses;
    }


    public void setEmailTemplate(EmailTemplate emailTemplate) {
        this.emailTemplate = emailTemplate;
    }

    public EmailTemplate getEmailTemplate() {
        return emailTemplate;
    }


    public void setFrom(String from) {
        this.from = from;
    }

    public String getFrom() {
        return from;
    }




}