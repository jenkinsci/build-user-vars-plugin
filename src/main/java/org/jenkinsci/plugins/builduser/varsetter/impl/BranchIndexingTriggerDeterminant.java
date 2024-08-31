package org.jenkinsci.plugins.builduser.varsetter.impl;

import jenkins.branch.BranchIndexingCause;
import org.jenkinsci.plugins.builduser.utils.BuildUserVariable;
import org.jenkinsci.plugins.builduser.utils.UsernameUtils;
import org.jenkinsci.plugins.builduser.varsetter.IUsernameSettable;

import java.util.Map;

public class BranchIndexingTriggerDeterminant implements IUsernameSettable<BranchIndexingCause> {

    @Override
    public boolean setJenkinsUserBuildVars(BranchIndexingCause cause, Map<String, String> variables) {
        if (cause == null) {
            return false;
        }

        UsernameUtils.setUsernameVars("Branch Indexing", variables);
        variables.put(BuildUserVariable.ID, "branchIndexing");
        return true;
    }

    @Override
    public Class<BranchIndexingCause> getUsedCauseClass() {
        return BranchIndexingCause.class;
    }
}
