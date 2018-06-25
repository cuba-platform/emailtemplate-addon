package com.haulmont.addon.yargemailtemplateaddon.core.emailtemplateapi;

import com.haulmont.addon.yargemailtemplateaddon.entity.ContentEmailTemplate;
import com.haulmont.addon.yargemailtemplateaddon.entity.LayoutEmailTemplate;
import com.haulmont.cuba.core.global.EmailInfo;

import java.util.List;
import java.util.Map;

public interface EmailTemplateAPI {

    String NAME = "yet_EmailTemplateAPI";

    EmailInfo generateEmail(LayoutEmailTemplate layoutTemplate, List<Map<String, Object>> params);

    EmailInfo generateEmail(ContentEmailTemplate contentTemplate, List<Map<String, Object>> params);

    EmailInfo generateEmail(LayoutEmailTemplate layoutTemplate, String caption, String content);

}
