package com.haulmont.addon.yargemailtemplateaddon.web.outboundemail;

import com.haulmont.addon.yargemailtemplateaddon.entity.OutboundEmail;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.LookupPickerField;

import javax.inject.Inject;
import java.util.Map;

public class LayoutTemplateChoiceScreen extends AbstractWindow {

    public static final String PARAM_EMAIL = "outboundEmail";

    @WindowParam(name = PARAM_EMAIL, required = true)
    protected OutboundEmail outboundEmail;
    @Inject
    protected Button nextButton;
    @Inject
    protected LookupPickerField layoutChoiceField;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        nextButton.setEnabled(false);
        layoutChoiceField.addValueChangeListener(e -> {
            if (e != null) {
                nextButton.setEnabled(true);
            } else {
                nextButton.setEnabled(false);
            }
        });
    }

    public void onNextButtonClick() {
        if (outboundEmail != null) {
            outboundEmail.setLayoutTemplate(layoutChoiceField.getValue());
            openEditor("yet$OutboundEmail.edit", outboundEmail, WindowManager.OpenType.DIALOG);
        }
    }

    public void onCancelButtonClick() {
        close("windowClose ");
    }
}