package org.jenkinsci.plugins.builduser.varsetter.impl;

import hudson.triggers.SCMTrigger;
import hudson.triggers.SCMTrigger.SCMTriggerCause;

import java.lang.reflect.Field;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.builduser.utils.UsernameUtils;
import org.jenkinsci.plugins.builduser.varsetter.IUsernameSettable;

public class SCMTriggerCauseDeterminant implements IUsernameSettable<SCMTrigger.SCMTriggerCause> {

	final Class<SCMTrigger.SCMTriggerCause> causeClass = SCMTrigger.SCMTriggerCause.class;
	
	public boolean setJenkinsUserBuildVars(SCMTriggerCause cause,
			Map<String, String> variables) {
		
        if (cause != null) {
			UsernameUtils.setUsernameVars("SCM Change", variables);
			variables.put(BUILD_USER_ID, "scmChange");
			
			// sets pushedBy provided by GitHubPushCause as BUILD_USER_ID
			try {
				Field pushedByField = cause.getClass().getDeclaredField("pushedBy");
				pushedByField.setAccessible(true);
				String pushedBy = (String) pushedByField.get(cause);
				if (StringUtils.isNotEmpty(pushedBy)) {
					variables.put(BUILD_USER_ID, pushedBy);
				}
			} catch (ReflectiveOperationException exception) {
				// do nothing 
			}

			return true;
		} else {
			return false;
		}
	}

	public Class<SCMTriggerCause> getUsedCauseClass() {

		return causeClass;
	}

}
