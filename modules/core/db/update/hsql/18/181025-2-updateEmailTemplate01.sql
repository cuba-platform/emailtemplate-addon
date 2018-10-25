alter table EMAILTEMPLATES_EMAIL_TEMPLATE add constraint FK_EMAILTEMPLATES_EMAIL_TEMPLATE_ON_EMAIL_BODY_REPORT foreign key (EMAIL_BODY_REPORT_ID) references EMAILTEMPLATES_TEMPLATE_REPORT(ID);
create index IDX_EMAILTEMPLATES_EMAIL_TEMPLATE_ON_EMAIL_BODY_REPORT on EMAILTEMPLATES_EMAIL_TEMPLATE (EMAIL_BODY_REPORT_ID);
