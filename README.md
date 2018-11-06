# YARG email template component

This component provide ability to create outbound email based on generating by YARG reporting templates.

## Email template

This functionality of the component allows to create templates of outbound emails.

![](img/templates_browse.png)

User may set report as email body. But template may contain one or more reports as attachments.
Also user may set default parameter values to all included reports. User have to use only HTML report templates.

![](img/templates_edit.png)

## Outbound email

By the outbound email editor user may set sender and recipient addresses and fill parameters of all reports.
If email template does not contain any reports user will see exception message.

![](img/outbound_email.png)

Before sending is displayed preview screen.

## Email templates API

Developer can use the next methods from EmailTemplatesAPI:
1. To create EmailInfo from template that may contain the same reports with different parameter values
```
    EmailInfo generateEmail(EmailTemplate emailTemplate, List<ReportWithParams> params)
```
2. To create EmailInfo by parameters map for all included reports
```
    EmailInfo generateEmail(EmailTemplate emailTemplate, Map<String, Object> params)
```
3. To check that the report input parameter did not change own parameter type
```
    void checkParameterTypeChanged(ReportInputParameter inputParameter, ParameterValue parameterValue)
```

The EmailTemplate entity contains subject, body and attachments. It also contains from, to, cc, bcc addresses.

The ReportWithParams class is a wrapper for report a map of parameters for that report.

The ParameterValue class is the storage for string representation of default value of parameter with alias and type.

The ReportInputParameter is a class of reporting component.

The EmailInfo is a one of the classes of cuba email service.

#### Email templates builder

Email templates API contains builder that can create and fill EmailTemplate entity.
EmailTemplateBuilderImpl is implementation of EmailTemplateBuilder that provides intermediate methods for 
setting and adding email template properties. It also contains terminal methods that can build EmailTemplate,
generate or send EmailInfo.

In constructor is created a copy of specified EmailTemplate. Everything intermediate methods fill created copy.
```
    public EmailTemplateBuilderImpl(EmailTemplate emailTemplate) {
        this.emailTemplate = cloneTemplate(emailTemplate);
    }
```
The build() method create the copy from the copy inside builder. It necessary to save state of existed entity or builder.

Example of using the builder:
```
    EmailTemplate newTemplate = emailTemplatesAPI.buildFromTemplate(emailTemplate)
            .setSubject("Test subject")
            .setTo("address@haulmont.com")
            .setBodyParameter("entity", someEntity)
            .setAttachmentParameters(reportsWithParams)
            .build();
```