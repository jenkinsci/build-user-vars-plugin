package org.jenkinsci.plugins.builduser.varsetter.impl;

import hudson.model.Cause.UserIdCause;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.builduser.utils.UsernameUtils;
import org.jenkinsci.plugins.builduser.varsetter.IUsernameSettable;

/**
 * This implementation is used to determine build username variables from <b>{@link UserIdCause}</b>.
 * This will let to get whole set of variables:
 * <ul>
 *   <li>{@link IUsernameSettable#BUILD_USER_ID}</li>
 *   <li>{@link IUsernameSettable#BUILD_USER_VAR_NAME}</li>
 *   <li>{@link IUsernameSettable#BUILD_USER_FIRST_NAME_VAR_NAME}</li>
 *   <li>{@link IUsernameSettable#BUILD_USER_LAST_NAME_VAR_NAME}</li>
 * </ul>
 * 
 * @author GKonovalenko
 */
public class UserIdCauseDeterminant implements IUsernameSettable<UserIdCause> {
	
	final Class<UserIdCause> causeClass = UserIdCause.class;

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>{@link UserIdCause}</b> based implementation.
	 */
	public boolean setJenkinsUserBuildVars(UserIdCause cause,
			Map<String, String> variables) {
		if(null != cause) {
			String username = cause.getUserName();
			UsernameUtils.setUsernameVars(username, variables);
			
			String userid= StringUtils.trimToEmpty(cause.getUserId());
			variables.put(BUILD_USER_ID, userid);
			
			return true;
		} else {
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Class<UserIdCause> getUsedCauseClass() {
		return causeClass;
	}

}
