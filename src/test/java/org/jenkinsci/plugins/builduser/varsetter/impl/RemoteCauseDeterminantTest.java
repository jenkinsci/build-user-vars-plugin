package org.jenkinsci.plugins.builduser.varsetter.impl;

import hudson.model.Cause.RemoteCause;
import org.jenkinsci.plugins.builduser.utils.BuildUserVariable;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RemoteCauseDeterminantTest {
    @Test
    public void usedCauseClassIsRemoteCause() {
        assertThat(new RemoteCauseDeterminant().getUsedCauseClass(), equalTo(RemoteCause.class));
    }

    @Test
    public void setVarsReturnsFalseWithoutBuildUserVarsOnNullCause() {
        Map<String, String> variables = new HashMap<>();
        assertFalse(new RemoteCauseDeterminant().setJenkinsUserBuildVars(null, variables));
        assertThat(variables, equalTo(Collections.emptyMap()));
    }

    @Test
    public void setVarsReturnsTrueWithBuildUsersVarsOnValidCause() throws Exception {
        Map<String, String> variables = new HashMap<>();
        assertTrue(new RemoteCauseDeterminant().setJenkinsUserBuildVars(mockCause(), variables));
        assertThat(variables, allOf(hasEntry(BuildUserVariable.USERNAME, "host note"),
                hasEntry(BuildUserVariable.FIRST_NAME, "host"),
                hasEntry(BuildUserVariable.LAST_NAME, "note"),
                hasEntry(BuildUserVariable.ID, "remoteRequest")
        ));
    }

    private RemoteCause mockCause() throws Exception {
        Constructor<RemoteCause> ctor = RemoteCause.class.getDeclaredConstructor(String.class, String.class);
        ctor.setAccessible(true);
        return ctor.newInstance("host", "note");
    }

}