package org.jenkinsci.plugins.builduser.varsetter.impl;

import hudson.model.Cause.UserIdCause;
import hudson.model.User;
import org.jenkinsci.plugins.builduser.utils.BuildUserVariable;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class UserIdCauseDeterminantTest {
    public static final String TEST_USER = "testuser";
    
    @Rule
    public JenkinsRule r = new JenkinsRule();

    @Test
    public void testSetJenkinsUserBuildVars() {
        User.getById(TEST_USER, true);
        JenkinsRule.DummySecurityRealm realm = r.createDummySecurityRealm();
        r.jenkins.setSecurityRealm(realm);
        realm.addGroups(TEST_USER, "group1", "group2");
        Map<String, String> outputVars = new HashMap<>();
        UserIdCause cause = new UserIdCause(TEST_USER);
        UserIdCauseDeterminant determinant = new UserIdCauseDeterminant();
        determinant.setJenkinsUserBuildVars(cause, outputVars);
        assertThat(outputVars.get(BuildUserVariable.GROUPS), is(equalTo("authenticated,group1,group2")));
    }

    @Test
    public void testSetJenkinsUserBuildVarsInvalidUser() {
        JenkinsRule.DummySecurityRealm realm = r.createDummySecurityRealm();
        r.jenkins.setSecurityRealm(realm);
        Map<String, String> outputVars = new HashMap<>();
        UserIdCause cause = new UserIdCause(TEST_USER);
        UserIdCauseDeterminant determinant = new UserIdCauseDeterminant();
        determinant.setJenkinsUserBuildVars(cause, outputVars);
        assertThat(outputVars.get(BuildUserVariable.GROUPS), is(equalTo("authenticated")));
    }

    @Test
    public void testSetJenkinsUserBuildVarsNoGroups() {
        User.getById(TEST_USER, true);
        JenkinsRule.DummySecurityRealm realm = r.createDummySecurityRealm();
        r.jenkins.setSecurityRealm(realm);
        Map<String, String> outputVars = new HashMap<>();
        UserIdCause cause = new UserIdCause(TEST_USER);
        UserIdCauseDeterminant determinant = new UserIdCauseDeterminant();
        determinant.setJenkinsUserBuildVars(cause, outputVars);
        assertThat(outputVars.get(BuildUserVariable.GROUPS), is(equalTo("authenticated")));
    }

    @Test
    public void testSetJenkinsUserBuildVarsNoSecurityRealm() {
        User.getById(TEST_USER, true);
        Map<String, String> outputVars = new HashMap<>();
        UserIdCause cause = new UserIdCause(TEST_USER);
        UserIdCauseDeterminant determinant = new UserIdCauseDeterminant();
        determinant.setJenkinsUserBuildVars(cause, outputVars);
        assertThat(outputVars.get(BuildUserVariable.GROUPS), is(equalTo("")));
    }
}
