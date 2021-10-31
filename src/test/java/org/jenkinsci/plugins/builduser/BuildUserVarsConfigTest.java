package org.jenkinsci.plugins.builduser;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlForm;

import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsSessionRule;

public class BuildUserVarsConfigTest {

    @Rule public JenkinsSessionRule sessions = new JenkinsSessionRule();

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
    public void uiAndStorage() throws Throwable {
        sessions.then(
                r -> {
                    assertFalse("not set initially", BuildUserVarsConfig.get().isAllBuilds());
                    HtmlForm config = r.createWebClient().goTo("configure").getFormByName("config");
                    HtmlCheckBoxInput checkbox = config.getInputByName("_.allBuilds");
                    checkbox.setChecked(true);
                    r.submit(config);
                    assertTrue(
                            "global config page let us edit it",
                            BuildUserVarsConfig.get().isAllBuilds());
                });
        sessions.then(
                r -> assertTrue(
                        "still there after restart of Jenkins",
                        BuildUserVarsConfig.get().isAllBuilds()));
    }
}
