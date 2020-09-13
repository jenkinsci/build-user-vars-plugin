package org.jenkinsci.plugins.builduser;

import hudson.model.Cause;
import hudson.model.CauseAction;
import hudson.model.Label;
import hudson.model.ParametersAction;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

/**
 * Contains tests for {@link BuildUserStep}.
 *
 * @author Fabio Silva <fabiodcasilva@gmail.com>
 */
public class BuildUserStepTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();
    private WorkflowJob pipeline;
    private WorkflowRun run;

    @Before
    public void setup() throws Exception {
        j.createOnlineSlave(Label.get("slaves"));
        pipeline = j.jenkins.createProject(WorkflowJob.class, "pipeline");
    }

    @Test
    public void canGetBuildUserVariables() throws Exception {
        pipeline.setDefinition(
                new CpsFlowDefinition("getBuildUser().BUILD_USER\n", true)
        );
        run = j.assertBuildStatusSuccess(
                pipeline.scheduleBuild2(0,
                        new CauseAction(new Cause.UserIdCause()),
                        new ParametersAction()));
        j.assertLogContains("Started by user", run);
    }

    @Test
    public void canGetBuildUserVariablesWithAgent() throws Exception {
        pipeline.setDefinition(
                new CpsFlowDefinition(
                "node('slaves') {\n" +
                        "  def userId = getBuildUser().BUILD_USER_ID\n" +
                        "  echo 'Hello ' + userId\n" +
                        "}", true
                )
        );
        run = j.assertBuildStatusSuccess(
                pipeline.scheduleBuild2(0,
                        new CauseAction(new Cause.UserIdCause("user")),
                        new ParametersAction()));
        j.assertLogContains("Hello user", run);
    }
}