alter table EMAILTEMPLATES_EMAIL_TEMPLATE_FILE_DESCRIPTOR_LINK add constraint FK_EMATEMFILDES_ON_EMAIL_TEMPLATE foreign key (EMAIL_TEMPLATE_ID) references EMAILTEMPLATES_EMAIL_TEMPLATE(ID);
alter table EMAILTEMPLATES_EMAIL_TEMPLATE_FILE_DESCRIPTOR_LINK add constraint FK_EMATEMFILDES_ON_FILE_DESCRIPTOR foreign key (FILE_DESCRIPTOR_ID) references SYS_FILE(ID);
