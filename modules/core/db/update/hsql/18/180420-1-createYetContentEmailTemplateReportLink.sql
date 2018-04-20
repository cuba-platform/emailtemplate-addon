create table YET_CONTENT_EMAIL_TEMPLATE_REPORT_LINK (
    CONTENT_EMAIL_TEMPLATE_ID varchar(36) not null,
    REPORT_ID varchar(36) not null,
    primary key (CONTENT_EMAIL_TEMPLATE_ID, REPORT_ID)
);
