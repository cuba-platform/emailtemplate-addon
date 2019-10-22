<p>
    <a href="http://www.apache.org/licenses/LICENSE-2.0"><img src="https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat" alt="license" title=""></a>
    <a href="https://travis-ci.org/cuba-platform/emailtemplate-addon"><img src="https://travis-ci.org/cuba-platform/emailtemplate-addon.svg?branch=master" alt="Build Status" title=""></a>
</p>

# Email Templates

- [1. Overview](#Overview)
- [2. Installation](#installation)  
  - [2.1. From the Marketplace](#from-the-marketplace)  
  - [2.2. By Coordinates](#by-coordinates)  
- [3. Usage](#usage)  
  - [3.1. Creating Email Templates](#creating-email-templates)  
    - [3.1.1. Creating Email Template From Report](#creating-from-report)  
    - [3.1.2. Creating Email Template From Designer](#creating-from-designer)  
    - [3.1.3. Setting attachments](#setting-attachments)  
  - [3.2. Setting Groups](#setting-groups)  
  - [3.3. Sending Emails](#sending-emails)  
- [4. Email Templates API](#api)  
  - [4.1 Email Templates Builder](#builder)

# 1. Overview <a name="Overview"></a>

The add-on enables creating and configuring outbound email templates containing a constant body and variable parameters. A template is created in the visual HTML designer or by using reports. The add-on provides a visual HTML editor with the extensive set of HTML elements.

Sending emails from templates can be set as a reaction to different events in your application. You can preset recipients, configure parameters and upload attachment files to be sent with emails.

Key features:
- Visual HTML templates builder based on [GrapesJS](https://grapesjs.com/) JavaScript library.
- HTML [reports](https://www.cuba-platform.com/marketplace/reporting/) as a base for outbound emails body.
- Downloading/uploading HTML code of a template.
- User interface for configuring and managing templates.


See [sample application](https://github.com/cuba-platform/emailtemplate-addon-demo) using this add-on.
See [webinar](https://www.youtube.com/watch?v=JqRexrg4mAs) on the CUBA Platform channel.

# 2. Installation <a name="installation"></a>

The add-on can be added to your project in one of the ways described below. Installation from the Marketplace is the simplest way. The last version of the add-on compatible with the used version of the platform will be installed.
Also, you can install the add-on by coordinates choosing the required version of the add-on from the table.

In case you want to install the add-on by manual editing or by building from sources see the complete add-ons installation guide in [CUBA Platform documentation](https://doc.cuba-platform.com/manual-latest/manual.html#app_components_usage).

## 2.1. From the Marketplace <a name="from-the-marketplace"></a>

1. Open your application in CUBA Studio. Check the latest version of CUBA Studio on the [CUBA Platform site](https://www.cuba-platform.com/download/previous-studio/).
2. Go to *CUBA -> Marketplace* in the main menu.

 ![marketplace](img/marketplace.png)

3. Find the *Email Templates* add-on there.

 ![addons](img/addons.png)

4. Click *Install* and apply the changes. The addon corresponding to the used platform version will be installed.

## 2.2. By Сoordinates <a name="by-coordinates"></a>

1. Open your application in CUBA Studio. Check the latest version of CUBA Studio on the [CUBA Platform site](https://www.cuba-platform.com/download/previous-studio/).

2. Go to *CUBA -> Marketplace* in the main menu.

3. Click the icon in the upper-right corner.

 ![by-coordinates](img/by-coordinates.png)

4. Paste the add-on coordinates in the corresponding field as follows:

 `com.haulmont.addon.emailtemplates:yet-global:<add-on version>`

 where `<add-on version>` is compatible with the used version of the CUBA platform.

 | Platform Version  | Component Version |
|-------------------|-------------------|
| 7.1.X             | 1.2.0             |
| 7.0.X             | 1.1.3             |
| 6.10.X            | 1.0.3             |

5. Click *Install* and apply the changes. The add-on will be installed to your project.

# 3. Usage <a name="usage"></a>

You can use the following component features.

## 3.1. Creating email templates <a name="creating-email-templates"></a>

The component enables you to create, edit and remove email templates.

To open *Email template browser* press *Email templates* in the *Administration* menu.

![email-template-menu](img/email-template-menu.png)

There are two ways to create email template: from report and from designer.

![email-template-editor-modes](img/email-template-editor-modes.png)

### 3.1.1. Creating Email Template From Report <a name="creating-from-report"></a>

The following parameters are available for editing:

- the *Name* field;
- the *Code* field;
- the *Group* drop-down;
- the *Use subject from report* checkbox;
- the *Subject* field (if *Use subject from report* unchecked);
- the *From* field;
- the *To* field;
- the *Cc* field;
- the *Bcc* field;
- the *Report template to generate body* lookup field.

![email-template-editor](img/email-template-editor.png)

If the report type is a report with an entity you can set entity for a template. In addition, you can set report parameters.

![email-template-editor-entity](img/email-template-editor-entity.png)

If the report type is a report with entities you can set entities for a template. In addition, you can set report parameters.

![email-template-editor-entities](img/email-template-editor-entities.png)

### 3.1.2. Creating Email Template From Designer <a name="creating-from-designer"></a>

The following parameters are available for editing:

- the *Name* field;
- the *Code* field;
- the *Group* drop-down;
- the *Subject* field;
- the *From* field;
- the *To* field;
- the *Cc* field;
- the *Bcc* field.

The screen contains the following elements:

- the *Import HTML* button;
- the *HTML code* button;
- the *View HTML* button;
- the *Export Report* button;
- the *HTML Editor*.

This type of creating template provides the ability to use HTML editor. You can design a template with different elements and set every element,  using *Setting* panel.

See more information about using the editor in `README` [for GrapesJs HTML editor](https://github.com/cuba-platform/grapesjs-addon/blob/master/README.md).

![email-template-editor-designer](img/email-template-editor-designer.png)

To add parameters and value formats go to the *Parameters and Formats* tab.

![email-template-editor-designer-parameters-formats](img/email-template-editor-designer-parameters-formats.png)

See the complete parameter guide in [CUBA Platform. Report Generator | External Report Parameters](https://doc.cuba-platform.com/reporting-6.10/parameters.html).

See the complete value format guide in [CUBA Platform. Report Generator | Field Value Formats](https://doc.cuba-platform.com/reporting-6.10/formatters.html).

### 3.1.3. Setting attachments <a name="setting-attachments"></a>
You can add or remove attachments on the *Attachments* tab for both types of templates: from report and from designer. You can attach a report or a file.

![email-template-editor-attachment](img/email-template-editor-attachment.png)

You can set the following parameters for a report attachment:

- *File name* - a report attachment name for an addressee;
- an entity or entities for a report;
- parameters from a report.

![email-template-editor-attachment-report-parameters](img/email-template-editor-attachment-report-parameters.png)

## 3.2. Setting Groups <a name="setting-groups"></a>

To open group browser click *Groups* in the *Email templates* browser. The screen enables you to create, edit or remove email template groups.

![email-template-group-browser](img/email-template-group-browser.png)

To create or edit the group enter the name of the group.

![email-template-group-editor](img/email-template-group-editor.png)

After setting groups, you can specify a group for a template.

## 3.3. Sending Emails <a name="sending-emails"></a>

To send an email select a template in the list and click *Send*.

![email-template-list](img/email-template-list.png)

The following parameters are available for editing:

- the *Subject* field;
- the *From* field;
- the *To* field;
- the *Cc* field;
- the *Bcc* field.

The *To* field is required. You can select entity or entities for the report and set report parameter.

![email-template-sending](img/email-template-sending.png)

# 4. Email Templates API <a name="api"></a>

A developer can use the following methods from EmailTemplatesAPI:

1. To create EmailInfo from a template that may contain the same reports with different parameter values:
```java
    EmailInfo generateEmail(EmailTemplate emailTemplate, List<ReportWithParams> params)
```
2. To create EmailInfo by parameters map for all included reports:
```java
    EmailInfo generateEmail(EmailTemplate emailTemplate, Map<String, Object> params)
```
3. To check that the report input parameter did not change its parameter type:
```java
    void checkParameterTypeChanged(ReportInputParameter inputParameter, ParameterValue parameterValue)
```

The `EmailTemplate` entity contains subject, body, and attachments. It also contains from, to, cc, bcc addresses.

The `ReportWithParams` is a wrapper class that represents a report and a map of parameters for that report.

The `ParameterValue` is a class that provides a string representation of the parameter with alias and type.

The `ReportInputParameter` is a class of Reporting component.

The `EmailInfo` is a class of CUBA `EmailService`.

## 4.1. Email Templates Builder <a name="builder"></a>

Email templates API contains builder that can create and fill `EmailTemplate` entity.

`EmailTemplateBuilderImpl` is an implementation of `EmailTemplateBuilder` that provides intermediate methods for
setting and adding email template properties. It also contains terminal methods that can build `EmailTemplate`,
generate or send `EmailInfo`.

A copy of the specified `EmailTemplate` is created in the constructor. Every intermediate method fills the created copy.
```java
    public EmailTemplateBuilderImpl(EmailTemplate emailTemplate) {
        this.emailTemplate = cloneTemplate(emailTemplate);
    }
```
The `build()` method creates the copy from the copy inside builder. It is necessary to save a state of the existed entity or builder.

Example of using the builder:
```java
    EmailTemplate newTemplate = emailTemplatesAPI.buildFromTemplate(emailTemplate)
            .setSubject("Test subject")
            .setTo("address@haulmont.com")
            .setBodyParameter("entity", someEntity)
            .setAttachmentParameters(reportsWithParams)
            .build();
```
