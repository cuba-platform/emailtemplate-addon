alter table EMAILTEMPLATES_EMAIL_TEMPLATE add constraint FK_EMAILTEMPLATES_EMTE_EMBORE foreign key (EMAIL_BODY_REPORT_ID) references EMAILTEMPLATES_TEMPLATE_REPORT(ID)^