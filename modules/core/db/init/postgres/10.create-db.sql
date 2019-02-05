-- begin EMAILTEMPLATES_EMAIL_TEMPLATE
create table EMAILTEMPLATES_EMAIL_TEMPLATE (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    DTYPE varchar(100),
    --
    USE_REPORT_SUBJECT boolean,
    NAME varchar(255) not null,
    GROUP_ID uuid,
    TYPE_ varchar(50) not null,
    CODE varchar(255) not null,
    FROM_ varchar(255),
    TO_ text,
    CC text,
    BCC text,
    SUBJECT varchar(255),
    --
    -- from emailtemplates$ReportEmailTemplate
    EMAIL_BODY_REPORT_ID uuid,
    --
    -- from emailtemplates$JsonEmailTemplate
    HTML text,
    REPORT_XML text,
    --
    primary key (ID)
)^
-- end EMAILTEMPLATES_EMAIL_TEMPLATE
-- begin EMAILTEMPLATES_TEMPLATE_GROUP
create table EMAILTEMPLATES_TEMPLATE_GROUP (
    ID uuid,
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
-- end EMAILTEMPLATES_TEMPLATE_GROUP
-- begin EMAILTEMPLATES_TEMPLATE_REPORT
create table EMAILTEMPLATES_TEMPLATE_REPORT (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(255),
    REPORT_ID uuid not null,
    EMAIL_TEMPLATE_ID uuid,
    --
    primary key (ID)
)^
-- end EMAILTEMPLATES_TEMPLATE_REPORT
-- begin EMAILTEMPLATES_PARAMETER_VALUE
create table EMAILTEMPLATES_PARAMETER_VALUE (
    ID uuid,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    PARAMETER_TYPE integer not null,
    ALIAS varchar(255) not null,
    DEFAULT_VALUE varchar(255),
    TEMPLATE_PARAMETER_ID uuid not null,
    --
    primary key (ID)
)^
-- end EMAILTEMPLATES_PARAMETER_VALUE
-- begin EMAILTEMPLATES_EMAIL_TEMPLATE_FILE_DESCRIPTOR_LINK
create table EMAILTEMPLATES_EMLTPTE_FDLINK (
    EMAIL_TEMPLATE_ID uuid,
    FILE_DESCRIPTOR_ID uuid,
    primary key (EMAIL_TEMPLATE_ID, FILE_DESCRIPTOR_ID)
)^
-- end EMAILTEMPLATES_EMAIL_TEMPLATE_FILE_DESCRIPTOR_LINK
