package com.haulmont.addon.emailtemplates.core

import com.haulmont.addon.emailtemplates.EmailtemplatesTestContainer
import com.haulmont.addon.emailtemplates.dto.ReportWithParams
import com.haulmont.addon.emailtemplates.entity.EmailTemplate
import com.haulmont.addon.emailtemplates.entity.ParameterValue
import com.haulmont.addon.emailtemplates.exceptions.ReportParameterTypeChangedException
import com.haulmont.addon.emailtemplates.exceptions.TemplateNotFoundException
import com.haulmont.cuba.core.global.AppBeans
import com.haulmont.reports.entity.ParameterType
import com.haulmont.reports.entity.ReportInputParameter
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
    @Shared
    ReportInputParameter parameter
    @Shared
    ParameterValue value

    @Shared
    EmailTemplatesAPI delegate

    void setupSpec() {
        delegate = AppBeans.get(EmailTemplates)
        paramsMap = new HashMap<>()
        paramsList = new ArrayList<>()

        parameter = new ReportInputParameter()
        parameter.setType(ParameterType.ENTITY)
        value = new ParameterValue()
        value.setParameterType(ParameterType.TEXT)
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

    def "check that method checkParameterTypeChanged throw exception if parameter type was changed"() {
        when:
        delegate.checkParameterTypeChanged(inputParameter, parameterValue)

        then:
        thrown(ReportParameterTypeChangedException)

        where:
        inputParameter             | parameterValue
        new ReportInputParameter() | new ParameterValue()
        parameter                  | new ParameterValue()
        parameter                  | value
    }

}
