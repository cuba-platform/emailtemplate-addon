create unique index IDX_EMAILTEMPLATES_EMAIL_TEMPLATE_UNIQ_CODE on EMAILTEMPLATES_EMAIL_TEMPLATE (CODE) where delete_ts is null;
