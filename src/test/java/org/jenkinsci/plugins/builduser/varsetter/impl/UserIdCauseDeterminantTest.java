package org.jenkinsci.plugins.builduser.varsetter.impl;

import hudson.model.Cause.UserIdCause;
import hudson.security.SecurityRealm;
import jenkins.model.Jenkins;
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.acegisecurity.userdetails.UsernameNotFoundException;
import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;
import java.util.Map;
import hudson.model.User;

import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

@RunWith(PowerMockRunner.class)
@PrepareForTest(UserIdCauseDeterminant.class)
@SuppressStaticInitializationFor({"jenkins.model.Jenkins", "hudson.model.User"})
public class UserIdCauseDeterminantTest {
    @Test
    public void testSetJenkinsUserBuildVars() {
        UserIdCause causeMock = EasyMock.createMock(UserIdCause.class);

        // Mock: cause.getUserName() (UserIdCauseDeterminant.java:46)
        EasyMock.expect(causeMock.getUserName()).andReturn("Test User");
        // Mock: cause.getUserId() (UserIdCauseDeterminant.java:49)
        EasyMock.expect(causeMock.getUserId()).andReturn("testuser");

        PowerMock.mockStatic(Jenkins.class);
        Jenkins jenkinsMock = EasyMock.createMock(Jenkins.class);
        SecurityRealm securityRealmMock = EasyMock.createMock(SecurityRealm.class);
        GrantedAuthority authorityMock = EasyMock.createMock(GrantedAuthority.class);
        GrantedAuthority[] grantedAuthorities = new GrantedAuthority[3];
        grantedAuthorities[0] = SecurityRealm.AUTHENTICATED_AUTHORITY;
        grantedAuthorities[1] = new GrantedAuthorityImpl("rolename1");
        grantedAuthorities[2] = authorityMock;
        org.acegisecurity.userdetails.User user =
                new org.acegisecurity.userdetails.User("testuser", "password123", true, grantedAuthorities);

        // Mock: Jenkins.getInstance().getSecurityRealm().loadUserByUsername(userid).getAuthorities();
        // UserIdCauseDeterminant.java:54
        EasyMock.expect(Jenkins.getInstanceOrNull()).andReturn(jenkinsMock);
        EasyMock.expect(jenkinsMock.getSecurityRealm()).andReturn(securityRealmMock);
        EasyMock.expect(securityRealmMock.loadUserByUsername("testuser")).andReturn(user);

        // Mock: authorities[i].getAuthority() (UserIdCauseDeterminant.java:56)
        EasyMock.expect(authorityMock.getAuthority()).andReturn(null);

        PowerMock.mockStatic(User.class);

        // Mock: User.get(userid) (UserIdCauseDeterminant.java:69)
        EasyMock.expect(User.get("testuser")).andReturn(null);

        EasyMock.replay(causeMock);
        EasyMock.replay(jenkinsMock);
        EasyMock.replay(securityRealmMock);
        replayAll();

        UserIdCauseDeterminant userIdCauseDeterminant = new UserIdCauseDeterminant();
        Map<String, String> outputVars = new HashMap<String, String>();
        userIdCauseDeterminant.setJenkinsUserBuildVars(causeMock, outputVars);

        assert(outputVars.get("BUILD_USER_GROUPS") == "authenticated,rolename1");
        verifyAll();
    }

    @Test
    public void testSetJenkinsUserBuildVarsInvalidUser() {
        UserIdCause causeMock = EasyMock.createMock(UserIdCause.class);

        // Mock: cause.getUserName() (UserIdCauseDeterminant.java:46)
        EasyMock.expect(causeMock.getUserName()).andReturn("Fake User");
        // Mock: cause.getUserId() (UserIdCauseDeterminant.java:49)
        EasyMock.expect(causeMock.getUserId()).andReturn("fakeUser");

        PowerMock.mockStatic(Jenkins.class);
        Jenkins jenkinsMock = EasyMock.createMock(Jenkins.class);
        SecurityRealm securityRealmMock = EasyMock.createMock(SecurityRealm.class);

        // Mock: Jenkins.getInstance().getSecurityRealm().loadUserByUsername(userid).getAuthorities();
        // UserIdCauseDeterminant.java:54
        EasyMock.expect(Jenkins.getInstanceOrNull()).andReturn(jenkinsMock);
        EasyMock.expect(jenkinsMock.getSecurityRealm()).andReturn(securityRealmMock);
        EasyMock.expect(securityRealmMock.loadUserByUsername("fakeUser")).andThrow(new UsernameNotFoundException("fakeUser"));

        PowerMock.mockStatic(User.class);

        // Mock: User.get(userid) (UserIdCauseDeterminant.java:69)
        EasyMock.expect(User.get("fakeUser")).andReturn(null);

        EasyMock.replay(causeMock);
        EasyMock.replay(jenkinsMock);
        EasyMock.replay(securityRealmMock);
        replayAll();

        UserIdCauseDeterminant userIdCauseDeterminant = new UserIdCauseDeterminant();
        Map<String, String> outputVars = new HashMap<String, String>();
        userIdCauseDeterminant.setJenkinsUserBuildVars(causeMock, outputVars);

        assert(outputVars.get("BUILD_USER_GROUPS") == "");
        verifyAll();
    }

    @Test
    public void testSetJenkinsUserBuildVarsNoGroups() {
        UserIdCause causeMock = EasyMock.createMock(UserIdCause.class);

        // Mock: cause.getUserName() (UserIdCauseDeterminant.java:46)
        EasyMock.expect(causeMock.getUserName()).andReturn("Test User");
        // Mock: cause.getUserId() (UserIdCauseDeterminant.java:49)
        EasyMock.expect(causeMock.getUserId()).andReturn("testuser");

        PowerMock.mockStatic(Jenkins.class);
        Jenkins jenkinsMock = EasyMock.createMock(Jenkins.class);
        SecurityRealm securityRealmMock = EasyMock.createMock(SecurityRealm.class);
        GrantedAuthority[] grantedAuthorities = new GrantedAuthority[0];
        org.acegisecurity.userdetails.User user =
                new org.acegisecurity.userdetails.User("testuser", "password123", true, grantedAuthorities);

        // Mock: Jenkins.getInstance().getSecurityRealm().loadUserByUsername(userid).getAuthorities();
        // UserIdCauseDeterminant.java:54
        EasyMock.expect(Jenkins.getInstanceOrNull()).andReturn(jenkinsMock);
        EasyMock.expect(jenkinsMock.getSecurityRealm()).andReturn(securityRealmMock);
        EasyMock.expect(securityRealmMock.loadUserByUsername("testuser")).andReturn(user);

        PowerMock.mockStatic(User.class);

        // Mock: User.get(userid) (UserIdCauseDeterminant.java:69)
        EasyMock.expect(User.get("testuser")).andReturn(null);

        EasyMock.replay(causeMock);
        EasyMock.replay(jenkinsMock);
        EasyMock.replay(securityRealmMock);
        replayAll();

        UserIdCauseDeterminant userIdCauseDeterminant = new UserIdCauseDeterminant();
        Map<String, String> outputVars = new HashMap<String, String>();
        userIdCauseDeterminant.setJenkinsUserBuildVars(causeMock, outputVars);

        assert(outputVars.get("BUILD_USER_GROUPS") == "");
        verifyAll();
    }
}
