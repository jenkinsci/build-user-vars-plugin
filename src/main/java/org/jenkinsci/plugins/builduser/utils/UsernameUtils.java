package org.jenkinsci.plugins.builduser.utils;

import java.util.Map;

/**
 * Utility class for manipulating and extracting parts from a full username.
 * This class provides methods to split a full username into first and last names.
 * 
 * @author GKonovalenko
 */
public final class UsernameUtils {

	private UsernameUtils() {
	}

	/**
	 * Splits a full username string into first and last names and sets the appropriate build variables.
	 *
	 * @param username  The full username string, usually in the format "First Last"
	 * @param variables A map to store the extracted variables, where to put build variables.
	 */
	public static void setUsernameVars(String username, Map<String, String> variables) {
		variables.put(BuildUserVariable.USERNAME, username);
		variables.put(BuildUserVariable.FIRST_NAME, getFirstName(username));
		variables.put(BuildUserVariable.LAST_NAME, getLastName(username));
	}

	/**
	 * Extracts the first name from a full name.
	 *
	 * @param fullName The full name string, e.g., "First Last"
	 * @return The first name ("First")
	 */
	public static String getFirstName(String fullName) {
		if (fullName == null || fullName.trim().isEmpty()) {
			return "";
		}
		String[] parts = splitName(fullName);
		return parts[0];
	}

	/**
	 * Extracts the last name from a full name.
	 *
	 * @param fullName The full name string, e.g., "First Last"
	 * @return The last name ("Last")
	 */
	public static String getLastName(String fullName) {
		if (fullName == null || fullName.trim().isEmpty()) {
			return "";
		}
		String[] parts = splitName(fullName);
		return parts.length >= 2 ? parts[1] : "";
	}

	/**
	 * Splits a full name into its constituent parts.
	 *
	 * @param fullName The full name string, e.g., "First Last"
	 * @return An array containing the first and last name
	 */
	private static String[] splitName(String fullName) {
		return fullName.trim().split("\\s+");
	}
}
