-- begin EMAILTEMPLATES_EMAIL_TEMPLATE
alter table EMAILTEMPLATES_EMAIL_TEMPLATE add constraint FK_EMAILTEMPLATES_EMATEM_GRO foreign key (GROUP_ID) references EMAILTEMPLATES_TEMPLATE_GROUP(ID)^
alter table EMAILTEMPLATES_EMAIL_TEMPLATE add constraint FK_EMAILTEMPLATES_EMTE_EMBORE foreign key (EMAIL_BODY_REPORT_ID) references EMAILTEMPLATES_TEMPLATE_REPORT(ID)^
create unique index IDX_EMAILTEMPLATES_EMTE_UK_CO on EMAILTEMPLATES_EMAIL_TEMPLATE (CODE, DELETE_TS) ^
create index IDX_EMAILTEMPLATES_EMATEM_GRO on EMAILTEMPLATES_EMAIL_TEMPLATE (GROUP_ID)^
-- end EMAILTEMPLATES_EMAIL_TEMPLATE
-- begin EMAILTEMPLATES_TEMPLATE_REPORT
alter table EMAILTEMPLATES_TEMPLATE_REPORT add constraint FK_EMAILTEMPLATES_TEMREP_REP foreign key (REPORT_ID) references REPORT_REPORT(ID)^
alter table EMAILTEMPLATES_TEMPLATE_REPORT add constraint FK_EMAILTEMPLATES_TERE_EMTE foreign key (EMAIL_TEMPLATE_ID) references EMAILTEMPLATES_EMAIL_TEMPLATE(ID)^
create index IDX_EMAILTEMPLATES_TEMREP_REP on EMAILTEMPLATES_TEMPLATE_REPORT (REPORT_ID)^
create index IDX_EMAILTEMPLATES_TERE_EMTE on EMAILTEMPLATES_TEMPLATE_REPORT (EMAIL_TEMPLATE_ID)^
-- end EMAILTEMPLATES_TEMPLATE_REPORT
-- begin EMAILTEMPLATES_PARAMETER_VALUE
alter table EMAILTEMPLATES_PARAMETER_VALUE add constraint FK_EMAILTEMPLATES_PAVA_TEPA foreign key (TEMPLATE_PARAMETER_ID) references EMAILTEMPLATES_TEMPLATE_REPORT(ID)^
create index IDX_EMAILTEMPLATES_PAVA_TEPA on EMAILTEMPLATES_PARAMETER_VALUE (TEMPLATE_PARAMETER_ID)^
-- end EMAILTEMPLATES_PARAMETER_VALUE
-- begin EMAILTEMPLATES_EMLTPTE_FDLINK
alter table EMAILTEMPLATES_EMLTPTE_FDLINK add constraint FK_EMATEMFI_EMAIL_TEMPLATE foreign key (EMAIL_TEMPLATE_ID) references EMAILTEMPLATES_EMAIL_TEMPLATE(ID)^
alter table EMAILTEMPLATES_EMLTPTE_FDLINK add constraint FK_EMATEMFI_FILE_DESCRIPTOR foreign key (FILE_DESCRIPTOR_ID) references SYS_FILE(ID)^
-- end EMAILTEMPLATES_EMLTPTE_FDLINK
-- begin EMAILTEMPLATES_TEMPLATE_BLOCK
alter table EMAILTEMPLATES_TEMPLATE_BLOCK add constraint FK_EMAILTEMPLATES_TEMBLO_CAT foreign key (CATEGORY_ID) references EMAILTEMPLATES_BLOCK_GROUP(ID)^
create unique index IDX_EMAILTEMPLATES_TEBL_UK_NA on EMAILTEMPLATES_TEMPLATE_BLOCK (NAME, DELETE_TS) ^
create index IDX_EMAILTEMPLATES_TEMBLO_CAT on EMAILTEMPLATES_TEMPLATE_BLOCK (CATEGORY_ID)^
-- end EMAILTEMPLATES_TEMPLATE_BLOCK
-- begin EMAILTEMPLATES_TEMPLATE_BLOCK
create unique index IDX_EMAILTEMPLATES_CTB_U_NAME on EMAILTEMPLATES_BLOCK_GROUP (NAME, DELETE_TS) ^
-- end EMAILTEMPLATES_TEMPLATE_BLOCK
