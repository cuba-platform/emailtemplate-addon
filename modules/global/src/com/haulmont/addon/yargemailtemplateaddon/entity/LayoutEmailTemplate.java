package com.haulmont.addon.yargemailtemplateaddon.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.InheritanceType;
import javax.persistence.Inheritance;

@Table(name = "YET_LAYOUT_EMAIL_TEMPLATE")
@Entity(name = "yet$LayoutEmailTemplate")
public class LayoutEmailTemplate extends EmailTemplate {
    private static final long serialVersionUID = -6290882811419921297L;

}