package org.jenkinsci.plugins.builduser.varsetter.impl;

import java.util.Map;

import org.jenkinsci.plugins.builduser.utils.UsernameUtils;
import org.jenkinsci.plugins.builduser.varsetter.IUsernameSettable;

import hudson.model.Cause;

public class RemoteCauseDeterminant implements IUsernameSettable<Cause.RemoteCause> {

    @Override
    public boolean setJenkinsUserBuildVars(Cause.RemoteCause cause, Map<String, String> variables) {
        if (cause == null) {
            return false;
        }

        //As of Jenkins 2.51 remote cause is set the build was triggered using token and real user is not set
        UsernameUtils.setUsernameVars(cause.getShortDescription(), variables);
        variables.put(BUILD_USER_ID, "remoteTrigger");
        return true;
    }

    @Override
    public Class<Cause.RemoteCause> getUsedCauseClass() {
        return Cause.RemoteCause.class;
    }
}
