package com.haulmont.addon.emailtemplates.emailer;

import com.haulmont.addon.emailtemplates.dto.ExtendedEmailInfo;
import com.haulmont.addon.emailtemplates.entity.emailer.ExtendedSendingMessage;
import com.haulmont.cuba.core.entity.SendingAttachment;
import com.haulmont.cuba.core.global.EmailException;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.List;

public interface EmailerAPI {

    String NAME = "emailtemplates_Emailer";

    /**
     * Send email synchronously.
     *
     * @param info email details
     * @throws EmailException in case of any errors
     */
    void sendEmail(ExtendedEmailInfo info) throws EmailException;

    /**
     * Send email asynchronously, with limited number of attempts.
     *
     * @param info          email details
     * @param attemptsCount count of attempts to send (1 attempt per scheduler tick). If not specified,
     *                      {@link com.haulmont.cuba.core.app.EmailerConfig#getDefaultSendingAttemptsCount()} is used
     * @param deadline      Emailer tries to send message till deadline.
     *                      If deadline has come and message has not been sent, status of this message is changed to
     *                      {@link com.haulmont.cuba.core.global.SendingStatus#NOTSENT}
     * @return list of created {@link ExtendedSendingMessage}s
     */
    List<ExtendedSendingMessage> sendEmailAsync(ExtendedEmailInfo info, @Nullable Integer attemptsCount, @Nullable Date deadline);

    /**
     * Send email asynchronously.
     * <p>
     * This method creates a list of {@link ExtendedSendingMessage} instances, saves it to the database and returns immediately.
     * The actual sending is performed by the {@link #processQueuedEmails()} method which should be invoked by a
     * scheduled task.
     *
     * @param info email details
     * @return list of created {@link ExtendedSendingMessage}s
     */
    List<ExtendedSendingMessage> sendEmailAsync(ExtendedEmailInfo info);

    /**
     * Send emails added to the queue.
     * <p>
     * This method should be called periodically from a scheduled task.
     *
     * @return short message describing how many emails were sent, or error message
     */
    String processQueuedEmails();

    /**
     * Migrate list of existing messages to be stored in file storage, in a single transaction.
     */
    void migrateEmailsToFileStorage(List<ExtendedSendingMessage> messages);

    /**
     * Migrate list of existing email attachments to be stored in file storage, in a single transaction.
     */
    void migrateAttachmentsToFileStorage(List<SendingAttachment> attachments);

    /**
     * Loads content text for given message.
     *
     * @return email content text
     */
    String loadContentText(ExtendedSendingMessage sendingMessage);
}
