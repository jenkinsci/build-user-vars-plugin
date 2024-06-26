package org.jenkinsci.plugins.builduser.utils;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * 
 * @author GKonovalenko
 */
public class UsernameUtilsTest {

	@Test
	public void testGetFirstName() {
		assertEquals("First", UsernameUtils.getFirstName("First Second"));
		assertEquals("First", UsernameUtils.getFirstName("First               Second"));
		assertEquals("First", UsernameUtils.getFirstName("First\tSecond"));
		assertEquals("First", UsernameUtils.getFirstName("First\t\tSecond"));
		assertEquals("First", UsernameUtils.getFirstName("  First Second  "));
		assertEquals("First", UsernameUtils.getFirstName("\t \tFirst\t \tSecond \t  "));
		assertEquals("", UsernameUtils.getFirstName(""));
		assertEquals("First", UsernameUtils.getFirstName(" First "));
		assertEquals("", UsernameUtils.getFirstName(null));
	}

	@Test
	public void testGetLastName() {
		assertEquals("Second", UsernameUtils.getLastName("First Second"));
		assertEquals("Second", UsernameUtils.getLastName("First               Second"));
		assertEquals("Second", UsernameUtils.getLastName("First\tSecond"));
		assertEquals("Second", UsernameUtils.getLastName("First\t\tSecond"));
		assertEquals("Second", UsernameUtils.getLastName("  First Second  "));
		assertEquals("Second", UsernameUtils.getLastName("\t \tFirst\t \tSecond \t  "));
		assertEquals("", UsernameUtils.getLastName(""));
		assertEquals("", UsernameUtils.getLastName(" First "));
		assertEquals("", UsernameUtils.getLastName(null));
	}

	@Test
	public void testSetUsernameVars() {
		Map<String, String> variables = new HashMap<>();
		UsernameUtils.setUsernameVars("John Doe", variables);
		assertEquals("John Doe", variables.get(BuildUserVariable.USERNAME));
		assertEquals("John", variables.get(BuildUserVariable.FIRST_NAME));
		assertEquals("Doe", variables.get(BuildUserVariable.LAST_NAME));
	}
}
