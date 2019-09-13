create table EMAILTEMPLATES_TEMPLATE_BLOCK (
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
    LABEL varchar(255) not null,
    CATEGORY_ID varchar(32),
    CONTENT longtext,
    ATTRIBUTES longtext,
    --
    primary key (ID)
);