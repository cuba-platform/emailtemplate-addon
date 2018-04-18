-- begin YET_LAYOUT_EMAIL_TEMPLATE
create table YET_LAYOUT_EMAIL_TEMPLATE (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(255) not null,
    CODE varchar(255) not null,
    GROUP_ID varchar(36) not null,
    REPORT_ID varchar(36) not null,
    --
    primary key (ID)
)^
-- end YET_LAYOUT_EMAIL_TEMPLATE
-- begin YET_CONTENT_EMAIL_TEMPLATE
create table YET_CONTENT_EMAIL_TEMPLATE (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    primary key (ID)
)^
-- end YET_CONTENT_EMAIL_TEMPLATE
-- begin YET_CONTENT_EMAIL_TEMPLATE_REPORT_LINK
create table YET_CONTENT_EMAIL_TEMPLATE_REPORT_LINK (
    CONTENT_EMAIL_TEMPLATE_ID varchar(36) not null,
    REPORT_ID varchar(36) not null,
    primary key (CONTENT_EMAIL_TEMPLATE_ID, REPORT_ID)
)^
-- end YET_CONTENT_EMAIL_TEMPLATE_REPORT_LINK
