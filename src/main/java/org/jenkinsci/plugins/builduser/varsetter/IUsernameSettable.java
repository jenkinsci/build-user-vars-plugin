package org.jenkinsci.plugins.builduser.varsetter;

import hudson.model.Cause;

import java.util.Map;

/**
 * Interface declaring method for setting jenkins user build variables parametrized by
 * {@link Cause} subclasses.
 * <p>
 * User based {@link Cause} instance is the source of username data. 
 * 
 * <ul>
 *	<li><b>BUILD_USER</b> -- full name of user started build,</li>
 *	<li><b>BUILD_USER_FIRST_NAME</b> -- first name of user started build,</li>
 * 	<li><b>BUILD_USER_LAST_NAME</b> -- last name of user started build,</li>
 * 	<li><b>BUILD_USER_ID</b> -- id of user started build.</li>
 * </ul>
 * 
 * @author GKonovalenko
 */
public interface IUsernameSettable<T extends Cause> {
	
	/** Full name of user started build */
	public static final String BUILD_USER_VAR_NAME = "BUILD_USER";
	/** First name of user started build */
	public static final String BUILD_USER_FIRST_NAME_VAR_NAME = "BUILD_USER_FIRST_NAME";
	/** Last name of user started build */
	public static final String BUILD_USER_LAST_NAME_VAR_NAME = "BUILD_USER_LAST_NAME";
    /** Email of user started build */
    public static final String BUILD_USER_EMAIL = "BUILD_USER_EMAIL";
	/** Id of user started build */
	public static final String BUILD_USER_ID = "BUILD_USER_ID";
	/** Optional value for variable which value couldn't be defined. */
	public static final String UNDEFINED = "UNDEFINED";
	
	/**
	 * Adds username build variables extracted from build cause to map of build variables.
	 * 
	 * @param cause
	 * 		cause where to get username from.
	 * @param variables
	 * 		map of build variables, where to add username variables.
	 * @return
	 * 		<code>true</code> if username was determined and added to the passed map,
	 *      <code>false</code> otherwise.
	 */
	boolean setJenkinsUserBuildVars(T cause, Map<String, String> variables);
	
	/**
	 * Returns {@link Cause} subclass used to determine user name.
	 * @return
	 *     class used to determine user name.
	 */
	Class<T> getUsedCauseClass();
	
}
