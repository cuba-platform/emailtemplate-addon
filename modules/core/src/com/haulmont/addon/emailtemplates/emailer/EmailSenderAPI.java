package com.haulmont.addon.emailtemplates.emailer;

import com.haulmont.addon.emailtemplates.entity.emailer.ExtendedSendingMessage;

import javax.mail.MessagingException;

public interface EmailSenderAPI {

    String NAME = "emailtemplates_EmailSender";

    /**
     * Sends email with help of {@link org.springframework.mail.javamail.JavaMailSender}.
     * Message body and attachments' content must be loaded from file storage.
     * <br>
     * Use {@link EmailerAPI} instead if you need email to be delivered reliably and stored to email history.
     *
     * @throws MessagingException if delivery fails
     */
    void sendEmail(ExtendedSendingMessage sendingMessage) throws MessagingException;
}
