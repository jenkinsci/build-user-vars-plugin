package org.jenkinsci.plugins.builduser.varsetter.impl;

import org.easymock.EasyMock;
import org.jenkinsci.plugins.builduser.utils.BuildUserVariable;
import org.jenkinsci.plugins.gwt.GenericCause;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GenericCauseDeterminantTest {

    @Test
    void usedCauseClassIsGenericCause() {
        assertThat(new GenericCauseDeterminant().getUsedCauseClass(), equalTo(GenericCause.class));
    }

    @Test
    void setVarsReturnsFalseWithoutBuildUserVarsOnNullCause() {
        Map<String,String> variables = new HashMap<>();
        assertFalse(new GenericCauseDeterminant().setJenkinsUserBuildVars(null, variables));
        assertThat(variables, equalTo(Collections.emptyMap()));
    }

    @Test
    void setVarsReturnsTrueWithBuildUsersVarsOnValidCause() {
        Map<String, String> variables = new HashMap<>();
        GenericCause mockCause = EasyMock.createMock(GenericCause.class);
        EasyMock.replay(mockCause);

        assertTrue(new GenericCauseDeterminant().setJenkinsUserBuildVars(mockCause, variables));
        assertThat(variables,
                allOf(hasEntry(BuildUserVariable.USERNAME, GenericCauseDeterminant.GENERIC_TRIGGER_DUMMY_USER_NAME),
                        hasEntry(BuildUserVariable.FIRST_NAME, "Generic"),
                        hasEntry(BuildUserVariable.LAST_NAME, "Webhook"),
                        hasEntry(BuildUserVariable.ID, GenericCauseDeterminant.GENERIC_TRIGGER_DUMMY_USER_ID)
        ));

        EasyMock.verify(mockCause);
    }
}
