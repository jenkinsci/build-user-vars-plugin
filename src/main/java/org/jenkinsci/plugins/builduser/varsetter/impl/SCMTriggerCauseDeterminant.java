package org.jenkinsci.plugins.builduser.varsetter.impl;

import hudson.triggers.SCMTrigger;
import hudson.triggers.SCMTrigger.SCMTriggerCause;
import org.jenkinsci.plugins.builduser.utils.BuildUserVariable;
import org.jenkinsci.plugins.builduser.utils.UsernameUtils;
import org.jenkinsci.plugins.builduser.varsetter.IUsernameSettable;

import java.util.Map;

public class SCMTriggerCauseDeterminant implements IUsernameSettable<SCMTrigger.SCMTriggerCause> {

	static final Class<SCMTrigger.SCMTriggerCause> causeClass = SCMTrigger.SCMTriggerCause.class;

	public boolean setJenkinsUserBuildVars(SCMTriggerCause cause, Map<String, String> variables) {
        if (cause != null) {
			UsernameUtils.setUsernameVars("SCM Change", variables);
			variables.put(BuildUserVariable.ID, "scmChange");
			
			return true;
		} else {
			return false;
		}
	}

	public Class<SCMTriggerCause> getUsedCauseClass() {

		return causeClass;
	}

}
