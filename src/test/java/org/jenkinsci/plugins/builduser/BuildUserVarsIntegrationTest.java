package org.jenkinsci.plugins.builduser;

import hudson.EnvVars;
import hudson.model.Cause;
import hudson.model.CauseAction;
import hudson.model.FreeStyleProject;
import hudson.model.User;
import hudson.tasks.Mailer;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.CaptureEnvironmentBuilder;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

import static org.junit.jupiter.api.Assertions.assertEquals;

@WithJenkins
class BuildUserVarsIntegrationTest {
    private static final String TEST_USER_NAME = "Bob Smith";
    private static final String TEST_USER_EMAIL = "bob@example.com";

    @Test
    void smokes(JenkinsRule r) throws Exception {
        User user = User.getById("bob", true);
        user.setFullName(TEST_USER_NAME);
        user.addProperty(new Mailer.UserProperty(TEST_USER_EMAIL));
        r.jenkins.setSecurityRealm(r.createDummySecurityRealm());

        FreeStyleProject p = r.createFreeStyleProject();
        p.getBuildWrappersList().add(new BuildUser());
        CaptureEnvironmentBuilder captureEnvironment = new CaptureEnvironmentBuilder();
        p.getBuildersList().add(captureEnvironment);
        r.assertBuildStatusSuccess(
                p.scheduleBuild2(0, new CauseAction(new Cause.UserIdCause(user.getId()))));

        EnvVars envVars = captureEnvironment.getEnvVars();
        assertEquals(TEST_USER_NAME, envVars.get("BUILD_USER"));
        assertEquals("authenticated", envVars.get("BUILD_USER_GROUPS"));
        assertEquals("Bob", envVars.get("BUILD_USER_FIRST_NAME"));
        assertEquals("Smith", envVars.get("BUILD_USER_LAST_NAME"));
        assertEquals(TEST_USER_EMAIL, envVars.get("BUILD_USER_EMAIL"));
        assertEquals("bob", envVars.get("BUILD_USER_ID"));
    }

    @Test
    void allBuilds(JenkinsRule r) throws Exception {
        BuildUserVarsConfig config = BuildUserVarsConfig.get();
        config.setAllBuilds(true);
        config.save();

        User user = User.getById("bob", true);
        user.setFullName(TEST_USER_NAME);
        user.addProperty(new Mailer.UserProperty(TEST_USER_EMAIL));
        r.jenkins.setSecurityRealm(r.createDummySecurityRealm());

        FreeStyleProject p = r.createFreeStyleProject();
        CaptureEnvironmentBuilder captureEnvironment = new CaptureEnvironmentBuilder();
        p.getBuildersList().add(captureEnvironment);
        r.assertBuildStatusSuccess(
                p.scheduleBuild2(0, new CauseAction(new Cause.UserIdCause(user.getId()))));

        EnvVars envVars = captureEnvironment.getEnvVars();
        assertEquals(TEST_USER_NAME, envVars.get("BUILD_USER"));
        assertEquals("authenticated", envVars.get("BUILD_USER_GROUPS"));
        assertEquals("Bob", envVars.get("BUILD_USER_FIRST_NAME"));
        assertEquals("Smith", envVars.get("BUILD_USER_LAST_NAME"));
        assertEquals(TEST_USER_EMAIL, envVars.get("BUILD_USER_EMAIL"));
        assertEquals("bob", envVars.get("BUILD_USER_ID"));
    }
}
