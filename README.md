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

##Email templates API

Developer can use the next methods from EmailTemplatesAPI:
1. To create EmailInfo from template that may contain the same reports different parameter values
```java
    EmailInfo generateEmail(EmailTemplate emailTemplate, List<ReportWithParams> params)
```
2. To create EmailInfo by parameters map for all included reports
```java
    EmailInfo generateEmail(EmailTemplate emailTemplate, Map<String, Object> params)
```
2. To check that the report input parameter did not change own parameter type
```java
    void checkParameterTypeChanged(ReportInputParameter inputParameter, ParameterValue parameterValue)
```