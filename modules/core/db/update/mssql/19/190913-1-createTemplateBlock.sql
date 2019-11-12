create table EMAILTEMPLATES_TEMPLATE_BLOCK (
    ID uniqueidentifier,
    VERSION integer not null,
    CREATE_TS datetime2,
    CREATED_BY varchar(50),
    UPDATE_TS datetime2,
    UPDATED_BY varchar(50),
    DELETE_TS datetime2,
    DELETED_BY varchar(50),
    --
    NAME varchar(255) not null,
    LABEL varchar(255) not null,
    CATEGORY_ID uniqueidentifier,
    CONTENT varchar(max),
    ICON varchar(50),
    --
    primary key nonclustered (ID)
);