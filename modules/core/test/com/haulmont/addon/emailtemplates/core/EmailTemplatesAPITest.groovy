package com.haulmont.addon.emailtemplates.core

import com.haulmont.addon.emailtemplates.EmailtemplatesTestContainer
import com.haulmont.addon.emailtemplates.dto.ReportWithParams
import com.haulmont.addon.emailtemplates.entity.EmailTemplate
import com.haulmont.addon.emailtemplates.exceptions.TemplateNotFoundException
import com.haulmont.cuba.core.global.AppBeans
import org.junit.ClassRule
import spock.lang.Shared
import spock.lang.Specification

class EmailTemplatesAPITest extends Specification {

    @ClassRule @Shared
    EmailtemplatesTestContainer container = EmailtemplatesTestContainer.Common.INSTANCE

    @Shared
    Map<String, Object> paramsMap
    @Shared
    List<ReportWithParams> paramsList

    EmailTemplatesAPI delegate

    void setup() {
        delegate = AppBeans.get(EmailTemplates)
        paramsMap = new HashMap<>()
        paramsList = new ArrayList<>()
    }

    def "check that method generateEmail with list params throw exception with empty template"() {
        when:
        delegate.generateEmail(template, params as List<ReportWithParams>)

        then:
        thrown(TemplateNotFoundException)

        where:
        template             | params
        null                 | paramsList
        new EmailTemplate()  | paramsList
        null                 | null
        new EmailTemplate()  | null
    }

    def "check that method generateEmail with map params throw exception with empty template"() {
        when:
        delegate.generateEmail(template, params as Map<String, Object>)

        then:
        thrown(TemplateNotFoundException)

        where:
        template             | params
        null                 | paramsMap
        new EmailTemplate()  | paramsMap
        null                 | null
        new EmailTemplate()  | null
    }

}
