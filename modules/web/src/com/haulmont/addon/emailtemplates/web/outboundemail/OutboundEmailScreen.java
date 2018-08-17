package com.haulmont.addon.emailtemplates.web.outboundemail;

import com.haulmont.cuba.core.app.EmailService;
import com.haulmont.cuba.core.global.EmailAttachment;
import com.haulmont.cuba.core.global.EmailException;
import com.haulmont.cuba.core.global.EmailInfo;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.export.ByteArrayDataProvider;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.Map;

public class OutboundEmailScreen extends AbstractWindow {

    public static final String PARAM_SEND = "send";

    @WindowParam(name = "body", required = true)
    protected EmailInfo emailInfo;
    @Inject
    private Label bodyLabel;
    @Inject
    private ScrollBoxLayout attachmentsBox;
    @Inject
    protected Button sendButton;
    @Inject
    protected EmailService emailService;
    @Inject
    private ExportDisplay exportDisplay;
    @Inject
    private ComponentsFactory componentsFactory;
    @Inject
    protected Logger log;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        String body = emailInfo.getBody();
        bodyLabel.setHtmlEnabled(true);
        bodyLabel.setValue(body);

        if (emailInfo.getAttachments() != null) {
            generateLinkButtonsByAttachments(emailInfo.getAttachments());
        }

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
            }
            showNotification(builder.toString());
            log.warn(e.toString());
        } finally {
            close("windowClose");
        }
    }

    protected void generateLinkButtonsByAttachments(EmailAttachment[] attachments) {
        for (EmailAttachment attachment: attachments) {
            LinkButton linkButton = componentsFactory.createComponent(LinkButton.class);
            linkButton.setAction(new BaseAction("openLink") {
                @Override
                public void actionPerform(Component component) {
                    exportDisplay.show(new ByteArrayDataProvider(attachment.getData()), attachment.getName());
                }
                @Override
                public String getCaption() {
                    return attachment.getName();
                }
            });
            attachmentsBox.add(linkButton);
        }
    }

}