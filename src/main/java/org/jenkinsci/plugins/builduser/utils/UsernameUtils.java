package org.jenkinsci.plugins.builduser.utils;

import java.util.Map;

/**
 * Utility class for splitting full username to parts.
 * 
 * @author GKonovalenko
 */
public final class UsernameUtils {

	private UsernameUtils() {
	}

	/**
	 * Splits username string to first & last names and sets appropriate build variables.
	 *
	 * @param username  string with username, usually something like "Chuck Norris"
	 * @param variables result map, where to put build variables.
	 */
	public static void setUsernameVars(String username, Map<String, String> variables) {
		variables.put(BuildUserVariable.USERNAME, username);
		variables.put(BuildUserVariable.FIRST_NAME, getFirstName(username));
		variables.put(BuildUserVariable.LAST_NAME, getLastName(username));
	}

	/**
	 * Extracts the first name from the full name.
	 *
	 * @param fullName string like "Chuck Norris"
	 * @return first name ("Chuck")
	 */
	public static String getFirstName(String fullName) {
		if (fullName == null || fullName.trim().isEmpty()) {
			return "";
		}
		String[] parts = splitName(fullName);
		return parts[0];
	}

	/**
	 * Extracts the last name from the full name.
	 *
	 * @param fullName full name -- string like "Chuck Norris"
	 * @return last name ("Norris")
	 */
	public static String getLastName(String fullName) {
		if (fullName == null || fullName.trim().isEmpty()) {
			return "";
		}
		String[] parts = splitName(fullName);
		return parts.length >= 2 ? parts[1] : "";
	}

	/**
	 * Splits the full name into parts.
	 *
	 * @param fullName full name -- string like "Chuck Norris"
	 * @return array containing first and last name
	 */
	private static String[] splitName(String fullName) {
		return fullName.trim().split("\\s+");
	}
}
