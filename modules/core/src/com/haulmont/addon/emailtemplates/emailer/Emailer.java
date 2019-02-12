package com.haulmont.addon.emailtemplates.emailer;

import com.google.common.base.Strings;
import com.haulmont.addon.emailtemplates.dto.ExtendedEmailInfo;
import com.haulmont.addon.emailtemplates.entity.emailer.ExtendedSendingMessage;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.TypedQuery;
import com.haulmont.cuba.core.app.EmailerConfig;
import com.haulmont.cuba.core.app.FileStorageAPI;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.SendingAttachment;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.security.app.Authentication;
import com.sun.mail.smtp.SMTPAddressFailedException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.mail.internet.AddressException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.RejectedExecutionException;

@Component(EmailerAPI.NAME)
public class Emailer implements EmailerAPI {

    protected static final String BODY_FILE_EXTENSION = "txt";

    private static final Logger log = LoggerFactory.getLogger(Emailer.class);

    protected EmailerConfig config;

    protected volatile int callCount = 0;

    @Resource(name = "mailSendTaskExecutor")
    protected TaskExecutor mailSendTaskExecutor;

    @Inject
    protected TimeSource timeSource;

    @Inject
    protected Persistence persistence;

    @Inject
    protected Metadata metadata;

    @Inject
    protected Authentication authentication;

    @Inject
    protected EmailSenderAPI emailSender;

    @Inject
    protected Resources resources;

    @Inject
    protected FileStorageAPI fileStorage;

    @Inject
    public void setConfig(Configuration configuration) {
        this.config = configuration.getConfig(EmailerConfig.class);
    }

    protected String getEmailerLogin() {
        return config.getEmailerUserLogin();
    }

    @Override
    public void sendEmail(ExtendedEmailInfo info) throws EmailException {
        prepareEmailInfo(info);
        persistAndSendEmail(info);
    }

    @Override
    public List<ExtendedSendingMessage> sendEmailAsync(ExtendedEmailInfo info) {
        //noinspection UnnecessaryLocalVariable
        List<ExtendedSendingMessage> result = sendEmailAsync(info, null, null);
        return result;
    }

    @Override
    public List<ExtendedSendingMessage> sendEmailAsync(ExtendedEmailInfo info, Integer attemptsCount, Date deadline) {
        prepareEmailInfo(info);
        List<ExtendedSendingMessage> messages = splitEmail(info, attemptsCount, deadline);
        persistMessages(messages, SendingStatus.QUEUE);
        return messages;
    }

    protected void prepareEmailInfo(EmailInfo emailInfo) {
        processBodyTemplate(emailInfo);

        if (emailInfo.getFrom() == null) {
            String defaultFromAddress = config.getFromAddress();
            if (defaultFromAddress == null) {
                throw new IllegalStateException("cuba.email.fromAddress not set in the system");
            }
            emailInfo.setFrom(defaultFromAddress);
        }
    }

    protected void processBodyTemplate(EmailInfo info) {
        String templatePath = info.getTemplatePath();
        if (templatePath == null) {
            return;
        }

        Map<String, Serializable> params = info.getTemplateParameters() == null
                ? Collections.<String, Serializable>emptyMap()
                : info.getTemplateParameters();
        String templateContents = resources.getResourceAsString(templatePath);
        if (templateContents == null) {
            throw new IllegalArgumentException("Could not find template by path: " + templatePath);
        }
        String body = TemplateHelper.processTemplate(templateContents, params);
        info.setBody(body);
    }

    protected List<ExtendedSendingMessage> splitEmail(ExtendedEmailInfo info, @Nullable Integer attemptsCount, @Nullable Date deadline) {
        List<ExtendedSendingMessage> sendingMessageList = new ArrayList<>();
        if (info.isSendInOneMessage()) {
            if (StringUtils.isNotBlank(info.getAddresses())) {
                ExtendedSendingMessage sendingMessage = convertToSendingMessage(info.getAddresses(), info.getFrom(), info.getCc(),
                        info.getBcc(), info.getCaption(), info.getBody(), info.getBodyContentType(), info.getHeaders(),
                        info.getAttachments(), attemptsCount, deadline);

                sendingMessageList.add(sendingMessage);
            }
        } else {
            String[] splitAddresses = info.getAddresses().split("[,;]");
            for (String address : splitAddresses) {
                address = address.trim();
                if (StringUtils.isNotBlank(address)) {
                    ExtendedSendingMessage sendingMessage = convertToSendingMessage(address, info.getFrom(), null,
                            null, info.getCaption(), info.getBody(), info.getBodyContentType(), info.getHeaders(),
                            info.getAttachments(), attemptsCount, deadline);

                    sendingMessageList.add(sendingMessage);
                }
            }
        }
        return sendingMessageList;
    }

