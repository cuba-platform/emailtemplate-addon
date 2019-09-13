-- begin EMAILTEMPLATES_EMAIL_TEMPLATE
alter table EMAILTEMPLATES_EMAIL_TEMPLATE add constraint FK_EMAILTEMPLATES_EMAIL_TEMPLATE_GROUP foreign key (GROUP_ID) references EMAILTEMPLATES_TEMPLATE_GROUP(ID)^
alter table EMAILTEMPLATES_EMAIL_TEMPLATE add constraint FK_EMAILTEMPLATES_EMAIL_TEMPLATE_EMAIL_BODY_REPORT foreign key (EMAIL_BODY_REPORT_ID) references EMAILTEMPLATES_TEMPLATE_REPORT(ID)^
create unique index IDX_EMAILTEMPLATES_EMAIL_TEMPLATE_UNIQ_CODE on EMAILTEMPLATES_EMAIL_TEMPLATE (CODE) ^
create index IDX_EMAILTEMPLATES_EMAIL_TEMPLATE_GROUP on EMAILTEMPLATES_EMAIL_TEMPLATE (GROUP_ID)^
-- end EMAILTEMPLATES_EMAIL_TEMPLATE

-- begin EMAILTEMPLATES_PARAMETER_VALUE
alter table EMAILTEMPLATES_PARAMETER_VALUE add constraint FK_EMAILTEMPLATES_PARAMETER_VALUE_ON_TEMPLATE_PARAMETER foreign key (TEMPLATE_PARAMETER_ID) references EMAILTEMPLATES_TEMPLATE_REPORT(ID)^
create index IDX_EMAILTEMPLATES_PARAMETER_VALUE_ON_TEMPLATE_PARAMETER on EMAILTEMPLATES_PARAMETER_VALUE (TEMPLATE_PARAMETER_ID)^
-- end EMAILTEMPLATES_PARAMETER_VALUE

-- begin EMAILTEMPLATES_EMLTPTE_FDLINK
alter table EMAILTEMPLATES_EMLTPTE_FDLINK add constraint FK_EMATEMFILDES_ON_EMAIL_TEMPLATE foreign key (EMAIL_TEMPLATE_ID) references EMAILTEMPLATES_EMAIL_TEMPLATE(ID)^
alter table EMAILTEMPLATES_EMLTPTE_FDLINK add constraint FK_EMATEMFILDES_ON_FILE_DESCRIPTOR foreign key (FILE_DESCRIPTOR_ID) references SYS_FILE(ID)^
-- end EMAILTEMPLATES_EMLTPTE_FDLINK
-- begin EMAILTEMPLATES_TEMPLATE_REPORT
alter table EMAILTEMPLATES_TEMPLATE_REPORT add constraint FK_EMAILTEMPLATES_TEMPLATE_REPORT_ON_REPORT foreign key (REPORT_ID) references REPORT_REPORT(ID)^
alter table EMAILTEMPLATES_TEMPLATE_REPORT add constraint FK_EMAILTEMPLATES_TEMPLATE_REPORT_ON_EMAIL_TEMPLATE foreign key (EMAIL_TEMPLATE_ID) references EMAILTEMPLATES_EMAIL_TEMPLATE(ID)^
create index IDX_EMAILTEMPLATES_TEMPLATE_REPORT_ON_REPORT on EMAILTEMPLATES_TEMPLATE_REPORT (REPORT_ID)^
create index IDX_EMAILTEMPLATES_TEMPLATE_REPORT_ON_EMAIL_TEMPLATE on EMAILTEMPLATES_TEMPLATE_REPORT (EMAIL_TEMPLATE_ID)^
-- end EMAILTEMPLATES_TEMPLATE_REPORT
-- begin EMAILTEMPLATES_TEMPLATE_BLOCK
alter table EMAILTEMPLATES_TEMPLATE_BLOCK add constraint FK_EMAILTEMPLATES_TEMPLATE_BLOCK_CATEGORY foreign key (CATEGORY_ID) references EMAILTEMPLATES_BLOCK_GROUP(ID)^
create unique index IDX_EMAILTEMPLATES_TEMPLATE_BLOCK_UNIQ_NAME on EMAILTEMPLATES_TEMPLATE_BLOCK (NAME) ^
create index IDX_EMAILTEMPLATES_TEMPLATE_BLOCK_CATEGORY on EMAILTEMPLATES_TEMPLATE_BLOCK (CATEGORY_ID)^
-- end EMAILTEMPLATES_TEMPLATE_BLOCK
-- begin EMAILTEMPLATES_TEMPLATE_BLOCK
create unique index IDX_EMAILTEMPLATES_BLOCK_GROUP_UNIQ_NAME on EMAILTEMPLATES_BLOCK_GROUP (NAME) ^
-- end EMAILTEMPLATES_TEMPLATE_BLOCK
