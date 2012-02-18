package org.jenkinsci.plugins.builduser.utils;


/**
 * Utility class for dynamic check if class exists in the system.
 * 
 * @author GKonovalenko
 */
public final class ClassUtils {

	private ClassUtils() {
	}

	/**
	 * Checks if class exists on classpath.
	 * @param name
	 * 				name of class to check.
	 * @return
	 * 				<code>true</code> if class exists, <code>false</code> otherwise.
	 */
	public static boolean isClassExists(String name) {
		try {
			Class.forName(name);
			return true;
		} catch(ClassNotFoundException e) {
			return false;
		}
	}
}
