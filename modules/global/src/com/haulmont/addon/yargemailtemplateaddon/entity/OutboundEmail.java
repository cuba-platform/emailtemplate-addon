package com.haulmont.addon.yargemailtemplateaddon.entity;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern(" %s %s|from,addressses,template")
@MetaClass(name = "yet$OutboundEmail")
public class OutboundEmail extends BaseUuidEntity {
    private static final long serialVersionUID = 6494336757048633734L;

    @NotNull
    @MetaProperty(mandatory = true)
    protected String from;

    @NotNull
    @MetaProperty(mandatory = true)
    protected String addressses;

    @NotNull
    @MetaProperty(mandatory = true)
    protected LayoutEmailTemplate template;

    public void setTemplate(LayoutEmailTemplate template) {
        this.template = template;
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

    public LayoutEmailTemplate getTemplate() {
        return template;
    }


}