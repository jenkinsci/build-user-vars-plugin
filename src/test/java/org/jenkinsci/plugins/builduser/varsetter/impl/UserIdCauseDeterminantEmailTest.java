package org.jenkinsci.plugins.builduser.varsetter.impl;

import hudson.model.Cause.UserIdCause;
import hudson.model.User;
import hudson.tasks.MailAddressResolver;
import hudson.tasks.Mailer.UserProperty;
import org.jenkinsci.plugins.builduser.utils.BuildUserVariable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.TestExtension;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@WithJenkins
class UserIdCauseDeterminantEmailTest {

    private static final String TEST_USER = "test_user";
    
    private JenkinsRule r;

    @BeforeEach
    void beforeEach(JenkinsRule rule) {
        r = rule;
    }

    @Test
    void testSetJenkinsUserBuildVarsEmailWithResolver() throws Exception {
        User user = User.getById(TEST_USER, true);
        assertNotNull(user);
        user.addProperty(new UserProperty(null));

        JenkinsRule.DummySecurityRealm realm = r.createDummySecurityRealm();
        r.jenkins.setSecurityRealm(realm);

        Map<String, String> outputVars = new HashMap<>();
        UserIdCause cause = new UserIdCause(TEST_USER);
        UserIdCauseDeterminant determinant = new UserIdCauseDeterminant();
        determinant.setJenkinsUserBuildVars(cause, outputVars);

        assertThat(outputVars.get(BuildUserVariable.EMAIL), is(equalTo("resolveduser@example.com")));
    }

    @SuppressWarnings("unused")
    @TestExtension
    public static class TestMailAddressResolver extends MailAddressResolver {

        @Override
        public String findMailAddressFor(User user) {
            return "resolveduser@example.com";
        }
    }
}
