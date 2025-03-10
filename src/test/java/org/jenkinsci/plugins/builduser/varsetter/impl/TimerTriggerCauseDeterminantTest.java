package org.jenkinsci.plugins.builduser.varsetter.impl;

import hudson.triggers.TimerTrigger.TimerTriggerCause;
import org.jenkinsci.plugins.builduser.utils.BuildUserVariable;
import org.jenkinsci.plugins.builduser.utils.UsernameUtils;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TimerTriggerCauseDeterminantTest {

    @Test
    void usedCauseClassIsSCMTriggerCause() {
        assertThat(new TimerTriggerCauseDeterminant().getUsedCauseClass(), equalTo(TimerTriggerCause.class));
    }

    @Test
    void setVarsReturnsFalseWithoutBuildUserVarsOnNullCause() {
        Map<String, String> variables = new HashMap<>();
        assertFalse(new TimerTriggerCauseDeterminant().setJenkinsUserBuildVars(null, variables));
        assertThat(variables, equalTo(Collections.emptyMap()));
    }

    @Test
    void setVarsReturnsTrueWithBuildUsersVarsOnValidCause() throws Exception {
        Map<String, String> variables = new HashMap<>();
        assertTrue(new TimerTriggerCauseDeterminant().setJenkinsUserBuildVars(mockCause(), variables));
        assertThat(variables, allOf(hasEntry(BuildUserVariable.USERNAME, TimerTriggerCauseDeterminant.TIMER_TRIGGER_DUMMY_USER_NAME),
                hasEntry(BuildUserVariable.FIRST_NAME, UsernameUtils.getFirstName(TimerTriggerCauseDeterminant.TIMER_TRIGGER_DUMMY_USER_NAME)),
                hasEntry(BuildUserVariable.LAST_NAME, UsernameUtils.getLastName(TimerTriggerCauseDeterminant.TIMER_TRIGGER_DUMMY_USER_NAME)),
                hasEntry(BuildUserVariable.ID, TimerTriggerCauseDeterminant.TIMER_TRIGGER_DUMMY_USER_ID)
        ));
    }

    private static TimerTriggerCause mockCause() throws Exception {
        Constructor<TimerTriggerCause> ctor = TimerTriggerCause.class.getDeclaredConstructor();
        ctor.setAccessible(true);
        return ctor.newInstance();
    }

}