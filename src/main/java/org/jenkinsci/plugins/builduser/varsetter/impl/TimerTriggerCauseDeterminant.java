package org.jenkinsci.plugins.builduser.varsetter.impl;

import java.util.Map;

import org.jenkinsci.plugins.builduser.utils.UsernameUtils;
import org.jenkinsci.plugins.builduser.varsetter.IUsernameSettable;

import hudson.triggers.TimerTrigger;

public class TimerTriggerCauseDeterminant implements IUsernameSettable<TimerTrigger.TimerTriggerCause> {

	private static final String TIMER_TRIGGER_DUMMY_USER_NAME = "Timer Trigger";
	private static final String TIMER_TRIGGER_DUMMY_USER_ID = "timer";

    @Override
	public boolean setJenkinsUserBuildVars(TimerTrigger.TimerTriggerCause cause, Map<String, String> variables) {
		if (cause == null) {
			return false;
		}

		UsernameUtils.setUsernameVars(TIMER_TRIGGER_DUMMY_USER_NAME, variables);
		variables.put(BUILD_USER_ID, TIMER_TRIGGER_DUMMY_USER_ID);
		return true;
	}

	@Override
	public Class<TimerTrigger.TimerTriggerCause> getUsedCauseClass() {
		return TimerTrigger.TimerTriggerCause.class;
	}
}
