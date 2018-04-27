package com.haulmont.addon.yargemailtemplateaddon.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s|name")
@Table(name = "YET_TEMPLATE_GROUP")
@Entity(name = "yet$TemplateGroup")
public class TemplateGroup extends StandardEntity {
    private static final long serialVersionUID = -8663738904983023904L;

    @NotNull
    @Column(name = "NAME", nullable = false)
    protected String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


}