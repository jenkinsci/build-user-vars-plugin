package org.jenkinsci.plugins.builduser;

import com.sonyericsson.rebuild.RebuildCause;

import org.jenkinsci.plugins.builduser.varsetter.IUsernameSettable;
import org.jenkinsci.plugins.builduser.varsetter.impl.SCMTriggerCauseDeterminant;
import org.jenkinsci.plugins.builduser.varsetter.impl.TimerTriggerCauseDeterminant;
import org.jenkinsci.plugins.builduser.varsetter.impl.UserCauseDeterminant;
import org.jenkinsci.plugins.builduser.varsetter.impl.UserIdCauseDeterminant;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Cause;
import hudson.model.Cause.UserCause;
import hudson.model.Cause.UserIdCause;
import hudson.model.CauseAction;
import hudson.model.Job;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildWrapperDescriptor;
import hudson.triggers.SCMTrigger;
import hudson.triggers.TimerTrigger;
import jenkins.model.Jenkins;
import jenkins.tasks.SimpleBuildWrapper;

/**
 * This plugin is used to set build user variables, see {@link IUsernameSettable}:
 *
 * @author GKonovalenko
 * @see IUsernameSettable
 */
@SuppressWarnings("deprecation")
public class BuildUser extends SimpleBuildWrapper {

    private static final String EXTENSION_DISPLAY_NAME = "Set jenkins user build variables";


    @DataBoundConstructor
    public BuildUser() {
        //noop
    }

    public void setUp(Context context, Run<?, ?> build, FilePath workspace, Launcher launcher, TaskListener listener, EnvVars initialEnvironment) {
        Map<String, String> variables = new HashMap<>();
        makeUserBuildVariables(build, variables);
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            context.env(entry.getKey(), entry.getValue());
        }
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

        //when the build is rebuild
        List<CauseAction> actions = build.getActions(CauseAction.class);
        UserIdCause userIdCause = null;
        RebuildCause rebuildCause = null;
        for (CauseAction action : actions) {
            UserIdCause uc = action.findCause(UserIdCause.class);
            if (uc != null) {
                userIdCause = uc;
            }
            RebuildCause rc = action.findCause(RebuildCause.class);
            if (rc != null) {
                rebuildCause = rc;
            }
        }
        if (rebuildCause != null && userIdCause != null) {
            if (new UserIdCauseDeterminant().setJenkinsUserBuildVars(userIdCause, variables)) {
                return;
            }
        }

        // set BUILD_USER_NAME to fixed value if the build was triggered by a change in the scm
        SCMTrigger.SCMTriggerCause scmTriggerCause = (SCMTrigger.SCMTriggerCause) build.getCause(SCMTrigger.SCMTriggerCause.class);
        if (new SCMTriggerCauseDeterminant().setJenkinsUserBuildVars(scmTriggerCause, variables)) {
            return;
        }

        TimerTrigger.TimerTriggerCause timerTriggerCause = (TimerTrigger.TimerTriggerCause) build.getCause(TimerTrigger.TimerTriggerCause.class);
        if (new TimerTriggerCauseDeterminant().setJenkinsUserBuildVars(timerTriggerCause, variables)) {
            return;
        }
        /* Try to use UserIdCause to get & set jenkins user build variables */
        userIdCause = (UserIdCause) build.getCause(UserIdCause.class);
        if (new UserIdCauseDeterminant().setJenkinsUserBuildVars(userIdCause, variables)) {
            return;
        }

        // Try to use deprecated UserCause to get & set jenkins user build variables
        UserCause userCause = (UserCause) build.getCause(UserCause.class);
        if (new UserCauseDeterminant().setJenkinsUserBuildVars(userCause, variables)) {
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

