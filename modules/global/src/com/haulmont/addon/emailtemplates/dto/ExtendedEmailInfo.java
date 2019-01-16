package com.haulmont.addon.emailtemplates.dto;

import com.haulmont.cuba.core.global.EmailInfo;

public class ExtendedEmailInfo extends EmailInfo {

    /**
     * Recipient email addresses separated with "," or ";" symbol.
     * <p>
     * Flag {@code sendInOneMessage} is for backward compatibility with previous CUBA versions.
     * If {@code sendInOneMessage = true} then one message will be sent for all recipients and it will include CC and BCC.
     * Otherwise CC and BCC are ignored and multiple messages by the number of recipients in addresses will be sent.
     */
    private String cc;
    private String bcc;
    private boolean sendInOneMessage = false;

    public ExtendedEmailInfo(String addresses, String caption, String body, String bodyContentType) {
        super(addresses, caption, body, bodyContentType);
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getBcc() {
        return bcc;
    }

    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    public boolean isSendInOneMessage() {
        return sendInOneMessage;
    }

    public void setSendInOneMessage(boolean sendInOneMessage) {
        this.sendInOneMessage = sendInOneMessage;
    }
}
