package org.jenkinsci.plugins.builduser;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Cause.UserCause;
import hudson.model.Cause.UserIdCause;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;

import java.io.IOException;
import java.util.Map;

import org.jenkinsci.plugins.builduser.utils.ClassUtils;
import org.jenkinsci.plugins.builduser.varsetter.IUsernameSettable;
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
	private static final String USER_ID_CAUSE_CLASS_NAME = "hudson.model.Cause$UserIdCause";


	@DataBoundConstructor
	public BuildUser() {
		//noop
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Environment setUp(AbstractBuild build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
		/* noop */
		return new Environment() {
		};
	}


	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void makeBuildVariables(AbstractBuild build,
			Map<String, String> variables) {

		/* Use UserIdCause.class if it exists in the system (should be starting from b1.427 of jenkins). */
		if(ClassUtils.isClassExists(USER_ID_CAUSE_CLASS_NAME)){

			/* Try to use UserIdCause to get & set jenkins user build variables */
			UserIdCause userIdCause = (UserIdCause) build.getCause(UserIdCause.class);
			if(new UserIdCauseDeterminant().setJenkinsUserBuildVars(userIdCause, variables)) {
				return;
			}
		}

		/* Try to use deprecated UserCause to get & set jenkins user build variables */
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

