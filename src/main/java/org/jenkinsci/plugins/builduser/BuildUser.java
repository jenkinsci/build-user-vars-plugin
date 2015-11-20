package org.jenkinsci.plugins.builduser;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Cause;
import hudson.model.Cause.UserCause;
import hudson.model.Cause.UserIdCause;
import hudson.model.Job;
import hudson.model.Run;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;
import hudson.triggers.SCMTrigger;

import java.io.IOException;
import java.util.Map;
import javax.annotation.Nonnull;

import jenkins.model.Jenkins;

import org.jenkinsci.plugins.builduser.utils.ClassUtils;
import org.jenkinsci.plugins.builduser.varsetter.IUsernameSettable;
import org.jenkinsci.plugins.builduser.varsetter.impl.SCMTriggerCauseDeterminant;
import org.jenkinsci.plugins.builduser.varsetter.impl.UserCauseDeterminant;
import org.jenkinsci.plugins.builduser.varsetter.impl.UserIdCauseDeterminant;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * This plugin is used to set build user variables, see {@link IUsernameSettable}:
 * 
 * @see IUsernameSettable
 * 
 * @author GKonovalenko
 */
@SuppressWarnings("deprecation")
public class BuildUser extends BuildWrapper {

    private static final String EXTENSION_DISPLAY_NAME = "Set jenkins user build variables";


    @DataBoundConstructor
    public BuildUser() {
        //noop
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Environment setUp(final AbstractBuild build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
        /* noop */
        return new Environment() {
            @Override
            public void buildEnvVars(Map<String, String> env) {
              makeUserBuildVariables(build, env);
            }
          };
    }


    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void makeBuildVariables(AbstractBuild build,
            Map<String, String> variables) {
        makeUserBuildVariables(build, variables);
    }

    /**
     * Retrieve user cause that triggered this build and populate variables accordingly
     */
    private void makeUserBuildVariables(@Nonnull Run build, @Nonnull Map<String, String> variables) {

        // If build has been triggered form an upstream build, get UserCause from there to set user build variables
        Cause.UpstreamCause upstreamCause = (Cause.UpstreamCause) build.getCause(Cause.UpstreamCause.class);
        if (upstreamCause != null) {
            Job job = Jenkins.getInstance().getItemByFullName(upstreamCause.getUpstreamProject(), Job.class);
            if (job != null) {
            Run upstream = job.getBuildByNumber(upstreamCause.getUpstreamBuild());
            if (upstream != null) {
                makeUserBuildVariables(upstream, variables);
            }
            }
        }

        // set BUILD_USER_NAME to fixed value if the build was triggered by a change in the scm
        SCMTrigger.SCMTriggerCause scmTriggerCause = (SCMTrigger.SCMTriggerCause) build.getCause(SCMTrigger.SCMTriggerCause.class);
        if (new SCMTriggerCauseDeterminant().setJenkinsUserBuildVars(scmTriggerCause, variables)) {
            return;
        }

        /* Try to use UserIdCause to get & set jenkins user build variables */
        UserIdCause userIdCause = (UserIdCause) build.getCause(UserIdCause.class);
        if(new UserIdCauseDeterminant().setJenkinsUserBuildVars(userIdCause, variables)) {
            return;
        }

        // Try to use deprecated UserCause to get & set jenkins user build variables
        UserCause userCause = (UserCause) build.getCause(UserCause.class);
        if(new UserCauseDeterminant().setJenkinsUserBuildVars(userCause, variables)) {
            return;
        }
    }


    @Extension
    public static class DescriptorImpl extends BuildWrapperDescriptor {
        @Override
        public boolean isApplicable(AbstractProject<?, ?> item) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return EXTENSION_DISPLAY_NAME;
        }
    }
}

