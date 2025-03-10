package org.jenkinsci.plugins.builduser.varsetter.impl;

import hudson.model.Cause.UserCause;
import org.jenkinsci.plugins.builduser.utils.BuildUserVariable;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserCauseDeterminantTest {

    @Test
    void usedCauseClassIsUserCause() {
        assertThat(new UserCauseDeterminant().getUsedCauseClass(), equalTo(UserCause.class));
    }

    @Test
    void setVarsReturnsFalseWithoutBuildUserVarsOnNullCause() {
        Map<String, String> variables = new HashMap<>();
        assertFalse(new UserCauseDeterminant().setJenkinsUserBuildVars(null, variables));
        assertThat(variables, equalTo(Collections.emptyMap()));
    }

    @Test
    void setVarsReturnsTrueWithBuildUsersVarsOnValidCause() {
        Map<String, String> variables = new HashMap<>();
        assertTrue(new UserCauseDeterminant().setJenkinsUserBuildVars(mockCause(), variables));
        assertThat(variables, allOf(hasEntry(BuildUserVariable.USERNAME, "John Doe"),
                hasEntry(BuildUserVariable.FIRST_NAME, "John"),
                hasEntry(BuildUserVariable.LAST_NAME, "Doe")
        ));
    }

    private static UserCause mockCause() {
        return new UserCause() {
            @Override
            public String getUserName() {
                return "John Doe";
            }
        };
    }

}