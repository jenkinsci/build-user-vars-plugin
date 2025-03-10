package org.jenkinsci.plugins.builduser.varsetter.impl;

import jenkins.branch.BranchIndexingCause;
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

class BranchIndexingTriggerDeterminantTest {

    @Test
    void usedCauseClassIsBranchIndexingCause() {
        assertThat(new BranchIndexingTriggerDeterminant().getUsedCauseClass(), equalTo(BranchIndexingCause.class));
    }

    @Test
    void setVarsReturnsFalseWithoutBuildUserVarsOnNullCause() {
        Map<String, String> variables = new HashMap<>();
        assertFalse(new BranchIndexingTriggerDeterminant().setJenkinsUserBuildVars(null, variables));
        assertThat(variables, equalTo(Collections.emptyMap()));
    }

    @Test
    void setVarsReturnsTrueWithBuildUsersVarsOnValidCause() throws Exception {
        Map<String, String> variables = new HashMap<>();
        assertTrue(new BranchIndexingTriggerDeterminant().setJenkinsUserBuildVars(mockCause(), variables));
        assertThat(variables, allOf(hasEntry(BuildUserVariable.USERNAME, "Branch Indexing"),
                hasEntry(BuildUserVariable.FIRST_NAME, "Branch"),
                hasEntry(BuildUserVariable.LAST_NAME, "Indexing"),
                hasEntry(BuildUserVariable.ID, "branchIndexing")
        ));
    }

    private static BranchIndexingCause mockCause() throws Exception {
        Constructor<BranchIndexingCause> ctor = BranchIndexingCause.class.getDeclaredConstructor();
        ctor.setAccessible(true);
        return ctor.newInstance();
    }

}