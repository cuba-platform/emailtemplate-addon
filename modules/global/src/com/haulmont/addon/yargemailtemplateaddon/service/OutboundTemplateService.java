package com.haulmont.addon.yargemailtemplateaddon.service;


import com.haulmont.addon.yargemailtemplateaddon.entity.ContentEmailTemplate;
import com.haulmont.addon.yargemailtemplateaddon.entity.LayoutEmailTemplate;
import com.haulmont.cuba.core.global.EmailInfo;

import java.util.List;
import java.util.Map;

public interface OutboundTemplateService {
    String NAME = "yet_OutboundTemplateService";


    EmailInfo generateMessageByTemplate(LayoutEmailTemplate emailTemplate, String addresses, String from, List<Map<String, Object>> param);

    EmailInfo generateMessageByContentTemplate(ContentEmailTemplate emailTemplate, String addresses, String from, List<Map<String, Object>> param);

    EmailInfo generateMessageByTemplateWithCustomParams(LayoutEmailTemplate emailTemplate, String addresses, String from, List<Map<String, Object>> param);
}