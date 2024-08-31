package org.jenkinsci.plugins.builduser.varsetter.impl;

import hudson.triggers.SCMTrigger.SCMTriggerCause;
import org.jenkinsci.plugins.builduser.utils.BuildUserVariable;
import org.jenkinsci.plugins.builduser.utils.UsernameUtils;
import org.jenkinsci.plugins.builduser.varsetter.IUsernameSettable;

import java.util.Map;

public class SCMTriggerCauseDeterminant implements IUsernameSettable<SCMTriggerCause> {

	public boolean setJenkinsUserBuildVars(SCMTriggerCause cause, Map<String, String> variables) {
        if (cause == null) {
            return false;
        }

        UsernameUtils.setUsernameVars("SCM Change", variables);
        variables.put(BuildUserVariable.ID, "scmChange");

        return true;
    }

	public Class<SCMTriggerCause> getUsedCauseClass() {
		return SCMTriggerCause.class;
	}
}
