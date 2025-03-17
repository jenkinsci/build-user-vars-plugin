package org.jenkinsci.plugins.builduser;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.htmlunit.html.HtmlCheckBoxInput;
import org.htmlunit.html.HtmlForm;

import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
class BuildUserVarsConfigTest {

    /**
     * Tries to exercise enough code paths to catch common mistakes:
     *
     * <ul>
     *   <li>missing {@code load}
     *   <li>missing {@code save}
     *   <li>misnamed or absent getter/setter
     *   <li>misnamed {@code textbox}
     * </ul>
     */
    @Test
    void uiAndStorage(JenkinsRule r) throws Throwable {
        assertFalse(BuildUserVarsConfig.get().isAllBuilds(), "not set initially");
        HtmlForm config = r.createWebClient().goTo("configure").getFormByName("config");
        HtmlCheckBoxInput checkbox = config.getInputByName("_.allBuilds");
        checkbox.setChecked(true);
        r.submit(config);
        assertTrue(
                BuildUserVarsConfig.get().isAllBuilds(),
                "global config page let us edit it");

        r.restart();

        assertTrue(
                BuildUserVarsConfig.get().isAllBuilds(),
                "still there after restart of Jenkins");
    }
}
