package org.jenkinsci.plugins.builduser;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jenkinsci.plugins.builduser.varsetter.IUsernameSettable;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import hudson.model.Run;

/**
 * Get and return build user variables, see {@link IUsernameSettable}:
 *
 * @author awitt
 * @see IUsernameSettable
 */
public class BuildUserStep extends Step {

    @DataBoundConstructor
    public BuildUserStep() {
    }

    @Override
    public StepExecution start(StepContext context) {
        return new Execution(context);
    }

    public static class Execution extends StepExecution {

        private static final long serialVersionUID = 1L;

        public Execution(StepContext context) {
            super(context);
        }

        @Override
        public boolean start() throws Exception {
            Run<?, ?> build = getContext().get(Run.class);
            Map<String, String> variables = new HashMap<>();

            BuildUser.makeUserBuildVariables(build, variables);
            getContext().onSuccess(variables);

            return true;
        }
    }

    @Extension
    public static class DescriptorImpl extends StepDescriptor {

        @Override
        public String getFunctionName() {
            return "getBuildUser";
        }

        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return new HashSet<>(Collections.singletonList(
                    Run.class // for the build get the user information from
            ));
        }

        @Override
        public String getDisplayName() {
            return "Get information about the user that started this build.";
        }
    }
}
