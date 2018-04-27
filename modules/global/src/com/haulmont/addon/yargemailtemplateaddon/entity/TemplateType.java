package com.haulmont.addon.yargemailtemplateaddon.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum TemplateType implements EnumClass<String> {

    LAYOUT("Layout"),
    CONTENT("Content");

    private String id;

    TemplateType(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static TemplateType fromId(String id) {
        for (TemplateType at : TemplateType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}