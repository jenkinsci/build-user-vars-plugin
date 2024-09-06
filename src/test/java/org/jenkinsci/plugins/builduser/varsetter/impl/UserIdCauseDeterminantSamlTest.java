package org.jenkinsci.plugins.builduser.varsetter.impl;

import hudson.model.Cause.UserIdCause;
import hudson.security.ChainedServletFilter2;
import hudson.security.SecurityRealm;
import jenkins.model.IdStrategy;
import org.easymock.EasyMock;
import org.jenkinsci.plugins.builduser.utils.BuildUserVariable;
import org.jenkinsci.plugins.saml.SamlSecurityRealm;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import jakarta.servlet.FilterConfig;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.easymock.EasyMock.anyObject;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class UserIdCauseDeterminantSamlTest {
    public static final String TEST_USER = "Testuser";

    @Rule
    public JenkinsRule r = new JenkinsRule();

    public Map<String, String> runSamlSecurityRealmTest(JenkinsRule r, String userid, String caseConversion) {
        ArrayList<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        User user = new User(userid, "password123", true, true, true, true, grantedAuthorities);
        SamlSecurityRealm realm = EasyMock.mock(SamlSecurityRealm.class);

        IdStrategy strategy = new IdStrategy.CaseSensitive();
        EasyMock.expect(realm.getUserIdStrategy()).andReturn(strategy).anyTimes();
        EasyMock.expect(realm.getSecurityComponents()).andReturn(new SecurityRealm.SecurityComponents());
        EasyMock.expect(realm.createFilter(anyObject(FilterConfig.class))).andReturn(new ChainedServletFilter2());
        EasyMock.expect(realm.getUsernameCaseConversion()).andReturn(caseConversion);
        EasyMock.expect(realm.loadUserByUsername2(userid)).andReturn(user).anyTimes();

        EasyMock.replay(realm);

        hudson.model.User.getById(userid, true);
        r.jenkins.setSecurityRealm(realm);
        Map<String, String> outputVars = new HashMap<>();
        UserIdCause cause = new UserIdCause(userid);
        UserIdCauseDeterminant determinant = new UserIdCauseDeterminant();
        determinant.setJenkinsUserBuildVars(cause, outputVars);
        return outputVars;
    }

    @Test
    public void testSetJenkinsUserBuildVarsSamlUpperCase() {
        Map<String, String> outputVars = runSamlSecurityRealmTest(r, TEST_USER, "uppercase");
        assertThat(outputVars.get(BuildUserVariable.ID), is(equalTo("TESTUSER")));
    }

    @Test
    public void testSetJenkinsUserBuildVarsSamlLowerCase() {
        Map<String, String> outputVars = runSamlSecurityRealmTest(r, TEST_USER, "lowercase");
        assertThat(outputVars.get(BuildUserVariable.ID), is(equalTo("testuser")));
    }

    @Test
    public void testSetJenkinsUserBuildVarsSamlNoCase() {
        Map<String, String> outputVars = runSamlSecurityRealmTest(r, TEST_USER, "none");
        assertThat(outputVars.get(BuildUserVariable.ID), is(equalTo(TEST_USER)));
    }
}
