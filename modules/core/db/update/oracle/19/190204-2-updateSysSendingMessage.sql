alter table SYS_SENDING_MESSAGE add ( CC_ varchar2(255 char) ) ^
alter table SYS_SENDING_MESSAGE add ( BCC_ varchar2(255 char) ) ^
alter table SYS_SENDING_MESSAGE drop column ADDRESS_CC cascade constraints ^
alter table SYS_SENDING_MESSAGE drop column ADDRESS_BCC cascade constraints ^
