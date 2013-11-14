package se.diabol.jenkins.pipeline.trigger;

import hudson.model.CauseAction;
import hudson.model.FreeStyleProject;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.util.List;

import static org.junit.Assert.*;

public class ManualTriggerTest {

    @Rule
    public JenkinsRule jenkins = new JenkinsRule();


    @Test
    public void test1() throws Exception {
        FreeStyleProject a = jenkins.createFreeStyleProject("a");
        FreeStyleProject b = jenkins.createFreeStyleProject("b");
        ManualTrigger trigger = new ManualTrigger("b");
        a.getPublishersList().add(trigger);
        jenkins.getInstance().rebuildDependencyGraph();

        jenkins.buildAndAssertSuccess(a);
        jenkins.waitUntilNoActivity();
        assertNotNull(a.getLastBuild());
        assertNull(b.getLastBuild());

        trigger.trigger(a.getLastBuild(), b);
        jenkins.waitUntilNoActivity();

        assertNotNull(b.getLastBuild());



        List<CauseAction> causeActions =  b.getLastBuild().getActions(CauseAction.class);



    }

}
