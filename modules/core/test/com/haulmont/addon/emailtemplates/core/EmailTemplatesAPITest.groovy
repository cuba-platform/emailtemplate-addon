package com.haulmont.addon.emailtemplates.core

import com.haulmont.addon.emailtemplates.EmailtemplatesTestContainer
import com.haulmont.addon.emailtemplates.dto.ReportWithParams
import com.haulmont.addon.emailtemplates.entity.EmailTemplate
import com.haulmont.addon.emailtemplates.entity.JsonEmailTemplate
import com.haulmont.addon.emailtemplates.entity.ParameterValue
import com.haulmont.addon.emailtemplates.exceptions.ReportParameterTypeChangedException
import com.haulmont.addon.emailtemplates.exceptions.TemplateNotFoundException
import com.haulmont.cuba.core.global.AppBeans
import com.haulmont.cuba.core.global.EmailInfo
import com.haulmont.reports.entity.ParameterType
import com.haulmont.reports.entity.Report
import com.haulmont.reports.entity.ReportInputParameter
import org.junit.ClassRule
import spock.lang.Shared
import spock.lang.Specification

class EmailTemplatesAPITest extends Specification {

    @ClassRule
    @Shared
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
        parameter.setReport(new Report())
        value = new ParameterValue()
        value.setParameterType(ParameterType.TEXT)
    }

    def "check that method generateEmail with list params throw exception with empty template"() {
        when:
        delegate.generateEmail(template, params as List<ReportWithParams>)

        then:
        thrown(TemplateNotFoundException)

        where:
        template                | params
        null                    | paramsList
        null                    | null
    }

    def "check that method checkParameterTypeChanged throw exception if parameter type was changed"() {
        when:
        delegate.checkParameterTypeChanged(inputParameter, parameterValue)

        then:
        thrown(ReportParameterTypeChangedException)

        where:
        inputParameter             | parameterValue
        parameter                  | new ParameterValue()
        parameter                  | value
    }

    def "check builder methods"() {
        when:
        EmailTemplate template = new JsonEmailTemplate()
        template.setName("Test")
        template.setCode("Test")
        template.setHtml("\${paramValue}")
        template.setTo("address1")
        template.setCc("addressCC")
        template.initReport()
        EmailInfo emailInfo = delegate.buildFromTemplate(template)
                .setSubject("Subject")
                .setBodyParameter("paramValue", "New value")
                .addTo("address2")
                .setCc("newAddressCC")
                .generateEmail()


        then:
        emailInfo.caption == "Subject"
        emailInfo.addresses == "address1, address2"
        emailInfo.cc == "newAddressCC"
        emailInfo.body == "New value"

    }

}
