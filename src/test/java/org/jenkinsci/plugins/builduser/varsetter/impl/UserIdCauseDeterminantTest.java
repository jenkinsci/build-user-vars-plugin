package org.jenkinsci.plugins.builduser.varsetter.impl;

import hudson.model.*;
import hudson.model.Cause.UserIdCause;

import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.HashMap;
import java.util.Map;

public class UserIdCauseDeterminantTest {

    @Rule
    public JenkinsRule r = new JenkinsRule();

    @Test
    public void testSetJenkinsUserBuildVars() {
        User.getById("testuser", true);
        JenkinsRule.DummySecurityRealm realm = r.createDummySecurityRealm();
        r.jenkins.setSecurityRealm(realm);
        realm.addGroups("testuser", "group1", "group2");
        Map<String, String> outputVars = new HashMap<>();
        UserIdCause cause = new UserIdCause("testuser");
        UserIdCauseDeterminant determinant = new UserIdCauseDeterminant();
        determinant.setJenkinsUserBuildVars(cause, outputVars);
        System.out.println(outputVars);
        assertThat(outputVars.get("BUILD_USER_GROUPS"), is(equalTo("authenticated,group1,group2")));
    }

    @Test
    public void testSetJenkinsUserBuildVarsInvalidUser() {
        JenkinsRule.DummySecurityRealm realm = r.createDummySecurityRealm();
        r.jenkins.setSecurityRealm(realm);
        Map<String, String> outputVars = new HashMap<>();
        UserIdCause cause = new UserIdCause("testuser");
        UserIdCauseDeterminant determinant = new UserIdCauseDeterminant();
        determinant.setJenkinsUserBuildVars(cause, outputVars);
        System.out.println(outputVars);
        // 'anonymous' user gets authenticated group automatically
        assertThat(outputVars.get("BUILD_USER_GROUPS"), is(equalTo("authenticated")));
    }

    @Test
    public void testSetJenkinsUserBuildVarsNoGroups() {
        User.getById("testuser", true);
        JenkinsRule.DummySecurityRealm realm = r.createDummySecurityRealm();
        r.jenkins.setSecurityRealm(realm);
        Map<String, String> outputVars = new HashMap<>();
        UserIdCause cause = new UserIdCause("testuser");
        UserIdCauseDeterminant determinant = new UserIdCauseDeterminant();
        determinant.setJenkinsUserBuildVars(cause, outputVars);
        System.out.println(outputVars);
        // User still gets authenticated group automatically
        assertThat(outputVars.get("BUILD_USER_GROUPS"), is(equalTo("authenticated")));
    }

    @Test
    public void testSetJenkinsUserBuildVarsNoSecurityRealm() {
        User.getById("testuser", true);
        Map<String, String> outputVars = new HashMap<>();
        UserIdCause cause = new UserIdCause("testuser");
        UserIdCauseDeterminant determinant = new UserIdCauseDeterminant();
        determinant.setJenkinsUserBuildVars(cause, outputVars);
        System.out.println(outputVars);
        assertThat(outputVars.get("BUILD_USER_GROUPS"), is(equalTo("")));
    }
}
