alter table YET_CONTENT_EMAIL_TEMPLATE add constraint FK_YET_CONTENT_EMAIL_TEMPLATE_ON_GROUP foreign key (GROUP_ID) references REPORT_GROUP(ID);
alter table YET_CONTENT_EMAIL_TEMPLATE add constraint FK_YET_CONTENT_EMAIL_TEMPLATE_ON_REPORT foreign key (REPORT_ID) references REPORT_REPORT(ID);
create index IDX_YET_CONTENT_EMAIL_TEMPLATE_ON_GROUP on YET_CONTENT_EMAIL_TEMPLATE (GROUP_ID);
create index IDX_YET_CONTENT_EMAIL_TEMPLATE_ON_REPORT on YET_CONTENT_EMAIL_TEMPLATE (REPORT_ID);
