create table EMAILTEMPLATES_TEMPLATE_BLOCK (
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
    LABEL varchar(255) not null,
    CATEGORY_ID uuid,
    CONTENT text,
    ICON varchar(50),
    --
    primary key (ID)
);