package org.jenkinsci.plugins.builduser.varsetter.impl;

import hudson.model.*;
import hudson.model.Cause.UserIdCause;

import hudson.security.ChainedServletFilter;
import hudson.security.SecurityRealm;
import jenkins.model.IdStrategy;
import org.acegisecurity.GrantedAuthority;
import org.easymock.EasyMock;
import org.jenkinsci.plugins.saml.SamlSecurityRealm;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import javax.servlet.FilterConfig;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.easymock.EasyMock.anyObject;

public class UserIdCauseDeterminantSamlTest {

    @Rule
    public JenkinsRule r = new JenkinsRule();

    public Map<String, String> runSamlSecurityRealmTest(JenkinsRule r, String userid, String caseConversion) {
        GrantedAuthority[] grantedAuthorities = new GrantedAuthority[0];
        org.acegisecurity.userdetails.User user =
                new org.acegisecurity.userdetails.User(
                        userid,
                        "password123",
                        true,
                        grantedAuthorities);
        SamlSecurityRealm realm = EasyMock.mock(SamlSecurityRealm.class);

        IdStrategy strategy = new IdStrategy.CaseSensitive();
        EasyMock.expect(realm.getUserIdStrategy()).andReturn(strategy).anyTimes();
        EasyMock.expect(realm.getSecurityComponents()).andReturn(new SecurityRealm.SecurityComponents());
        EasyMock.expect(realm.createFilter(anyObject(FilterConfig.class))).andReturn(new ChainedServletFilter());
        EasyMock.expect(realm.getUsernameCaseConversion()).andReturn(caseConversion);
        EasyMock.expect(realm.loadUserByUsername(userid)).andReturn(user).anyTimes();

        EasyMock.replay(realm);

        User.getById(userid, true);
        r.jenkins.setSecurityRealm(realm);
        Map<String, String> outputVars = new HashMap<>();
        UserIdCause cause = new UserIdCause(userid);
        UserIdCauseDeterminant determinant = new UserIdCauseDeterminant();
        determinant.setJenkinsUserBuildVars(cause, outputVars);
        return outputVars;
    }

    @Test
    public void testSetJenkinsUserBuildVarsSamlUpperCase() {
        Map<String, String> outputVars = runSamlSecurityRealmTest(r, "Testuser", "uppercase");
        assertThat(outputVars.get("BUILD_USER_ID"), is(equalTo("TESTUSER")));
    }

    @Test
    public void testSetJenkinsUserBuildVarsSamlLowerCase() {
        Map<String, String> outputVars = runSamlSecurityRealmTest(r, "Testuser", "lowercase");
        assertThat(outputVars.get("BUILD_USER_ID"), is(equalTo("testuser")));
    }

    @Test
    public void testSetJenkinsUserBuildVarsSamlNoCase() {
        Map<String, String> outputVars = runSamlSecurityRealmTest(r, "Testuser", "none");
        assertThat(outputVars.get("BUILD_USER_ID"), is(equalTo("Testuser")));
    }
}
