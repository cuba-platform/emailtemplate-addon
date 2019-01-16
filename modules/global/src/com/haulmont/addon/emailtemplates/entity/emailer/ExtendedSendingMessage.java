package com.haulmont.addon.emailtemplates.entity.emailer;

import com.haulmont.cuba.core.entity.SendingMessage;
import com.haulmont.cuba.core.entity.annotation.Extends;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "emailtemplates$ExtendedSendingMessage")
@Extends(SendingMessage.class)
public class ExtendedSendingMessage extends SendingMessage {

    @Column(name = "ADDRESS_CC")
    protected String cc;

    @Column(name = "ADDRESS_BCC")
    protected String bcc;

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
}