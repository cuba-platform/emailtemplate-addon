alter table EMAILTEMPLATES_TEMPLATE_PARAMETER rename to EMAILTEMPLATES_TEMPLATE_REPORT ;
alter table EMAILTEMPLATES_PARAMETER_VALUE drop constraint FK_EMAILTEMPLATES_PARAMETER_VALUE_ON_TEMPLATE_PARAMETER ;

