package org.jenkinsci.plugins.builduser.varsetter.impl;

import hudson.model.Cause.RemoteCause;
import org.jenkinsci.plugins.builduser.utils.BuildUserVariable;
import org.jenkinsci.plugins.builduser.utils.UsernameUtils;
import org.jenkinsci.plugins.builduser.varsetter.IUsernameSettable;

import java.util.Map;

public class RemoteCauseDeterminant implements IUsernameSettable<RemoteCause> {

    @Override
    public boolean setJenkinsUserBuildVars(RemoteCause cause, Map<String, String> variables) {
        if (cause == null) {
            return false;
        }

        //As of Jenkins 2.51 remote cause is set the build was triggered using token and real user is not set
        UsernameUtils.setUsernameVars(String.format("%s %s", cause.getAddr(), cause.getNote()), variables);
        variables.put(BuildUserVariable.ID, "remoteRequest");
        return true;
    }

    @Override
    public Class<RemoteCause> getUsedCauseClass() {
        return RemoteCause.class;
    }
}
