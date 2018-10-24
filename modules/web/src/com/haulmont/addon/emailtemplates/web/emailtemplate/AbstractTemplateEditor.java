package com.haulmont.addon.emailtemplates.web.emailtemplate;

import com.haulmont.addon.emailtemplates.entity.EmailTemplate;
import com.haulmont.addon.emailtemplates.entity.ParameterValue;
import com.haulmont.addon.emailtemplates.entity.TemplateParameters;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.reports.entity.Report;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTemplateEditor<T extends EmailTemplate> extends AbstractEditor<T> {

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        CommitContext commitContext = new CommitContext();
        List<Report> reports = new ArrayList<>(getItem().getAttachedReports());
        reports.add(getItem().getEmailBodyReport());
        for (TemplateParameters parameters : getItem().getParameters()) {
            if (parameters.getReport() != null && !reports.contains(parameters.getReport())) {
                commitContext.addInstanceToRemove(parameters);
                for (ParameterValue parameterValue : parameters.getParameterValues()) {
                    commitContext.addInstanceToRemove(parameterValue);
                }
            } else {
                for (ParameterValue parameterValue : parameters.getParameterValues()) {
                    commitContext.addInstanceToCommit(parameterValue);
                }
            }
        }
        getDsContext().getDataSupplier().commit(commitContext);
        return super.postCommit(committed, close);
    }
}
