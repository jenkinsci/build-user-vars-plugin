package org.jenkinsci.plugins.builduser.varsetter.impl;

import org.jenkinsci.plugins.builduser.utils.UsernameUtils;
import org.jenkinsci.plugins.builduser.varsetter.IUsernameSettable;

import java.util.Map;

import hudson.triggers.SCMTrigger;
import hudson.triggers.SCMTrigger.SCMTriggerCause;

public class SCMTriggerCauseDeterminant implements IUsernameSettable<SCMTrigger.SCMTriggerCause> {

    final Class<SCMTrigger.SCMTriggerCause> causeClass = SCMTrigger.SCMTriggerCause.class;

    public boolean setJenkinsUserBuildVars(SCMTriggerCause cause, Map<String, String> variables) {
        if (cause != null) {
            UsernameUtils.setUsernameVars("SCMTrigger", variables);
            return true;
        } else {
            return false;
        }
    }

    public Class<SCMTriggerCause> getUsedCauseClass() {
        return causeClass;
    }

}
