package org.jenkinsci.plugins.builduser.varsetter.impl;

import org.jenkinsci.plugins.builduser.utils.BuildUserVariable;
import org.jenkinsci.plugins.builduser.utils.UsernameUtils;
import org.jenkinsci.plugins.builduser.varsetter.IUsernameSettable;
import org.jenkinsci.plugins.gwt.GenericCause;

import java.util.Map;

public class GenericCauseDeterminant implements IUsernameSettable<GenericCause> {

    protected static final String GENERIC_TRIGGER_DUMMY_USER_NAME = "Generic Webhook Trigger";
    protected static final String GENERIC_TRIGGER_DUMMY_USER_ID = "genericWebhook";

    @Override
    public boolean setJenkinsUserBuildVars(GenericCause cause, Map<String, String> variables) {
        if (cause == null) {
            return false;
        }

        UsernameUtils.setUsernameVars(GENERIC_TRIGGER_DUMMY_USER_NAME, variables);
        variables.put(BuildUserVariable.ID, GENERIC_TRIGGER_DUMMY_USER_ID);
        return true;
    }

    @Override
    public Class<GenericCause> getUsedCauseClass() {
        return GenericCause.class;
    }
}
