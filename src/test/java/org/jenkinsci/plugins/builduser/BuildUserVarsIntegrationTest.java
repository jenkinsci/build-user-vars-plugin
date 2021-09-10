package org.jenkinsci.plugins.builduser;

import static org.junit.Assert.assertEquals;

import hudson.EnvVars;
import hudson.model.Cause;
import hudson.model.CauseAction;
import hudson.model.FreeStyleProject;
import hudson.model.User;
import hudson.tasks.Mailer;

import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.CaptureEnvironmentBuilder;
import org.jvnet.hudson.test.JenkinsRule;

public class BuildUserVarsIntegrationTest {

    @Rule public JenkinsRule r = new JenkinsRule();

    @Test
    public void smokes() throws Exception {
        User user = User.getById("bob", true);
        user.setFullName("Bob Smith");
        user.addProperty(new Mailer.UserProperty("bob@example.com"));
        r.jenkins.setSecurityRealm(r.createDummySecurityRealm());

        FreeStyleProject p = r.createFreeStyleProject();
        p.getBuildWrappersList().add(new BuildUser());
        CaptureEnvironmentBuilder captureEnvironment = new CaptureEnvironmentBuilder();
        p.getBuildersList().add(captureEnvironment);
        r.assertBuildStatusSuccess(
                p.scheduleBuild2(0, new CauseAction(new Cause.UserIdCause(user.getId()))));

        EnvVars envVars = captureEnvironment.getEnvVars();
        assertEquals("Bob Smith", envVars.get("BUILD_USER"));
        assertEquals("authenticated", envVars.get("BUILD_USER_GROUPS"));
        assertEquals("Bob", envVars.get("BUILD_USER_FIRST_NAME"));
        assertEquals("Smith", envVars.get("BUILD_USER_LAST_NAME"));
        assertEquals("bob@example.com", envVars.get("BUILD_USER_EMAIL"));
        assertEquals("bob", envVars.get("BUILD_USER_ID"));
    }

    @Test
    public void allBuilds() throws Exception {
        BuildUserVarsConfig config = BuildUserVarsConfig.get();
        config.setAllBuilds(true);
        config.save();

        User user = User.getById("bob", true);
        user.setFullName("Bob Smith");
        user.addProperty(new Mailer.UserProperty("bob@example.com"));
        r.jenkins.setSecurityRealm(r.createDummySecurityRealm());

        FreeStyleProject p = r.createFreeStyleProject();
        CaptureEnvironmentBuilder captureEnvironment = new CaptureEnvironmentBuilder();
        p.getBuildersList().add(captureEnvironment);
        r.assertBuildStatusSuccess(
                p.scheduleBuild2(0, new CauseAction(new Cause.UserIdCause(user.getId()))));

        EnvVars envVars = captureEnvironment.getEnvVars();
        assertEquals("Bob Smith", envVars.get("BUILD_USER"));
        assertEquals("authenticated", envVars.get("BUILD_USER_GROUPS"));
        assertEquals("Bob", envVars.get("BUILD_USER_FIRST_NAME"));
        assertEquals("Smith", envVars.get("BUILD_USER_LAST_NAME"));
        assertEquals("bob@example.com", envVars.get("BUILD_USER_EMAIL"));
        assertEquals("bob", envVars.get("BUILD_USER_ID"));
    }
}
