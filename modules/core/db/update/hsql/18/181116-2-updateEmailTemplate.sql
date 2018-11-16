alter table EMAILTEMPLATES_EMAIL_TEMPLATE add column TYPE_ varchar(50)^

update EMAILTEMPLATES_EMAIL_TEMPLATE set TYPE_ = 'json' where DTYPE='emailtemplates$JsonEmailTemplate';
update EMAILTEMPLATES_EMAIL_TEMPLATE set TYPE_ = 'report' where DTYPE='emailtemplates$ReportEmailTemplate'^

alter table EMAILTEMPLATES_EMAIL_TEMPLATE alter column TYPE_ set not null ;
