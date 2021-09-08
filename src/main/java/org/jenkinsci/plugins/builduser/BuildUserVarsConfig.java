package org.jenkinsci.plugins.builduser;

import hudson.Extension;
import hudson.ExtensionList;

import jenkins.model.GlobalConfiguration;

import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundSetter;

@Extension
@Symbol("buildUserVars")
public class BuildUserVarsConfig extends GlobalConfiguration {

    /** @return the singleton instance */
    public static BuildUserVarsConfig get() {
        return ExtensionList.lookupSingleton(BuildUserVarsConfig.class);
    }

    /** Whether to activate {@link BuildUserVarsEnvironmentContributor}. */
    private boolean allBuilds;

    public BuildUserVarsConfig() {
        // When Jenkins is restarted, load any saved configuration from disk.
        load();
    }

    public boolean isAllBuilds() {
        return allBuilds;
    }

    @DataBoundSetter
    public void setAllBuilds(boolean allBuilds) {
        this.allBuilds = allBuilds;
        save();
    }
}
