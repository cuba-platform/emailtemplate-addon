-- begin EMAILTEMPLATES_PARAMETER_VALUE
create table EMAILTEMPLATES_PARAMETER_VALUE (
    ID varchar(32),
    VERSION integer not null,
    CREATE_TS datetime(3),
    CREATED_BY varchar(50),
    UPDATE_TS datetime(3),
    UPDATED_BY varchar(50),
    DELETE_TS datetime(3),
    DELETED_BY varchar(50),
    --
    PARAMETER_TYPE integer not null,
    ALIAS varchar(255) not null,
    DEFAULT_VALUE varchar(255),
    TEMPLATE_PARAMETER_ID varchar(32) not null,
    --
    primary key (ID)
)^
-- end EMAILTEMPLATES_PARAMETER_VALUE
-- begin EMAILTEMPLATES_TEMPLATE_GROUP
create table EMAILTEMPLATES_TEMPLATE_GROUP (
    ID varchar(32),
    VERSION integer not null,
    CREATE_TS datetime(3),
    CREATED_BY varchar(50),
    UPDATE_TS datetime(3),
    UPDATED_BY varchar(50),
    DELETE_TS datetime(3),
    DELETED_BY varchar(50),
    --
    NAME varchar(255) not null,
    --
    primary key (ID)
)^
-- end EMAILTEMPLATES_TEMPLATE_GROUP
-- begin EMAILTEMPLATES_EMAIL_TEMPLATE
create table EMAILTEMPLATES_EMAIL_TEMPLATE (
    ID varchar(32),
    VERSION integer not null,
    CREATE_TS datetime(3),
    CREATED_BY varchar(50),
    UPDATE_TS datetime(3),
    UPDATED_BY varchar(50),
    DELETE_TS datetime(3),
    DELETED_BY varchar(50),
    DTYPE varchar(100),
    --
    USE_REPORT_SUBJECT boolean,
    NAME varchar(255) not null,
    GROUP_ID varchar(32),
    TYPE_ varchar(50) not null,
    CODE varchar(255) not null,
    FROM_ varchar(255),
    TO_ longtext,
    CC longtext,
    BCC longtext,
    SUBJECT varchar(255),
    --
    -- from emailtemplates$ReportEmailTemplate
    EMAIL_BODY_REPORT_ID varchar(32),
    --
    -- from emailtemplates$JsonEmailTemplate
    HTML longtext,
    REPORT_XML longtext,
    --
    primary key (ID)
)^
-- end EMAILTEMPLATES_EMAIL_TEMPLATE
-- begin EMAILTEMPLATES_TEMPLATE_REPORT
create table EMAILTEMPLATES_TEMPLATE_REPORT (
    ID varchar(32),
    VERSION integer not null,
    CREATE_TS datetime(3),
    CREATED_BY varchar(50),
    UPDATE_TS datetime(3),
    UPDATED_BY varchar(50),
    DELETE_TS datetime(3),
    DELETED_BY varchar(50),
    --
    NAME varchar(255),
    REPORT_ID varchar(32) not null,
    EMAIL_TEMPLATE_ID varchar(32),
    --
    primary key (ID)
)^
-- end EMAILTEMPLATES_TEMPLATE_REPORT
-- begin EMAILTEMPLATES_EMLTPTE_FDLINK
create table EMAILTEMPLATES_EMLTPTE_FDLINK (
    EMAIL_TEMPLATE_ID varchar(32),
    FILE_DESCRIPTOR_ID varchar(32),
    primary key (EMAIL_TEMPLATE_ID, FILE_DESCRIPTOR_ID)
)^
-- end EMAILTEMPLATES_EMLTPTE_FDLINK
-- begin SYS_SENDING_MESSAGE
alter table SYS_SENDING_MESSAGE add column CC_ varchar(255) ^
alter table SYS_SENDING_MESSAGE add column BCC_ varchar(255) ^
alter table SYS_SENDING_MESSAGE add column DTYPE varchar(100) ^
update SYS_SENDING_MESSAGE set DTYPE = 'emailtemplates$ExtendedSendingMessage' where DTYPE is null ^
-- end SYS_SENDING_MESSAGE
