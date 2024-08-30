package org.jenkinsci.plugins.builduser.varsetter.impl;

import hudson.model.Cause;
import org.jenkinsci.plugins.builduser.utils.BuildUserVariable;
import org.jenkinsci.plugins.builduser.utils.UsernameUtils;
import org.jenkinsci.plugins.builduser.varsetter.IUsernameSettable;

import java.util.Map;

import static java.lang.String.format;

public class RemoteCauseDeterminant implements IUsernameSettable<Cause.RemoteCause> {

    @Override
    public boolean setJenkinsUserBuildVars(Cause.RemoteCause cause, Map<String, String> variables) {
        if (cause == null) {
            return false;
        }

        //As of Jenkins 2.51 remote cause is set the build was triggered using token and real user is not set
        UsernameUtils.setUsernameVars(format("%s %s", cause.getAddr(), cause.getNote()), variables);
        variables.put(BuildUserVariable.ID, "remoteRequest");
        return true;
    }

    @Override
    public Class<Cause.RemoteCause> getUsedCauseClass() {
        return Cause.RemoteCause.class;
    }
}
