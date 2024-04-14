package org.jenkinsci.plugins.builduser.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * 
 * @author GKonovalenko
 */
public class UsernameUtilsTest {

	String name1 = "First Second";
	String name2 = "First               Second";
	String name3 = "First\tSecond";
	String name4 = "First\t\tSecond";
	String name5 = "  First Second  ";
	String name6 = "\t \tFirst\t \tSecond \t  ";
	String name7 = "";
	String name8 = " First ";
	String name9 = null;

	@Test
	public final void testGetFirstName() {
		assertEquals("First", UsernameUtils.getFirstName(name1));
		assertEquals("First", UsernameUtils.getFirstName(name2));
		assertEquals("First", UsernameUtils.getFirstName(name3));
		assertEquals("First", UsernameUtils.getFirstName(name4));
		assertEquals("First", UsernameUtils.getFirstName(name5));
		assertEquals("First", UsernameUtils.getFirstName(name6));
		assertEquals("", UsernameUtils.getFirstName(name7));
		assertEquals("First", UsernameUtils.getFirstName(name8));
		assertEquals("", UsernameUtils.getFirstName(name9));
	}

	@Test
	public final void testGetLastName() {
		assertEquals("Second", UsernameUtils.getLastName(name1));
		assertEquals("Second", UsernameUtils.getLastName(name2));
		assertEquals("Second", UsernameUtils.getLastName(name3));
		assertEquals("Second", UsernameUtils.getLastName(name4));
		assertEquals("Second", UsernameUtils.getLastName(name5));
		assertEquals("Second", UsernameUtils.getLastName(name6));
		assertEquals("", UsernameUtils.getLastName(name7));
		assertEquals("", UsernameUtils.getLastName(name8));
		assertEquals("", UsernameUtils.getLastName(name9));
	}
}
