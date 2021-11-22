package org.jenkinsci.plugins.builduser;

import edu.umd.cs.findbugs.annotations.NonNull;

import hudson.EnvVars;
import hudson.Extension;
import hudson.model.EnvironmentContributor;
import hudson.model.Run;
import hudson.model.TaskListener;

@Extension
public class BuildUserVarsEnvironmentContributor extends EnvironmentContributor {

    @Override
    public void buildEnvironmentFor(
            @NonNull Run r, @NonNull EnvVars envs, @NonNull TaskListener listener) {
        if (BuildUserVarsConfig.get().isAllBuilds()) {
            BuildUser.makeUserBuildVariables(r, envs);
        }
    }
}