    protected void sendSendingMessage(ExtendedSendingMessage sendingMessage) {
        Objects.requireNonNull(sendingMessage, "sendingMessage is null");
        Objects.requireNonNull(sendingMessage.getAddress(), "sendingMessage.address is null");
        Objects.requireNonNull(sendingMessage.getCaption(), "sendingMessage.caption is null");
        Objects.requireNonNull(sendingMessage.getContentText(), "sendingMessage.contentText is null");
        Objects.requireNonNull(sendingMessage.getFrom(), "sendingMessage.from is null");
        try {
            emailSender.sendEmail(sendingMessage);
            markAsSent(sendingMessage);
        } catch (Exception e) {
            log.warn("Unable to send email to '" + sendingMessage.getAddress() + "'", e);
            if (isNeedToRetry(e)) {
                returnToQueue(sendingMessage);
            } else {
                markAsNonSent(sendingMessage);
            }
        }
    }

    protected void persistAndSendEmail(ExtendedEmailInfo emailInfo) throws EmailException {
        Objects.requireNonNull(emailInfo.getAddresses(), "addresses are null");
        Objects.requireNonNull(emailInfo.getCaption(), "caption is null");
        Objects.requireNonNull(emailInfo.getBody(), "body is null");
        Objects.requireNonNull(emailInfo.getFrom(), "from is null");

        List<ExtendedSendingMessage> messages = splitEmail(emailInfo, null, null);

        List<String> failedAddresses = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();

        for (ExtendedSendingMessage sendingMessage : messages) {
            ExtendedSendingMessage persistedMessage = persistMessageIfPossible(sendingMessage);

            try {
                emailSender.sendEmail(sendingMessage);
                if (persistedMessage != null) {
                    markAsSent(persistedMessage);
                }
            } catch (Exception e) {
                log.warn("Unable to send email to '" + sendingMessage.getAddress() + "'", e);
                failedAddresses.add(sendingMessage.getAddress());
                errorMessages.add(e.getMessage());
                if (persistedMessage != null) {
                    markAsNonSent(persistedMessage);
                }
            }
        }

        if (!failedAddresses.isEmpty()) {
            throw new EmailException(failedAddresses, errorMessages);
        }
    }

    /*
     * Try to persist message and catch all errors to allow actual delivery
     * in case of database or file storage failure.
     */
    @Nullable
    protected ExtendedSendingMessage persistMessageIfPossible(ExtendedSendingMessage sendingMessage) {
        // A copy of sendingMessage is created
        // to avoid additional overhead to load body and attachments back from FS
        try {
            ExtendedSendingMessage clonedMessage = createClone(sendingMessage);
            persistMessages(Collections.singletonList(clonedMessage), SendingStatus.SENDING);
            return clonedMessage;
        } catch (Exception e) {
            log.error("Failed to persist message " + sendingMessage.getCaption(), e);
            return null;
        }
    }

    protected ExtendedSendingMessage createClone(ExtendedSendingMessage srcMessage) {
        ExtendedSendingMessage clonedMessage = metadata.getTools().copy(srcMessage);
        List<SendingAttachment> clonedList = new ArrayList<>();
        for (SendingAttachment srcAttach : srcMessage.getAttachments()) {
            SendingAttachment clonedAttach = metadata.getTools().copy(srcAttach);
            clonedAttach.setMessage(null);
            clonedAttach.setMessage(clonedMessage);
            clonedList.add(clonedAttach);
        }
        clonedMessage.setAttachments(clonedList);
        return clonedMessage;
    }

