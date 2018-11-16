alter table EMAILTEMPLATES_EMAIL_TEMPLATE add column TYPE varchar(50) not null;

update EMAILTEMPLATES_EMAIL_TEMPLATE set TYPE = 'json' where DTYPE='emailtemplates$JsonEmailTemplate';
update EMAILTEMPLATES_EMAIL_TEMPLATE set TYPE = 'report' where DTYPE='emailtemplates$ReportEmailTemplate';