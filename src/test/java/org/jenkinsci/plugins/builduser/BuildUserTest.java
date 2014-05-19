/*
 * The MIT License
 * 
 * Copyright (c) 2013 Oleg Nenashev
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.jenkinsci.plugins.builduser;

import hudson.model.AbstractProject;
import hudson.model.Build;
import hudson.model.Cause;
import hudson.model.CauseAction;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import hudson.tasks.BuildTrigger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.Assert;
import org.jvnet.hudson.test.Bug;
import org.jvnet.hudson.test.HudsonTestCase;
import org.jvnet.hudson.test.MockBuilder;

/**
 * Contains tests of {@link BuildUser}.
 * @author Oleg Nenashev <o.v.nenashev@gmail.com>
 */
public class BuildUserTest extends HudsonTestCase {
    
    
    @Bug(22974)
    public void testMakeUserBuildVariablesWithoutUpstream() throws Exception {      
        // Initialize
        FreeStyleProject childProject = createFreeStyleProject();
        List<AbstractProject> childProjects = new ArrayList<AbstractProject>(1);
        childProjects.add(childProject);
        Map<String, String> outputVars = new HashMap<String, String>();
        BuildUser buildUser = new BuildUser();
        
        // Create the parent job
        FreeStyleProject parentProject = createFreeStyleProject();
        parentProject.getBuildersList().add(new MockBuilder(Result.SUCCESS));
        parentProject.getPublishersList().add(new BuildTrigger(childProjects, Result.SUCCESS));
        parentProject.save();
        jenkins.rebuildDependencyGraph();
        
        // Trigger the first job. It should not trigger anything
        FreeStyleBuild upstreamBuild = this.buildAndAssertSuccess(parentProject);
        Thread.sleep(20000);
        Assert.assertEquals(1, childProject.getBuilds().size());
        
        // Register non-existent build as an execution cause
        Build downstreamBuild = childProject.getLastBuild();
        List<CauseAction> actions = downstreamBuild.getActions(CauseAction.class);
        Assert.assertTrue("CauseAction has not been created properly",
                actions != null && actions.size() == 1);
        Cause.UpstreamCause upstreamCause = null;
        List<Cause> causes = actions.get(0).getCauses();
        for (Cause cause : causes) {
            if (cause instanceof Cause.UpstreamCause) {
                upstreamCause = (Cause.UpstreamCause) cause;
            }
        }
        Assert.assertNotNull("Cannot extract the UpstreamCause", upstreamCause);
        buildUser.makeBuildVariables(downstreamBuild, outputVars); // Just a smoke check
        
        // Delete master build and check the correctness
        upstreamBuild.delete();
        try {
            buildUser.makeBuildVariables(downstreamBuild, outputVars);
        } catch (NullPointerException npe) {
            npe.printStackTrace();
            fail("MakeBuildVariables() has failed with NPE on non-existent upstream cause");            
        }
    }
    
}
