package org.jenkinsci.plugins.builduser;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import java.io.Serial;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.jenkinsci.plugins.workflow.steps.AbstractStepExecutionImpl;
import org.jenkinsci.plugins.workflow.steps.BodyExecutionCallback;
import org.jenkinsci.plugins.workflow.steps.EnvironmentExpander;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;

public class BuildUserStep extends Step {

    @DataBoundConstructor
    public BuildUserStep() {}

    @Override
    public StepExecution start(StepContext context) throws Exception {
        return new ExecutionImpl(context);
    }

    /**
     * Execution for {@link BuildUserStep}.
     */
    private static class ExecutionImpl extends AbstractStepExecutionImpl {

        @Serial
        private static final long serialVersionUID = 1L;

        ExecutionImpl(StepContext context) {
            super(context);
        }

        @Override
        public boolean start() throws Exception {
            StepContext context = getContext();

            EnvironmentExpander currentEnvironment = context.get(EnvironmentExpander.class);

            // Make environment variables
            Run<?, ?> build = context.get(Run.class);
            Map<String, String> variables = new HashMap<>();
            BuildUser.makeUserBuildVariables(build, variables);

            EnvironmentExpander userEnvironment = EnvironmentExpander.constant(variables);
            context.newBodyInvoker()
                    .withContext(EnvironmentExpander.merge(currentEnvironment, userEnvironment))
                    .withCallback(BodyExecutionCallback.wrap(context))
                    .start();
            return false;
        }
    }

    /**
     * Descriptor for {@link BuildUserStep}.
     */
    @Extension(optional = true)
    public static class StepDescriptorImpl extends StepDescriptor {

        @NonNull
        @Override
        public String getDisplayName() {
            return BuildUser.EXTENSION_DISPLAY_NAME;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getFunctionName() {
            return "withBuildUser";
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean takesImplicitBlockArgument() {
            return true;
        }

        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return Collections.singleton(TaskListener.class);
        }
    }
}
