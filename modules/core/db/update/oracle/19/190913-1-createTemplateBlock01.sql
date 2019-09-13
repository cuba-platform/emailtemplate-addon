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
    ATTRIBUTES clob,
    --
    primary key (ID)
)^