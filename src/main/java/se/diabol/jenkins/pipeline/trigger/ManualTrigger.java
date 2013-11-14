package se.diabol.jenkins.pipeline.trigger;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.DependencyGraph;
import hudson.model.StreamBuildListener;
import hudson.plugins.parameterizedtrigger.BuildTrigger;
import hudson.plugins.parameterizedtrigger.ResultCondition;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class ManualTrigger extends BuildTrigger {

    private List<ManualTriggerConfig> triggerConfigs;

    @DataBoundConstructor
    public ManualTrigger(List<ManualTriggerConfig> triggerConfigs) {
        this.triggerConfigs = triggerConfigs;
    }

    public ManualTrigger(String project) {
        ManualTriggerConfig config = new ManualTriggerConfig(project, ResultCondition.SUCCESS, true, null);
        triggerConfigs = new ArrayList<ManualTriggerConfig>();
        triggerConfigs.add(config);
    }

    public void trigger(AbstractBuild upstreamBuild, AbstractProject downstream) {
        try {
            for (ManualTriggerConfig manualTriggerConfig : triggerConfigs) {
                if (manualTriggerConfig.getProject().equals(downstream)) {
                    manualTriggerConfig.perform(upstreamBuild, null, new StreamBuildListener(System.out, Charset.defaultCharset()));
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    public List<ManualTriggerConfig> getTriggerConfigs() {
        return triggerConfigs;
    }

    @Override
    public void buildDependencyGraph(AbstractProject owner, DependencyGraph graph) {
        for (ManualTriggerConfig config : triggerConfigs)
            for (AbstractProject project : config.getProjectList(owner.getParent(), null))
                graph.addDependency(new ManualDependency(owner, project));
    }

    @Extension
    public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {
        @Override
        public String getDisplayName() {
            return "Delivery pipeline Manual Approval";
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

    }


}
