update SYS_SENDING_MESSAGE set ADDRESS_BCC = BCC_ where DTYPE = 'emailtemplates$ExtendedSendingMessage' ^
update SYS_SENDING_MESSAGE set ADDRESS_CC = CC_ where DTYPE = 'emailtemplates$ExtendedSendingMessage' ^
update SYS_SENDING_MESSAGE set DTYPE = null where DTYPE = 'emailtemplates$ExtendedSendingMessage' ^
alter table SYS_SENDING_MESSAGE drop column BCC_ cascade constraints ^
alter table SYS_SENDING_MESSAGE drop column CC_ cascade constraints ^
alter table SYS_SENDING_MESSAGE drop column DTYPE cascade constraints ^