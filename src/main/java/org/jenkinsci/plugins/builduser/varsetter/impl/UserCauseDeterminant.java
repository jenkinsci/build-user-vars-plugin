/**
 * 
 */
package org.jenkinsci.plugins.builduser.varsetter.impl;

import hudson.model.Cause.UserCause;
import hudson.model.Cause.UserIdCause;

import java.util.Map;

import org.jenkinsci.plugins.builduser.utils.UsernameUtils;
import org.jenkinsci.plugins.builduser.varsetter.IUsernameSettable;

/**
 * This implementation is used to determine build username variables from  <b>{@link UserCause}</b>.
 * This could be used with legacy version of jenkins, where {@link UserCause} is used instead of
 * {@link UserIdCause} (before b1.427).
 * This will let to get following set of variables:
 * <ul>
 *   <li>{@link IUsernameSettable#BUILD_USER_VAR_NAME}</li>
 *   <li>{@link IUsernameSettable#BUILD_USER_FIRST_NAME_VAR_NAME}</li>
 *   <li>{@link IUsernameSettable#BUILD_USER_LAST_NAME_VAR_NAME}</li>
 * </ul>
 * 
 * @author GKonovalenko
 */
@SuppressWarnings("deprecation")
public class UserCauseDeterminant implements IUsernameSettable<UserCause> {
	
	final Class<UserCause> causeClass = UserCause.class;
	
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
