update SYS_SENDING_MESSAGE set ADDRESS_BCC = _BCC where DTYPE = 'emailtemplates$ExtendedSendingMessage';
update SYS_SENDING_MESSAGE set ADDRESS_CC = _CC where DTYPE = 'emailtemplates$ExtendedSendingMessage';
update SYS_SENDING_MESSAGE set DTYPE = null where DTYPE = 'emailtemplates$ExtendedSendingMessage';

alter table SYS_SENDING_MESSAGE drop column _BCC;
alter table SYS_SENDING_MESSAGE drop column _CC;
alter table SYS_SENDING_MESSAGE drop column DTYPE;