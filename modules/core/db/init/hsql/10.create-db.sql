-- begin EMAILTEMPLATES_EMAIL_TEMPLATE
create table EMAILTEMPLATES_EMAIL_TEMPLATE (
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
    GROUP_ID varchar(36),
    EMAIL_BODY_ID varchar(36) not null,
    CODE varchar(255) not null,
    --
    primary key (ID)
)^
-- end EMAILTEMPLATES_EMAIL_TEMPLATE
-- begin YET_TEMPLATE_GROUP
create table YET_TEMPLATE_GROUP (
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
    --
    primary key (ID)
)^
-- end YET_TEMPLATE_GROUP
-- begin EMAILTEMPLATES_LAYOUT_EMAIL_TEMPLATE_REPORT_LINK
create table EMAILTEMPLATES_LAYOUT_EMAIL_TEMPLATE_REPORT_LINK (
    LAYOUT_EMAIL_TEMPLATE_ID varchar(36) not null,
    REPORT_ID varchar(36) not null,
    primary key (LAYOUT_EMAIL_TEMPLATE_ID, REPORT_ID)
)^
-- end EMAILTEMPLATES_LAYOUT_EMAIL_TEMPLATE_REPORT_LINK
