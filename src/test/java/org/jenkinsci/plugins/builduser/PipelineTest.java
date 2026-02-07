package org.jenkinsci.plugins.builduser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import hudson.model.Cause;
import hudson.model.CauseAction;
import hudson.model.Result;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
class PipelineTest {

    @Test
    void testPipelineWrapper(JenkinsRule jenkins) throws Exception {
        WorkflowJob job = jenkins.createProject(WorkflowJob.class, "testPipelineWrapper");
        String pipeline = """
                node {
                    withBuildUser {
                        sh 'echo BUILD_USER=${BUILD_USER}'
                    }
                }
                """;

        Cause.UserIdCause cause = new Cause.UserIdCause();
        job.setDefinition(new CpsFlowDefinition(pipeline, true));
        WorkflowRun run = job.scheduleBuild2(0, new CauseAction(cause)).get();
        jenkins.assertBuildStatusSuccess(run);

        String log = run.getLog();
        System.out.println(log);
        assertTrue(log.contains("BUILD_USER=SYSTEM"), log);
        assertEquals(Result.SUCCESS, run.getResult());
    }

    @Test
    void testDeclarativePipelineOption(JenkinsRule jenkins) throws Exception {
        WorkflowJob job = jenkins.createProject(WorkflowJob.class, "testDeclarativePipelineOption");
        String pipeline = """
                pipeline {
                    agent any
                    options {
                        withBuildUser()
                    }
                    stages {
                        stage('Display') {
                            steps {
                                sh 'echo BUILD_USER=${BUILD_USER}'
                            }
                        }
                    }

                }
                """;

        Cause.UserIdCause cause = new Cause.UserIdCause();
        job.setDefinition(new CpsFlowDefinition(pipeline, true));
        WorkflowRun run = job.scheduleBuild2(0, new CauseAction(cause)).get();
        jenkins.assertBuildStatusSuccess(run);

        String log = run.getLog();
        System.out.println(log);
        assertTrue(log.contains("BUILD_USER=SYSTEM"), log);
        assertEquals(Result.SUCCESS, run.getResult());
    }

    @Test
    void testDeclarativeStageOption(JenkinsRule jenkins) throws Exception {
        WorkflowJob job = jenkins.createProject(WorkflowJob.class, "testDeclarativeStageOption");
        String pipeline = """
                pipeline {
                    agent any
                    stages {
                        stage('Display') {
                            options {
                                withBuildUser()
                            }
                            steps {
                                sh 'echo BUILD_USER=${BUILD_USER}'
                            }
                        }
                    }

                }
                """;

        Cause.UserIdCause cause = new Cause.UserIdCause();
        job.setDefinition(new CpsFlowDefinition(pipeline, true));
        WorkflowRun run = job.scheduleBuild2(0, new CauseAction(cause)).get();
        jenkins.assertBuildStatusSuccess(run);

        String log = run.getLog();
        System.out.println(log);
        assertTrue(log.contains("BUILD_USER=SYSTEM"), log);
        assertEquals(Result.SUCCESS, run.getResult());
    }
}
