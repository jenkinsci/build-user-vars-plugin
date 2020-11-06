package org.jenkinsci.plugins.builduser.varsetter.impl;

import hudson.triggers.SCMTrigger;
import hudson.triggers.SCMTrigger.SCMTriggerCause;

import java.util.Map;

import org.jenkinsci.plugins.builduser.utils.UsernameUtils;
import org.jenkinsci.plugins.builduser.varsetter.IUsernameSettable;

public class SCMTriggerCauseDeterminant implements IUsernameSettable<SCMTrigger.SCMTriggerCause> {

	final Class<SCMTrigger.SCMTriggerCause> causeClass = SCMTrigger.SCMTriggerCause.class;
	
	public boolean setJenkinsUserBuildVars(SCMTriggerCause cause,
			Map<String, String> variables) {
		
        if (cause != null) {
			UsernameUtils.setUsernameVars("SCM Change", variables);
			variables.put(BUILD_USER_ID, "scmChange");
			
			return true;
		} else {
			return false;
		}
	}

	public Class<SCMTriggerCause> getUsedCauseClass() {

		return causeClass;
	}

}
