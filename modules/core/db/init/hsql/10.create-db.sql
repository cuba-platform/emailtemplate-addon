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
    DTYPE varchar(100),
    --
    NAME varchar(255) not null,
    GROUP_ID varchar(36),
    TYPE_ varchar(50) not null,
    CODE varchar(255) not null,
    FROM_ varchar(255),
    TO_ longvarchar,
    CC longvarchar,
    BCC longvarchar,
    SUBJECT varchar(255),
    --
    -- from emailtemplates$ReportEmailTemplate
    USE_REPORT_SUBJECT boolean,
    EMAIL_BODY_REPORT_ID varchar(36),
    --
    -- from emailtemplates$JsonEmailTemplate
    JSON_TEMPLATE longvarchar,
    HTML longvarchar,
    REPORT_XML longvarchar,
    --
    primary key (ID)
)^
-- end EMAILTEMPLATES_EMAIL_TEMPLATE
-- begin EMAILTEMPLATES_TEMPLATE_GROUP
create table EMAILTEMPLATES_TEMPLATE_GROUP (
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
-- end EMAILTEMPLATES_TEMPLATE_GROUP

-- begin EMAILTEMPLATES_PARAMETER_VALUE
create table EMAILTEMPLATES_PARAMETER_VALUE (
    ID varchar(36) not null,
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
    TEMPLATE_PARAMETER_ID varchar(36) not null,
    --
    primary key (ID)
)^
-- end EMAILTEMPLATES_PARAMETER_VALUE

-- begin EMAILTEMPLATES_EMAIL_TEMPLATE_FILE_DESCRIPTOR_LINK
create table EMAILTEMPLATES_EMAIL_TEMPLATE_FILE_DESCRIPTOR_LINK (
    EMAIL_TEMPLATE_ID varchar(36) not null,
    FILE_DESCRIPTOR_ID varchar(36) not null,
    primary key (EMAIL_TEMPLATE_ID, FILE_DESCRIPTOR_ID)
)^
-- end EMAILTEMPLATES_EMAIL_TEMPLATE_FILE_DESCRIPTOR_LINK
-- begin EMAILTEMPLATES_TEMPLATE_REPORT
create table EMAILTEMPLATES_TEMPLATE_REPORT (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    NAME varchar(255),
    REPORT_ID varchar(36) not null,
    EMAIL_TEMPLATE_ID varchar(36),
    --
    primary key (ID)
)^
-- end EMAILTEMPLATES_TEMPLATE_REPORT
