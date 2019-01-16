package com.haulmont.addon.emailtemplates.service;

import com.haulmont.addon.emailtemplates.dto.ExtendedEmailInfo;
import com.haulmont.addon.emailtemplates.entity.emailer.ExtendedSendingMessage;

import com.haulmont.cuba.core.global.EmailException;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.List;

public interface EmailService {

    String NAME = "emailtemplates_EmailService";

    /**
     * Send email synchronously.
     *
     * @param info email details
     * @throws EmailException in case of any errors
     */
    void sendEmail(ExtendedEmailInfo info) throws EmailException;

    /**
     * Send email asynchronously, with limited number of attempts.
     * <p>
     * The actual sending is performed by invoking the {@code EmailerAPI.processQueuedEmails()} (e.g. from a scheduled task).
     *
     * @param info          email details
     * @param attemptsCount count of attempts to send (1 attempt = 1 emailer cron tick)
     * @param deadline      Emailer tries to send message till deadline.
     *                      If deadline has come and message has not been sent, status of this message is changed to
     *                      {@link com.haulmont.cuba.core.global.SendingStatus#NOTSENT}
     */
    List<ExtendedSendingMessage> sendEmailAsync(ExtendedEmailInfo info, @Nullable Integer attemptsCount, @Nullable Date deadline);

    /**
     * Send email asynchronously.
     * <p>
     * The actual sending is performed by invoking the {@code EmailerAPI.processQueuedEmails()} (e.g. from a scheduled task).
     *
     * @param info email details
     */
    List<ExtendedSendingMessage> sendEmailAsync(ExtendedEmailInfo info);
    /**
     * Load content text for given message.
     *
     * @return email content text
     */
    String loadContentText(ExtendedSendingMessage sendingMessage);

    /**
     * @return true if email body text and attachments are stored in file storage instead of BLOB columns in database
     */
    boolean isFileStorageUsed();
}
