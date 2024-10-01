package org.jenkinsci.plugins.builduser;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Cause.RemoteCause;
import hudson.model.Cause.UpstreamCause;
import hudson.model.Cause.UserCause;
import hudson.model.Cause.UserIdCause;
import hudson.model.Job;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildWrapperDescriptor;
import hudson.triggers.SCMTrigger.SCMTriggerCause;
import hudson.triggers.TimerTrigger.TimerTriggerCause;
import jenkins.branch.BranchEventCause;
import jenkins.branch.BranchIndexingCause;
import jenkins.model.Jenkins;
import jenkins.tasks.SimpleBuildWrapper;
import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.builduser.varsetter.IUsernameSettable;
import org.jenkinsci.plugins.builduser.varsetter.impl.*;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * This plugin is used to set build user variables, see {@link IUsernameSettable}:
 *
 * @author GKonovalenko
 * @see IUsernameSettable
 */
public class BuildUser extends SimpleBuildWrapper {

    private static final Logger log = Logger.getLogger(BuildUser.class.getName());

    private static final String EXTENSION_DISPLAY_NAME = "Set jenkins user build variables";


    @DataBoundConstructor
    public BuildUser() {
        //noop
    }

    @Override
    public void setUp(Context context, Run<?, ?> build, FilePath workspace,
                      Launcher launcher, TaskListener listener, EnvVars initialEnvironment) {
        Map<String, String> variables = new HashMap<>();
        makeUserBuildVariables(build, variables);
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            context.env(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Retrieve user cause that triggered this build and populate variables accordingly
     * <p>
     * TODO: The whole hierarchy and way of applying could be refactored.
     */
    @Restricted(NoExternalUse.class)
    static void makeUserBuildVariables(@NonNull Run<?, ?> build, @NonNull Map<String, String> variables) {

        /* Try to use UserIdCause to get & set jenkins user build variables */
        UserIdCause userIdCause = build.getCause(UserIdCause.class);
        if (new UserIdCauseDeterminant().setJenkinsUserBuildVars(userIdCause, variables)) {
            return;
        }

        // Try to use deprecated UserCause to get & set jenkins user build variables
        @SuppressWarnings("deprecation")
        UserCause userCause = build.getCause(UserCause.class);
        if (new UserCauseDeterminant().setJenkinsUserBuildVars(userCause, variables)) {
            return;
        }

        // If build has been triggered form an upstream build, get UserCause from there to set user build variables
        UpstreamCause upstreamCause = build.getCause(UpstreamCause.class);
        if (upstreamCause != null) {
            Job<?, ?> job = Jenkins.get().getItemByFullName(upstreamCause.getUpstreamProject(), Job.class);
            if (job != null) {
                Run<?, ?> upstream = job.getBuildByNumber(upstreamCause.getUpstreamBuild());
                if (upstream != null) {
                    makeUserBuildVariables(upstream, variables);
                    return;
                }
            }
        }

        // Other causes should be checked after as build can be triggered automatically and later rerun manually by a human.
        // In that case there will be multiple causes and the direct manually one is preferred to set in a variable.
        handleOtherCausesOrLogWarningIfUnhandled(build, variables);
    }

    private static void handleOtherCausesOrLogWarningIfUnhandled(@NonNull Run<?, ?> build, @NonNull Map<String, String> variables) {
        // set BUILD_USER_NAME and ID to fixed value if the build was triggered by a change in the scm, timer or remotely with token
        SCMTriggerCause scmTriggerCause = build.getCause(SCMTriggerCause.class);
        if (new SCMTriggerCauseDeterminant().setJenkinsUserBuildVars(scmTriggerCause, variables)) {
            return;
        }

        TimerTriggerCause timerTriggerCause = build.getCause(TimerTriggerCause.class);
        if (new TimerTriggerCauseDeterminant().setJenkinsUserBuildVars(timerTriggerCause, variables)) {
            return;
        }

        RemoteCause remoteTriggerCause = build.getCause(RemoteCause.class);
        if (new RemoteCauseDeterminant().setJenkinsUserBuildVars(remoteTriggerCause, variables)) {
            return;
        }

        try {
            BranchIndexingCause branchIndexingCause = build.getCause(BranchIndexingCause.class);
            if (new BranchIndexingTriggerDeterminant().setJenkinsUserBuildVars(branchIndexingCause, variables)) {
                return;
            }

            BranchEventCause branchEventCause = build.getCause(BranchEventCause.class);
            if (branchEventCause != null) {
                // branch event cause does not have to be logged.
                return;
            }
        } catch (NoClassDefFoundError e) {
            log.fine("It seems the branch-api plugin is not installed, skipping.");
        }

        log.warning(() -> "Unsupported cause type(s): " + Arrays.toString(build.getCauses().toArray()));
    }

    @Extension
    @Symbol("withBuildUser")
    public static class DescriptorImpl extends BuildWrapperDescriptor {
        @Override
        public boolean isApplicable(AbstractProject<?, ?> item) {
            return true;
        }

        @NonNull
        @Override
        public String getDisplayName() {
            return EXTENSION_DISPLAY_NAME;
        }
    }
}

