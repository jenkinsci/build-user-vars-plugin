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
import org.jvnet.hudson.test.RestartableJenkinsRule;

import javax.servlet.FilterConfig;
import java.util.HashMap;
import java.util.Map;

import static org.easymock.EasyMock.anyObject;

public class UserIdCauseDeterminantTest {

    @Rule
    public RestartableJenkinsRule rr = new RestartableJenkinsRule();

    @Test
    public void testSetJenkinsUserBuildVars() throws Exception {
        rr.then(r-> {
            User.getById("testuser", true);
            JenkinsRule.DummySecurityRealm realm = r.createDummySecurityRealm();
            r.jenkins.setSecurityRealm(realm);
            realm.addGroups("testuser", "group1", "group2");
            Map<String, String> outputVars = new HashMap<String, String>();
            UserIdCause cause = new UserIdCause("testuser");
            UserIdCauseDeterminant determinant = new UserIdCauseDeterminant();
            determinant.setJenkinsUserBuildVars(cause, outputVars);
            System.out.println(outputVars);
            assert(outputVars.get("BUILD_USER_GROUPS").equals("authenticated,group1,group2"));
        });
    }

    @Test
    public void testSetJenkinsUserBuildVarsInvalidUser() {
        rr.then(r-> {
            JenkinsRule.DummySecurityRealm realm = r.createDummySecurityRealm();
            r.jenkins.setSecurityRealm(realm);
            Map<String, String> outputVars = new HashMap<String, String>();
            UserIdCause cause = new UserIdCause("testuser");
            UserIdCauseDeterminant determinant = new UserIdCauseDeterminant();
            determinant.setJenkinsUserBuildVars(cause, outputVars);
            System.out.println(outputVars);
            // 'anonymous' user gets authenticated group automatically
            assert(outputVars.get("BUILD_USER_GROUPS").equals("authenticated"));
        });
    }

    @Test
    public void testSetJenkinsUserBuildVarsNoGroups() {
        rr.then(r-> {
            User.getById("testuser", true);
            JenkinsRule.DummySecurityRealm realm = r.createDummySecurityRealm();
            r.jenkins.setSecurityRealm(realm);
            Map<String, String> outputVars = new HashMap<String, String>();
            UserIdCause cause = new UserIdCause("testuser");
            UserIdCauseDeterminant determinant = new UserIdCauseDeterminant();
            determinant.setJenkinsUserBuildVars(cause, outputVars);
            System.out.println(outputVars);
            // User still gets authenticated group automatically
            assert(outputVars.get("BUILD_USER_GROUPS").equals("authenticated"));
        });
    }

    @Test
    public void testSetJenkinsUserBuildVarsNoSecurityRealm() throws Exception {
        rr.then(r-> {
            User.getById("testuser", true);
            Map<String, String> outputVars = new HashMap<String, String>();
            UserIdCause cause = new UserIdCause("testuser");
            UserIdCauseDeterminant determinant = new UserIdCauseDeterminant();
            determinant.setJenkinsUserBuildVars(cause, outputVars);
            System.out.println(outputVars);
            assert(outputVars.get("BUILD_USER_GROUPS").equals(""));
        });
    }

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
        Map<String, String> outputVars = new HashMap<String, String>();
        UserIdCause cause = new UserIdCause(userid);
        UserIdCauseDeterminant determinant = new UserIdCauseDeterminant();
        determinant.setJenkinsUserBuildVars(cause, outputVars);
        return outputVars;
    }

    @Test
    public void testSetJenkinsUserBuildVarsSamlUpperCase() {
        rr.then(r-> {
            Map<String, String> outputVars = runSamlSecurityRealmTest(r, "Testuser", "uppercase");
            assert(outputVars.get("BUILD_USER_ID").equals("TESTUSER"));
        });
    }

    @Test
    public void testSetJenkinsUserBuildVarsSamlLowerCase() {
        rr.then(r-> {
            Map<String, String> outputVars = runSamlSecurityRealmTest(r, "Testuser", "lowercase");
            assert(outputVars.get("BUILD_USER_ID").equals("testuser"));
        });
    }

    @Test
    public void testSetJenkinsUserBuildVarsSamlNoCase() {
        rr.then(r-> {
            Map<String, String> outputVars = runSamlSecurityRealmTest(r, "Testuser", "none");
            assert(outputVars.get("BUILD_USER_ID").equals("Testuser"));
        });
    }
}
