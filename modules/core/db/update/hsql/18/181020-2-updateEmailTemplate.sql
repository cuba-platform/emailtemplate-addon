alter table EMAILTEMPLATES_EMAIL_TEMPLATE alter column EMAIL_BODY_ID rename to TEMPLATE_REPORT_ID ;
drop index IDX_EMAILTEMPLATES_EMAIL_TEMPLATE_ON_EMAIL_BODY ;
alter table EMAILTEMPLATES_EMAIL_TEMPLATE drop constraint FK_EMAILTEMPLATES_EMAIL_TEMPLATE_ON_EMAIL_BODY ;
alter table EMAILTEMPLATES_EMAIL_TEMPLATE alter column CAPTION rename to SUBJECT ;
alter table EMAILTEMPLATES_EMAIL_TEMPLATE add column DTYPE varchar(100) ;
alter table EMAILTEMPLATES_EMAIL_TEMPLATE add column FROM_ varchar(255) ;
alter table EMAILTEMPLATES_EMAIL_TEMPLATE add column TO_ longvarchar ;
alter table EMAILTEMPLATES_EMAIL_TEMPLATE add column CC longvarchar ;
alter table EMAILTEMPLATES_EMAIL_TEMPLATE add column BCC longvarchar ;
alter table EMAILTEMPLATES_EMAIL_TEMPLATE add column USE_REPORT_SUBJECT boolean ;
alter table EMAILTEMPLATES_EMAIL_TEMPLATE add column JSON_TEMPLATE longvarchar ;
alter table EMAILTEMPLATES_EMAIL_TEMPLATE add column HTML longvarchar ;