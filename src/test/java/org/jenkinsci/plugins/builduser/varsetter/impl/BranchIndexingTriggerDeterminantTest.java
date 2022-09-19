package org.jenkinsci.plugins.builduser.varsetter.impl;

import jenkins.branch.BranchIndexingCause;
import org.jenkinsci.plugins.builduser.varsetter.IUsernameSettable;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BranchIndexingTriggerDeterminantTest {
    @Test
    public void usedCauseClassIsBranchIndexingCause() {
        assertThat(new BranchIndexingTriggerDeterminant().getUsedCauseClass(), equalTo(BranchIndexingCause.class));
    }

    @Test
    public void setVarsReturnsFalseWithoutBuildUserVarsOnNullCause() {
        Map<String, String> variables = new HashMap<>();
        assertFalse(new BranchIndexingTriggerDeterminant().setJenkinsUserBuildVars(null, variables));
        assertThat(variables, equalTo(Collections.emptyMap()));
    }

    @Test
    public void setVarsReturnsTrueWithBuildUsersVarsOnValidCause() throws Exception {
        Map<String, String> variables = new HashMap<>();
        assertTrue(new BranchIndexingTriggerDeterminant().setJenkinsUserBuildVars(mockCause(), variables));
        assertThat(variables, allOf(hasEntry(IUsernameSettable.BUILD_USER_VAR_NAME, "Branch Indexing"),
                hasEntry(IUsernameSettable.BUILD_USER_FIRST_NAME_VAR_NAME, "Branch"),
                hasEntry(IUsernameSettable.BUILD_USER_LAST_NAME_VAR_NAME, "Indexing"),
                hasEntry(IUsernameSettable.BUILD_USER_ID, "branchIndexing")
        ));
    }

    private BranchIndexingCause mockCause() throws Exception {
        Constructor<BranchIndexingCause> ctor = BranchIndexingCause.class.getDeclaredConstructor();
        ctor.setAccessible(true);
        return ctor.newInstance();
    }

}