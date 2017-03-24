package org.jenkinsci.plugins.builduser;

import static java.lang.String.format;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Cause;
import hudson.model.Cause.UserCause;
import hudson.model.Cause.UserIdCause;
import hudson.model.Job;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildWrapperDescriptor;
import hudson.triggers.SCMTrigger;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;
import javax.annotation.Nonnull;

import hudson.triggers.TimerTrigger;
import jenkins.model.Jenkins;
import jenkins.tasks.SimpleBuildWrapper;

import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.builduser.varsetter.IUsernameSettable;
import org.jenkinsci.plugins.builduser.varsetter.impl.RemoteCauseDeterminant;
import org.jenkinsci.plugins.builduser.varsetter.impl.SCMTriggerCauseDeterminant;
import org.jenkinsci.plugins.builduser.varsetter.impl.TimerTriggerCauseDeterminant;
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
public class BuildUser extends SimpleBuildWrapper {

	private static final Logger log = Logger.getLogger(BuildUser.class.getName());

	private static final String EXTENSION_DISPLAY_NAME = "Set jenkins user build variables";


	@DataBoundConstructor
	public BuildUser() {
		//noop
	}

    public void setUp(Context context, Run<?,?> build, FilePath workspace,
        Launcher launcher, TaskListener listener, EnvVars initialEnvironment)
        throws IOException, InterruptedException
    {
        Map <String, String> variables = new HashMap<String,String>();
        makeUserBuildVariables(build, variables);
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            context.env(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Retrieve user cause that triggered this build and populate variables accordingly
	 *
	 * TODO: The whole hierarchy and way of applying could be refactored.
     */
    private void makeUserBuildVariables(@Nonnull Run build, @Nonnull Map<String, String> variables) {

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

		// Other causes should be checked after as build can be triggered automatically and later rerun manually by a human.
		// In that case there will be multiple causes and the direct manually one is preferred to set in a variable.
		handleOtherCausesOrLogWarningIfUnhandled(build, variables);
	}

	private void handleOtherCausesOrLogWarningIfUnhandled(@Nonnull Run build, @Nonnull Map<String, String> variables) {
		// set BUILD_USER_NAME and ID to fixed value if the build was triggered by a change in the scm, timer or remotly with token
		SCMTrigger.SCMTriggerCause scmTriggerCause = (SCMTrigger.SCMTriggerCause) build.getCause(SCMTrigger.SCMTriggerCause.class);
		if (new SCMTriggerCauseDeterminant().setJenkinsUserBuildVars(scmTriggerCause, variables)) {
			return;
		}

		TimerTrigger.TimerTriggerCause timerTriggerCause = (TimerTrigger.TimerTriggerCause) build.getCause(TimerTrigger.TimerTriggerCause.class);
		if (new TimerTriggerCauseDeterminant().setJenkinsUserBuildVars(timerTriggerCause, variables)) {
			return;
		}

		Cause.RemoteCause remoteTriggerCause = (Cause.RemoteCause) build.getCause(Cause.RemoteCause.class);
		if (new RemoteCauseDeterminant().setJenkinsUserBuildVars(remoteTriggerCause, variables)) {
			return;
		}

		log.warning(format("Unsupported cause type(s): %s", StringUtils.join(build.getCauses().iterator(), ", ")));
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

