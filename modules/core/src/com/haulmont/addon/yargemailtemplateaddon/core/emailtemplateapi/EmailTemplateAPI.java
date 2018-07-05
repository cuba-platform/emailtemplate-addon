package com.haulmont.addon.yargemailtemplateaddon.core.emailtemplateapi;

import com.haulmont.addon.yargemailtemplateaddon.dto.ReportWithParams;
import com.haulmont.addon.yargemailtemplateaddon.entity.ContentEmailTemplate;
import com.haulmont.addon.yargemailtemplateaddon.entity.LayoutEmailTemplate;
import com.haulmont.cuba.core.global.EmailInfo;

import java.util.List;

public interface EmailTemplateAPI {

    String NAME = "yet_EmailTemplateAPI";

    EmailInfo generateEmail(LayoutEmailTemplate layoutTemplate, List<ReportWithParams> params);

    EmailInfo generateEmail(ContentEmailTemplate contentTemplate, List<ReportWithParams> params);

    EmailInfo generateEmail(LayoutEmailTemplate layoutTemplate, String caption, String content);

}
