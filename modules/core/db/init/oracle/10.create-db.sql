-- begin EMAILTEMPLATES_EMAIL_TEMPLATE
create table EMAILTEMPLATES_EMAIL_TEMPLATE (
    ID varchar2(32),
    VERSION number(10) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar2(50 char),
    UPDATE_TS timestamp,
    UPDATED_BY varchar2(50 char),
    DELETE_TS timestamp,
    DELETED_BY varchar2(50 char),
    DTYPE varchar2(100 char),
    --
    USE_REPORT_SUBJECT char(1),
    NAME varchar2(255 char) not null,
    GROUP_ID varchar2(32),
    TYPE_ varchar2(50 char) not null,
    CODE varchar2(255 char) not null,
    FROM_ varchar2(255 char),
    TO_ clob,
    CC clob,
    BCC clob,
    SUBJECT varchar2(255 char),
    --
    -- from emailtemplates$ReportEmailTemplate
    EMAIL_BODY_REPORT_ID varchar2(32),
    --
    -- from emailtemplates$JsonEmailTemplate
    HTML clob,
    REPORT_XML clob,
    --
    primary key (ID)
)^
-- end EMAILTEMPLATES_EMAIL_TEMPLATE
-- begin EMAILTEMPLATES_TEMPLATE_GROUP
create table EMAILTEMPLATES_TEMPLATE_GROUP (
    ID varchar2(32),
    VERSION number(10) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar2(50 char),
    UPDATE_TS timestamp,
    UPDATED_BY varchar2(50 char),
    DELETE_TS timestamp,
    DELETED_BY varchar2(50 char),
    --
    NAME varchar2(255 char) not null,
    --
    primary key (ID)
)^
-- end EMAILTEMPLATES_TEMPLATE_GROUP
-- begin EMAILTEMPLATES_TEMPLATE_REPORT
create table EMAILTEMPLATES_TEMPLATE_REPORT (
    ID varchar2(32),
    VERSION number(10) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar2(50 char),
    UPDATE_TS timestamp,
    UPDATED_BY varchar2(50 char),
    DELETE_TS timestamp,
    DELETED_BY varchar2(50 char),
    --
    NAME varchar2(255 char),
    REPORT_ID varchar2(32) not null,
    EMAIL_TEMPLATE_ID varchar2(32),
    --
    primary key (ID)
)^
-- end EMAILTEMPLATES_TEMPLATE_REPORT
-- begin EMAILTEMPLATES_PARAMETER_VALUE
create table EMAILTEMPLATES_PARAMETER_VALUE (
    ID varchar2(32),
    VERSION number(10) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar2(50 char),
    UPDATE_TS timestamp,
    UPDATED_BY varchar2(50 char),
    DELETE_TS timestamp,
    DELETED_BY varchar2(50 char),
    --
    PARAMETER_TYPE integer not null,
    ALIAS varchar2(255 char) not null,
    DEFAULT_VALUE varchar2(255 char),
    TEMPLATE_PARAMETER_ID varchar2(32) not null,
    --
    primary key (ID)
)^
-- end EMAILTEMPLATES_PARAMETER_VALUE
-- begin EMAILTEMPLATES_EMLTPTE_FDLINK
create table EMAILTEMPLATES_EMLTPTE_FDLINK (
    EMAIL_TEMPLATE_ID varchar2(32),
    FILE_DESCRIPTOR_ID varchar2(32),
    primary key (EMAIL_TEMPLATE_ID, FILE_DESCRIPTOR_ID)
)^
-- end EMAILTEMPLATES_EMLTPTE_FDLINK
-- begin EMAILTEMPLATES_TEMPLATE_BLOCK
create table EMAILTEMPLATES_TEMPLATE_BLOCK (
    ID varchar2(32),
    VERSION number(10) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar2(50 char),
    UPDATE_TS timestamp,
    UPDATED_BY varchar2(50 char),
    DELETE_TS timestamp,
    DELETED_BY varchar2(50 char),
    --
    NAME varchar2(255 char) not null,
    LABEL varchar2(255 char) not null,
    CATEGORY_ID varchar2(32),
    CONTENT clob,
    ICON varchar2(50 char),
    --
    primary key (ID)
)^
-- end EMAILTEMPLATES_TEMPLATE_BLOCK
-- begin EMAILTEMPLATES_TEMPLATE_BLOCK
create table EMAILTEMPLATES_BLOCK_GROUP (
    ID varchar2(32),
    VERSION number(10) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar2(50 char),
    UPDATE_TS timestamp,
    UPDATED_BY varchar2(50 char),
    DELETE_TS timestamp,
    DELETED_BY varchar2(50 char),
    --
    NAME varchar2(255 char) not null,
    --
    primary key (ID)
)^
-- end EMAILTEMPLATES_TEMPLATE_BLOCK
