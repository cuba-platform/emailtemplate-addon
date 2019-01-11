package com.haulmont.addon.emailtemplates.service;

import com.haulmont.addon.emailtemplates.dto.ExtendedEmailInfo;
import com.haulmont.addon.emailtemplates.emailer.EmailerAPI;
import com.haulmont.addon.emailtemplates.entity.emailer.ExtendedSendingMessage;

import com.haulmont.cuba.core.app.EmailerConfig;
import com.haulmont.cuba.core.global.EmailException;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Date;
import java.util.List;

@Service(EmailService.NAME)
public class EmailServiceBean implements EmailService {

    @Inject
    protected EmailerAPI emailer;

    @Inject
    protected EmailerConfig emailerConfig;

    @Override
    public void sendEmail(ExtendedEmailInfo info) throws EmailException {
        emailer.sendEmail(info);
    }

    @Override
    public List<ExtendedSendingMessage> sendEmailAsync(ExtendedEmailInfo info, @Nullable Integer attemptsCount, @Nullable Date deadline) {
        return emailer.sendEmailAsync(info, attemptsCount, deadline);
    }

    @Override
    public List<ExtendedSendingMessage> sendEmailAsync(ExtendedEmailInfo info) {
        return emailer.sendEmailAsync(info);
    }

    @Override
    public String loadContentText(ExtendedSendingMessage sendingMessage) {
        return emailer.loadContentText(sendingMessage);
    }

    @Override
    public boolean isFileStorageUsed() {
        return emailerConfig.isFileStorageUsed();
    }
}