    @Override
    public String processQueuedEmails() {
        if (applicationNotStartedYet()) {
            return null;
        }

        int callsToSkip = config.getDelayCallCount();
        if (callCount < callsToSkip) {
            callCount++;
            return null;
        }

        String resultMessage;
        try {
            authentication.begin(getEmailerLogin());
            try {
                resultMessage = sendQueuedEmails();
            } finally {
                authentication.end();
            }
        } catch (Throwable e) {
            log.error("Error", e);
            resultMessage = e.getMessage();
        }
        return resultMessage;
    }

    protected boolean applicationNotStartedYet() {
        return !AppContext.isStarted();
    }

    protected String sendQueuedEmails() {
        List<ExtendedSendingMessage> messagesToSend = loadEmailsToSend();

        for (ExtendedSendingMessage msg : messagesToSend) {
            submitExecutorTask(msg);
        }

        if (messagesToSend.isEmpty()) {
            return "";
        }

        return String.format("Processed %d emails", messagesToSend.size());
    }

    protected boolean shouldMarkNotSent(ExtendedSendingMessage sendingMessage) {
        Date deadline = sendingMessage.getDeadline();
        if (deadline != null && deadline.before(timeSource.currentTimestamp())) {
            return true;
        }

        Integer messageAttemptsLimit = sendingMessage.getAttemptsCount();
        int defaultLimit = config.getDefaultSendingAttemptsCount();
        int attemptsLimit = messageAttemptsLimit != null ? messageAttemptsLimit : defaultLimit;
        //noinspection UnnecessaryLocalVariable
        boolean res = sendingMessage.getAttemptsMade() != null && sendingMessage.getAttemptsMade() >= attemptsLimit;
        return res;
    }

    protected void submitExecutorTask(ExtendedSendingMessage msg) {
        try {
            Runnable mailSendTask = new EmailSendTask(msg);
            mailSendTaskExecutor.execute(mailSendTask);
        } catch (RejectedExecutionException e) {
            returnToQueue(msg);
        } catch (Exception e) {
            log.error("Exception while sending email: ", e);
            if (isNeedToRetry(e)) {
                returnToQueue(msg);
            } else {
                markAsNonSent(msg);
            }
        }
    }

    protected List<ExtendedSendingMessage> loadEmailsToSend() {
        Date sendTimeoutTime = DateUtils.addSeconds(timeSource.currentTimestamp(), -config.getSendingTimeoutSec());

        List<ExtendedSendingMessage> emailsToSend = new ArrayList<>();

        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            TypedQuery<ExtendedSendingMessage> query = em.createQuery(
                    "select sm from email$SendingMessage sm" +
                            " where sm.status = :statusQueue or (sm.status = :statusSending and sm.updateTs < :time)" +
                            " order by sm.createTs",
                    ExtendedSendingMessage.class
            );
            query.setParameter("statusQueue", SendingStatus.QUEUE.getId());
            query.setParameter("time", sendTimeoutTime);
            query.setParameter("statusSending", SendingStatus.SENDING.getId());

            View view = metadata.getViewRepository().getView(ExtendedSendingMessage.class, "sendingMessage.loadFromQueue");
            view.setLoadPartialEntities(true); // because SendingAttachment.content has FetchType.LAZY
            query.setView(view);

            query.setMaxResults(config.getMessageQueueCapacity());

            List<ExtendedSendingMessage> resList = query.getResultList();

            for (ExtendedSendingMessage msg : resList) {
                if (shouldMarkNotSent(msg)) {
                    msg.setStatus(SendingStatus.NOTSENT);
                } else {
                    msg.setStatus(SendingStatus.SENDING);
                    emailsToSend.add(msg);
                }
            }
            tx.commit();
        }

