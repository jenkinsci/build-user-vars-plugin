package org.jenkinsci.plugins.builduser.varsetter.impl;

import jenkins.branch.BranchIndexingCause;
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
        assertThat(variables, allOf(hasEntry(BuildUserVariable.USERNAME, "Branch Indexing"),
                hasEntry(BuildUserVariable.FIRST_NAME, "Branch"),
                hasEntry(BuildUserVariable.LAST_NAME, "Indexing"),
                hasEntry(BuildUserVariable.ID, "branchIndexing")
        ));
    }

    private BranchIndexingCause mockCause() throws Exception {
        Constructor<BranchIndexingCause> ctor = BranchIndexingCause.class.getDeclaredConstructor();
        ctor.setAccessible(true);
        return ctor.newInstance();
    }

}