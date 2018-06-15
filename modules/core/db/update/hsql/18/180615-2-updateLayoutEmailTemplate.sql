-- update YET_LAYOUT_EMAIL_TEMPLATE set REPORT_ID = <default_value> where REPORT_ID is null ;
alter table YET_LAYOUT_EMAIL_TEMPLATE alter column REPORT_ID set not null ;
