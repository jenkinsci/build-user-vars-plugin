package org.jenkinsci.plugins.builduser.varsetter.impl;

import hudson.model.Cause.UserCause;
import hudson.model.Cause.UserIdCause;
import org.jenkinsci.plugins.builduser.utils.UsernameUtils;
import org.jenkinsci.plugins.builduser.varsetter.IUsernameSettable;

import java.util.Map;

/**
 * This implementation is used to determine build username variables from  <b>{@link UserCause}</b>.
 * This could be used with legacy version of jenkins, where {@link UserCause} is used instead of {@link UserIdCause}.
 * 
 * @author GKonovalenko
 */
@SuppressWarnings("deprecation")
public class UserCauseDeterminant implements IUsernameSettable<UserCause> {

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>{@link UserCause}</b> based implementation.
	 */
	public boolean setJenkinsUserBuildVars(UserCause cause, Map<String, String> variables) {
        if (cause == null) {
            return false;
        }

        String username = cause.getUserName();
        UsernameUtils.setUsernameVars(username, variables);

        return true;
    }

	/**
	 * {@inheritDoc}
	 */
	public Class<UserCause> getUsedCauseClass() {
		return UserCause.class;
	}
}
