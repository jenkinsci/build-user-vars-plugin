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

	static final Class<UserCause> causeClass = UserCause.class;
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>{@link UserCause}</b> based implementation.
	 */
	public boolean setJenkinsUserBuildVars(UserCause cause,
			Map<String, String> variables) {
		if(null != cause) {
			String username = cause.getUserName();
			UsernameUtils.setUsernameVars(username, variables);
			
			return true;
		} else {
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Class<UserCause> getUsedCauseClass() {
		return causeClass;
	}

}
