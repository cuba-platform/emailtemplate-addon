-- begin EMAILTEMPLATES_EMAIL_TEMPLATE
alter table EMAILTEMPLATES_EMAIL_TEMPLATE add constraint FK_EMAILTEMPLATES_EMAIL_TEMPLATE_GROUP foreign key (GROUP_ID) references EMAILTEMPLATES_TEMPLATE_GROUP(ID)^
alter table EMAILTEMPLATES_EMAIL_TEMPLATE add constraint FK_EMAILTEMPLATES_EMAIL_TEMPLATE_EMAIL_BODY foreign key (EMAIL_BODY_ID) references REPORT_REPORT(ID)^
create index IDX_EMAILTEMPLATES_EMAIL_TEMPLATE_GROUP on EMAILTEMPLATES_EMAIL_TEMPLATE (GROUP_ID)^
create index IDX_EMAILTEMPLATES_EMAIL_TEMPLATE_EMAIL_BODY on EMAILTEMPLATES_EMAIL_TEMPLATE (EMAIL_BODY_ID)^
-- end EMAILTEMPLATES_EMAIL_TEMPLATE
-- begin EMAILTEMPLATES_TEMPLATE_PARAMETER
alter table EMAILTEMPLATES_TEMPLATE_PARAMETER add constraint FK_EMAILTEMPLATES_TEMPLATE_PARAMETER_ON_REPORT foreign key (REPORT_ID) references REPORT_REPORT(ID)^
alter table EMAILTEMPLATES_TEMPLATE_PARAMETER add constraint FK_EMAILTEMPLATES_TEMPLATE_PARAMETER_ON_EMAIL_TEMPLATE foreign key (EMAIL_TEMPLATE_ID) references EMAILTEMPLATES_EMAIL_TEMPLATE(ID)^
create index IDX_EMAILTEMPLATES_TEMPLATE_PARAMETER_ON_REPORT on EMAILTEMPLATES_TEMPLATE_PARAMETER (REPORT_ID)^
create index IDX_EMAILTEMPLATES_TEMPLATE_PARAMETER_ON_EMAIL_TEMPLATE on EMAILTEMPLATES_TEMPLATE_PARAMETER (EMAIL_TEMPLATE_ID)^
-- end EMAILTEMPLATES_TEMPLATE_PARAMETER
-- begin EMAILTEMPLATES_PARAMETER_VALUE
alter table EMAILTEMPLATES_PARAMETER_VALUE add constraint FK_EMAILTEMPLATES_PARAMETER_VALUE_ON_TEMPLATE_PARAMETER foreign key (TEMPLATE_PARAMETER_ID) references EMAILTEMPLATES_TEMPLATE_PARAMETER(ID)^
create index IDX_EMAILTEMPLATES_PARAMETER_VALUE_ON_TEMPLATE_PARAMETER on EMAILTEMPLATES_PARAMETER_VALUE (TEMPLATE_PARAMETER_ID)^
-- end EMAILTEMPLATES_PARAMETER_VALUE
-- begin EMAILTEMPLATES_LAYOUT_EMAIL_TEMPLATE_REPORT_LINK
alter table EMAILTEMPLATES_LAYOUT_EMAIL_TEMPLATE_REPORT_LINK add constraint FK_LAYEMATEMREP_ON_EMAIL_TEMPLATE foreign key (LAYOUT_EMAIL_TEMPLATE_ID) references EMAILTEMPLATES_EMAIL_TEMPLATE(ID)^
alter table EMAILTEMPLATES_LAYOUT_EMAIL_TEMPLATE_REPORT_LINK add constraint FK_LAYEMATEMREP_ON_REPORT foreign key (REPORT_ID) references REPORT_REPORT(ID)^
-- end EMAILTEMPLATES_LAYOUT_EMAIL_TEMPLATE_REPORT_LINK
