package org.jenkinsci.plugins.builduser.varsetter.impl;

import org.jenkinsci.plugins.builduser.utils.UsernameUtils;
import org.jenkinsci.plugins.builduser.varsetter.IUsernameSettable;

import java.util.Map;

import hudson.triggers.TimerTrigger;

public class TimerTriggerCauseDeterminant implements IUsernameSettable<TimerTrigger.TimerTriggerCause> {

    final Class<TimerTrigger.TimerTriggerCause> causeClass = TimerTrigger.TimerTriggerCause.class;

    public boolean setJenkinsUserBuildVars(TimerTrigger.TimerTriggerCause cause, Map<String, String> variables) {
        if (cause != null) {
            UsernameUtils.setUsernameVars("TimerTrigger", variables);
            return true;
        } else {
            return false;
        }
    }

    public Class<TimerTrigger.TimerTriggerCause> getUsedCauseClass() {
        return causeClass;
    }

}
