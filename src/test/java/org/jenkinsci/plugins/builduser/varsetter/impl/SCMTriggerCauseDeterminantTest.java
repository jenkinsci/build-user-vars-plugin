package org.jenkinsci.plugins.builduser.varsetter.impl;

import hudson.triggers.SCMTrigger.SCMTriggerCause;
import org.jenkinsci.plugins.builduser.utils.BuildUserVariable;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SCMTriggerCauseDeterminantTest {

    @Test
    void usedCauseClassIsSCMTriggerCause() {
        assertThat(new SCMTriggerCauseDeterminant().getUsedCauseClass(), equalTo(SCMTriggerCause.class));
    }

    @Test
    void setVarsReturnsFalseWithoutBuildUserVarsOnNullCause() {
        Map<String, String> variables = new HashMap<>();
        assertFalse(new SCMTriggerCauseDeterminant().setJenkinsUserBuildVars(null, variables));
        assertThat(variables, equalTo(Collections.emptyMap()));
    }

    @Test
    void setVarsReturnsTrueWithBuildUsersVarsOnValidCause() throws Exception {
        Map<String, String> variables = new HashMap<>();
        assertTrue(new SCMTriggerCauseDeterminant().setJenkinsUserBuildVars(mockCause(), variables));
        assertThat(variables, allOf(hasEntry(BuildUserVariable.USERNAME, "SCM Change"),
                hasEntry(BuildUserVariable.FIRST_NAME, "SCM"),
                hasEntry(BuildUserVariable.LAST_NAME, "Change"),
                hasEntry(BuildUserVariable.ID, "scmChange")
        ));
    }

    private static SCMTriggerCause mockCause() throws Exception {
        Constructor<SCMTriggerCause> ctor = SCMTriggerCause.class.getDeclaredConstructor();
        ctor.setAccessible(true);
        return ctor.newInstance();
    }

}