        for (ExtendedSendingMessage message : emailsToSend) {
            loadBodyAndAttachments(message);
        }
        return emailsToSend;
    }

    @Override
    public String loadContentText(ExtendedSendingMessage sendingMessage) {
        ExtendedSendingMessage msg;
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            msg = em.reload(sendingMessage, "sendingMessage.loadContentText");
            tx.commit();
        }
        Objects.requireNonNull(msg, "Sending message not found: " + sendingMessage.getId());
        if (msg.getContentTextFile() != null) {
            byte[] bodyContent;
            try {
                bodyContent = fileStorage.loadFile(msg.getContentTextFile());
            } catch (FileStorageException e) {
                throw new RuntimeException(e);
            }
            //noinspection UnnecessaryLocalVariable
            String res = bodyTextFromByteArray(bodyContent);
            return res;
        } else {
            return msg.getContentText();
        }
    }

    protected void loadBodyAndAttachments(ExtendedSendingMessage message) {
        try {
            if (message.getContentTextFile() != null) {
                byte[] bodyContent = fileStorage.loadFile(message.getContentTextFile());
                String body = bodyTextFromByteArray(bodyContent);
                message.setContentText(body);
            }

            for (SendingAttachment attachment : message.getAttachments()) {
                if (attachment.getContentFile() != null) {
                    byte[] content = fileStorage.loadFile(attachment.getContentFile());
                    attachment.setContent(content);
                }
            }
        } catch (FileStorageException e) {
            log.error("Failed to load body or attachments for " + message);
        }
    }

    protected void persistMessages(List<ExtendedSendingMessage> sendingMessageList, SendingStatus status) {
        MessagePersistingContext context = new MessagePersistingContext();

        try {
            try (Transaction tx = persistence.createTransaction()) {
                EntityManager em = persistence.getEntityManager();
                for (ExtendedSendingMessage message : sendingMessageList) {
                    message.setStatus(status);

                    try {
                        persistSendingMessage(em, message, context);
                    } catch (FileStorageException e) {
                        throw new RuntimeException("Failed to store message " + message.getCaption(), e);
                    }
                }
                tx.commit();
            }
            context.finished();
        } finally {
            removeOrphanFiles(context);
        }
    }

    protected void removeOrphanFiles(MessagePersistingContext context) {
        for (FileDescriptor file : context.files) {
            try {
                fileStorage.removeFile(file);
            } catch (Exception e) {
                log.error("Failed to remove file " + file);
            }
        }
    }

    protected void persistSendingMessage(EntityManager em, ExtendedSendingMessage message,
                                         MessagePersistingContext context) throws FileStorageException {
        boolean useFileStorage = config.isFileStorageUsed();

        if (useFileStorage) {
            byte[] bodyBytes = bodyTextToBytes(message);

            FileDescriptor contentTextFile = createBodyFileDescriptor(message, bodyBytes);
            fileStorage.saveFile(contentTextFile, bodyBytes);
            context.files.add(contentTextFile);

            em.persist(contentTextFile);
            message.setContentTextFile(contentTextFile);
            message.setContentText(null);
        }

        em.persist(message);

        for (SendingAttachment attachment : message.getAttachments()) {
            if (useFileStorage) {
                FileDescriptor contentFile = createAttachmentFileDescriptor(attachment);

                fileStorage.saveFile(contentFile, attachment.getContent());
                context.files.add(contentFile);
                em.persist(contentFile);

                attachment.setContentFile(contentFile);
                attachment.setContent(null);
            }

            em.persist(attachment);
        }
    }

    protected FileDescriptor createAttachmentFileDescriptor(SendingAttachment attachment) {
        FileDescriptor contentFile = metadata.create(FileDescriptor.class);
        contentFile.setCreateDate(timeSource.currentTimestamp());
        contentFile.setName(attachment.getName());
        contentFile.setExtension(FilenameUtils.getExtension(attachment.getName()));
        contentFile.setSize((long) attachment.getContent().length);
        return contentFile;
    }

    protected FileDescriptor createBodyFileDescriptor(ExtendedSendingMessage message, byte[] bodyBytes) {
        FileDescriptor contentTextFile = metadata.create(FileDescriptor.class);
        contentTextFile.setCreateDate(timeSource.currentTimestamp());
        contentTextFile.setName("Email_" + message.getId() + "." + BODY_FILE_EXTENSION);
        contentTextFile.setExtension(BODY_FILE_EXTENSION);
        contentTextFile.setSize((long) bodyBytes.length);
        return contentTextFile;
    }

    protected void returnToQueue(ExtendedSendingMessage sendingMessage) {
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            ExtendedSendingMessage msg = em.merge(sendingMessage);

            msg.setAttemptsMade(msg.getAttemptsMade() + 1);
            msg.setStatus(SendingStatus.QUEUE);
            if (config.isFileStorageUsed()) {
                msg.setContentText(null);
            }

            tx.commit();
        } catch (Exception e) {
            log.error("Error returning message to '{}' to the queue", sendingMessage.getAddress(), e);
        }
    }

    protected void markAsSent(ExtendedSendingMessage sendingMessage) {
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            ExtendedSendingMessage msg = em.merge(sendingMessage);

            msg.setStatus(SendingStatus.SENT);
            msg.setAttemptsMade(msg.getAttemptsMade() + 1);
            msg.setDateSent(timeSource.currentTimestamp());
            if (config.isFileStorageUsed()) {
                msg.setContentText(null);
            }

            tx.commit();
        } catch (Exception e) {
            log.error("Error marking message to '{}' as sent", sendingMessage.getAddress(), e);
        }
    }

    protected void markAsNonSent(ExtendedSendingMessage sendingMessage) {
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();
            ExtendedSendingMessage msg = em.merge(sendingMessage);

            msg.setStatus(SendingStatus.NOTSENT);
            msg.setAttemptsMade(msg.getAttemptsMade() + 1);
            if (config.isFileStorageUsed()) {
                msg.setContentText(null);
            }


            tx.commit();
        } catch (Exception e) {
            log.error("Error marking message to '{}' as not sent", sendingMessage.getAddress(), e);
        }
    }

    protected ExtendedSendingMessage convertToSendingMessage(String address, String from, String cc, String bcc, String caption, String body,
                                                     String bodyContentType,
                                                     @Nullable List<EmailHeader> headers,
                                                     @Nullable EmailAttachment[] attachments,
                                                     @Nullable Integer attemptsCount, @Nullable Date deadline) {
        ExtendedSendingMessage sendingMessage = metadata.create(ExtendedSendingMessage.class);

        sendingMessage.setAddress(address);
        sendingMessage.setCc(cc);
        sendingMessage.setBcc(bcc);
        sendingMessage.setFrom(from);
        sendingMessage.setContentText(body);
        sendingMessage.setCaption(caption);
        sendingMessage.setAttemptsCount(attemptsCount);
        sendingMessage.setDeadline(deadline);
        sendingMessage.setAttemptsMade(0);

        if (Strings.isNullOrEmpty(bodyContentType)) {
            bodyContentType = getContentBodyType(sendingMessage);
            sendingMessage.setBodyContentType(bodyContentType);
        } else {
            sendingMessage.setBodyContentType(bodyContentType);
        }

        if (attachments != null && attachments.length > 0) {
            StringBuilder attachmentsName = new StringBuilder();
            List<SendingAttachment> sendingAttachments = new ArrayList<>(attachments.length);
            for (EmailAttachment ea : attachments) {
                attachmentsName.append(ea.getName()).append(";");

                SendingAttachment sendingAttachment = toSendingAttachment(ea);
                sendingAttachment.setMessage(sendingMessage);
                sendingAttachments.add(sendingAttachment);
            }
            sendingMessage.setAttachments(sendingAttachments);
            sendingMessage.setAttachmentsName(attachmentsName.toString());
        } else {
            sendingMessage.setAttachments(Collections.<SendingAttachment>emptyList());
        }

        if (headers != null && !headers.isEmpty()) {
            StringBuilder headersLine = new StringBuilder();
            for (EmailHeader header : headers) {
                headersLine.append(header.toString()).append(ExtendedSendingMessage.HEADERS_SEPARATOR);
            }
            sendingMessage.setHeaders(headersLine.toString());
        } else {
            sendingMessage.setHeaders(null);
        }

        replaceRecipientIfNecessary(sendingMessage);

        return sendingMessage;
    }

    protected String getContentBodyType(ExtendedSendingMessage sendingMessage) {
        String bodyContentType;
        String text = sendingMessage.getContentText();
        if (text.trim().startsWith("<html>")) {
            bodyContentType = "text/html; charset=UTF-8";
        } else {
            bodyContentType = "text/plain; charset=UTF-8";
        }
        log.warn("Content body type is not set for email '{}' with addresses: {}. Will be used '{}'.",
                sendingMessage.getCaption(), sendingMessage.getAddress(), bodyContentType);
        return bodyContentType;
    }

    protected void replaceRecipientIfNecessary(ExtendedSendingMessage msg) {
        if (config.getSendAllToAdmin()) {
            String adminAddress = config.getAdminAddress();
            log.warn(String.format(
                    "Replacing actual email recipient '%s' by admin address '%s'", msg.getAddress(), adminAddress
            ));
            msg.setAddress(adminAddress);
        }
    }

    protected SendingAttachment toSendingAttachment(EmailAttachment ea) {
        SendingAttachment sendingAttachment = metadata.create(SendingAttachment.class);
        sendingAttachment.setContent(ea.getData());
        sendingAttachment.setContentId(ea.getContentId());
        sendingAttachment.setName(ea.getName());
        sendingAttachment.setEncoding(ea.getEncoding());
        sendingAttachment.setDisposition(ea.getDisposition());
        return sendingAttachment;
    }

    protected byte[] bodyTextToBytes(ExtendedSendingMessage message) {
        byte[] bodyBytes = message.getContentText().getBytes(StandardCharsets.UTF_8);
        return bodyBytes;
    }

    protected String bodyTextFromByteArray(byte[] bodyContent) {
        return new String(bodyContent, StandardCharsets.UTF_8);
    }

    protected boolean isNeedToRetry(Exception e) {
        if (e instanceof MailSendException) {
            if (e.getCause() instanceof SMTPAddressFailedException) {
                return false;
            }
        } else if (e instanceof AddressException) {
            return false;
        }
        return true;
    }

    @Override
    public void migrateEmailsToFileStorage(List<ExtendedSendingMessage> messages) {
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();

            for (ExtendedSendingMessage msg : messages) {
                migrateMessage(em, msg);
            }
            tx.commit();
        }
    }

    @Override
    public void migrateAttachmentsToFileStorage(List<SendingAttachment> attachments) {
        try (Transaction tx = persistence.createTransaction()) {
            EntityManager em = persistence.getEntityManager();

            for (SendingAttachment attachment : attachments) {
                migrateAttachment(em, attachment);
            }

            tx.commit();
        }
    }

    protected void migrateMessage(EntityManager em, ExtendedSendingMessage msg) {
        msg = em.merge(msg);
        byte[] bodyBytes = bodyTextToBytes(msg);
        FileDescriptor bodyFile = createBodyFileDescriptor(msg, bodyBytes);

        try {
            fileStorage.saveFile(bodyFile, bodyBytes);
        } catch (FileStorageException e) {
            throw new RuntimeException(e);
        }
        em.persist(bodyFile);
        msg.setContentTextFile(bodyFile);
        msg.setContentText(null);
    }

    protected void migrateAttachment(EntityManager em, SendingAttachment attachment) {
        attachment = em.merge(attachment);
        FileDescriptor contentFile = createAttachmentFileDescriptor(attachment);

        try {
            fileStorage.saveFile(contentFile, attachment.getContent());
        } catch (FileStorageException e) {
            throw new RuntimeException(e);
        }
        em.persist(contentFile);
        attachment.setContentFile(contentFile);
        attachment.setContent(null);
    }

    protected static class EmailSendTask implements Runnable {

        private ExtendedSendingMessage sendingMessage;
        private static final Logger log = LoggerFactory.getLogger(EmailSendTask.class);

        public EmailSendTask(ExtendedSendingMessage message) {
            sendingMessage = message;
        }

        @Override
        public void run() {
            try {
                Authentication authentication = AppBeans.get(Authentication.NAME);
                Emailer emailer = AppBeans.get(EmailerAPI.NAME);

                authentication.begin(emailer.getEmailerLogin());
                try {
                    emailer.sendSendingMessage(sendingMessage);
                } finally {
                    authentication.end();
                }
            } catch (Exception e) {
                log.error("Exception while sending email: ", e);
            }
        }
    }

    protected static class MessagePersistingContext {
        public final List<FileDescriptor> files = new ArrayList<>();

        public void finished() {
            files.clear();
        }
    }
}