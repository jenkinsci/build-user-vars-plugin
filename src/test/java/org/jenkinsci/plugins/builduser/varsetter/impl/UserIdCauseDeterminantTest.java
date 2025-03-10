package org.jenkinsci.plugins.builduser.varsetter.impl;

import hudson.model.Cause.UserIdCause;
import hudson.model.User;
import hudson.tasks.Mailer.UserProperty;
import org.jenkinsci.plugins.builduser.utils.BuildUserVariable;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@WithJenkins
class UserIdCauseDeterminantTest {
    private static final String TEST_USER = "test_user";

    @Test
    void testSetJenkinsUserBuildVars(JenkinsRule r) {
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
    void testSetJenkinsUserBuildVarsInvalidUser(JenkinsRule r) {
        JenkinsRule.DummySecurityRealm realm = r.createDummySecurityRealm();
        r.jenkins.setSecurityRealm(realm);
        Map<String, String> outputVars = new HashMap<>();
        UserIdCause cause = new UserIdCause(TEST_USER);
        UserIdCauseDeterminant determinant = new UserIdCauseDeterminant();
        determinant.setJenkinsUserBuildVars(cause, outputVars);
        assertThat(outputVars.get(BuildUserVariable.GROUPS), is(equalTo("")));
    }

    @Test
    void testSetJenkinsUserBuildVarsNoGroups(JenkinsRule r) {
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
    void testSetJenkinsUserBuildVarsNoSecurityRealm(JenkinsRule r) {
        User.getById(TEST_USER, true);
        Map<String, String> outputVars = new HashMap<>();
        UserIdCause cause = new UserIdCause(TEST_USER);
        UserIdCauseDeterminant determinant = new UserIdCauseDeterminant();
        determinant.setJenkinsUserBuildVars(cause, outputVars);
        assertThat(outputVars.get(BuildUserVariable.GROUPS), is(equalTo(null)));
    }

    @Test
    void testSetJenkinsUserBuildVarsEmail(JenkinsRule r) throws IOException {
        User user = User.getById(TEST_USER, true);
        user.addProperty(new UserProperty("testuser@example.com"));
        JenkinsRule.DummySecurityRealm realm = r.createDummySecurityRealm();
        r.jenkins.setSecurityRealm(realm);
        Map<String, String> outputVars = new HashMap<>();
        UserIdCause cause = new UserIdCause(TEST_USER);
        UserIdCauseDeterminant determinant = new UserIdCauseDeterminant();
        determinant.setJenkinsUserBuildVars(cause, outputVars);
        assertThat(outputVars.get(BuildUserVariable.EMAIL), is(equalTo("testuser@example.com")));
    }

    @Test
    void testSetJenkinsUserBuildVarsId(JenkinsRule r) {
        User.getById(TEST_USER, true);
        JenkinsRule.DummySecurityRealm realm = r.createDummySecurityRealm();
        r.jenkins.setSecurityRealm(realm);
        Map<String, String> outputVars = new HashMap<>();
        UserIdCause cause = new UserIdCause(TEST_USER);
        UserIdCauseDeterminant determinant = new UserIdCauseDeterminant();
        determinant.setJenkinsUserBuildVars(cause, outputVars);
        assertThat(outputVars.get(BuildUserVariable.ID), is(equalTo(TEST_USER)));
    }

    @Test
    void testSetJenkinsUserBuildVarsKeepsExistingVariables(JenkinsRule r) {
        User.getById(TEST_USER, true);
        JenkinsRule.DummySecurityRealm realm = r.createDummySecurityRealm();
        r.jenkins.setSecurityRealm(realm);

        Map<String, String> outputVars = new HashMap<>();
        outputVars.put("EXISTING_VAR", "existing_value");

        UserIdCause cause = new UserIdCause(TEST_USER);
        UserIdCauseDeterminant determinant = new UserIdCauseDeterminant();

        determinant.setJenkinsUserBuildVars(cause, outputVars);

        assertThat(outputVars.get("EXISTING_VAR"), is(equalTo("existing_value")));
    }

    @Test
    void testSetJenkinsUserBuildVarsNoEmail(JenkinsRule r) throws IOException {
        User user = User.getById(TEST_USER, true);
        user.addProperty(new UserProperty(null));
        JenkinsRule.DummySecurityRealm realm = r.createDummySecurityRealm();
        r.jenkins.setSecurityRealm(realm);

        Map<String, String> outputVars = new HashMap<>();
        UserIdCause cause = new UserIdCause(TEST_USER);
        UserIdCauseDeterminant determinant = new UserIdCauseDeterminant();

        determinant.setJenkinsUserBuildVars(cause, outputVars);

        assertThat(outputVars.get(BuildUserVariable.EMAIL), is(equalTo(null)));
    }
}
