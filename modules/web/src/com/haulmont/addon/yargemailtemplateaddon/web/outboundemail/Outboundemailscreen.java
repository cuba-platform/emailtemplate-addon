package com.haulmont.addon.yargemailtemplateaddon.web.outboundemail;

import com.haulmont.cuba.core.app.EmailService;
import com.haulmont.cuba.core.global.EmailAttachment;
import com.haulmont.cuba.core.global.EmailException;
import com.haulmont.cuba.core.global.EmailInfo;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.Label;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.Map;

public class Outboundemailscreen extends AbstractWindow {

    public static final String PARAM_SEND = "send";

    @WindowParam(name = "body", required = true)
    protected EmailInfo emailInfo;
    @Inject
    protected Label messageLabel;
    @Inject
    protected Button sendButton;
    @Inject
    protected EmailService emailService;
    @Inject
    protected Logger log;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        String string = getBodyWithAttachments(emailInfo);
        messageLabel.setHtmlEnabled(true);
        messageLabel.setValue(string);

        if (Boolean.TRUE.equals(params.get(PARAM_SEND))) {
            sendButton.setVisible(true);
        } else {
            sendButton.setVisible(false);
        }
    }

    public void onCancelButtonClick() {
        close("windowClose ");
    }

    public void onSendButtonClick() {
        try {
            emailService.sendEmail(emailInfo);
        } catch (EmailException e) {
            StringBuilder builder = new StringBuilder();
            for (String s: e.getMessages()) {
                builder.append(s);
                builder.append("\n");
            }
            showNotification(builder.toString());
            log.warn(e.toString());
        }
    }

    protected String getBodyWithAttachments(EmailInfo emailInfo) {
        StringBuilder builder = new StringBuilder();
        builder.append(emailInfo.getBody());

        EmailAttachment[] attachments = emailInfo.getAttachments();
        if (attachments != null) {
            for (EmailAttachment attachment: attachments) {
                builder.append("\n");
                builder.append(new String(attachment.getData()));
            }
        }
        return builder.toString();
    }
}