package org.jenkinsci.plugins.builduser.varsetter.impl;

import jenkins.branch.BranchIndexingCause;
import org.jenkinsci.plugins.builduser.utils.BuildUserVariable;
import org.jenkinsci.plugins.builduser.utils.UsernameUtils;
import org.jenkinsci.plugins.builduser.varsetter.IUsernameSettable;

import java.util.Map;

public class BranchIndexingTriggerDeterminant implements IUsernameSettable<BranchIndexingCause> {
    private static final Class<BranchIndexingCause> causeClass = BranchIndexingCause.class;

    @Override
    public boolean setJenkinsUserBuildVars(BranchIndexingCause cause, Map<String, String> variables) {
        if (cause != null) {
            UsernameUtils.setUsernameVars("Branch Indexing", variables);
            variables.put(BuildUserVariable.ID, "branchIndexing");
            return true;
        }
        return false;
    }

    @Override
    public Class<BranchIndexingCause> getUsedCauseClass() {
        return causeClass;
    }
}
