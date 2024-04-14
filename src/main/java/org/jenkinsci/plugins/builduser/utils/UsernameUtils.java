package org.jenkinsci.plugins.builduser.utils;

import org.apache.commons.lang.StringUtils;

import java.util.Map;

/**
 * Utility class for splitting full username to parts.
 * 
 * @author GKonovalenko
 */
public final class UsernameUtils {
	
	private UsernameUtils(){
	}
	
	/**
	 * Splits username string to first &amp; last names and sets appropriate build variables.
	 * @param username string with username, usually smth. like "Chuck Norris"
	 * @param variables result map, where to put build variables.
	 */
	public static void setUsernameVars(String username, Map<String, String> variables) {
		variables.put(BuildUserVariable.USERNAME, username);
		variables.put(BuildUserVariable.FIRST_NAME, getFirstName(username));
		variables.put(BuildUserVariable.LAST_NAME, getLastName(username));
	}
	
	/**
	 * Cuts first name (first word) out from the passed string.
	 * @param fullName full name -- string like "Chuck Norris"
	 * @return first name ("Chuck")
	 */
	public static String getFirstName(String fullName) {
		String [] parts = StringUtils.trimToEmpty(fullName).split("\\s+");
		return parts.length > 0 ? parts[0] : "";
	}

	/**
	 * Cuts last name (second word) out from the passed string.
	 * @param fullName full name -- string like "Chuck Norris"
	 * @return last name ("Norris")
	 */
	public static String getLastName(String fullName) {
		String [] parts = StringUtils.trimToEmpty(fullName).split("\\s+");
		return parts.length >= 2 ? parts[1] : "";
	}
}
