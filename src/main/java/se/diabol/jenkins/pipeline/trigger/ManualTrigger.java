/*
This file is part of Delivery Pipeline Plugin.

Delivery Pipeline Plugin is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Delivery Pipeline Plugin is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Delivery Pipeline Plugin.
If not, see <http://www.gnu.org/licenses/>.
*/
package se.diabol.jenkins.pipeline.trigger;

import hudson.Extension;
import hudson.model.*;
import hudson.plugins.parameterizedtrigger.BuildTrigger;
import hudson.plugins.parameterizedtrigger.ResultCondition;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import hudson.util.DescribableList;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.DataBoundConstructor;
import se.diabol.jenkins.pipeline.util.ProjectUtil;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class ManualTrigger extends BuildTrigger implements Trigger {

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

    public List<AbstractProject> getProjects() {
        List<AbstractProject> result = new ArrayList<AbstractProject>();
        if (triggerConfigs != null) {
            for (ManualTriggerConfig config : triggerConfigs) {
                result.addAll(config.getProjectList(Jenkins.getInstance(), null));
            }
        }
        return result;
    }

    public void trigger(AbstractBuild upstreamBuild, AbstractProject downstream) throws InterruptedException, IOException {
        for (ManualTriggerConfig manualTriggerConfig : triggerConfigs) {
            if (manualTriggerConfig.getProject().equals(downstream)) {
                manualTriggerConfig.perform(upstreamBuild, null, new StreamBuildListener(System.out, Charset.defaultCharset()));

            }
        }
    }

    @Override
    public void triggerManual(String projectName, String upstreamName, String buildId,
                              ItemGroup<? extends TopLevelItem> itemGroup) throws TriggerException {
        try {
            AbstractProject project = ProjectUtil.getProject(projectName, itemGroup);
            AbstractProject upstream = ProjectUtil.getProject(upstreamName, itemGroup);
            ManualTrigger trigger = ManualTrigger.getTrigger(upstream, project);
            AbstractBuild upstreamBuild = upstream.getBuildByNumber(Integer.parseInt(buildId));
            trigger.trigger(upstreamBuild, project);
        } catch (Exception e) {
            throw new TriggerException("Could not trigger", e);
        }

    }

    public List<ManualTriggerConfig> getTriggerConfigs() {
        return triggerConfigs;
    }


    public static ManualTrigger getTrigger(AbstractProject<?, ?> project, AbstractProject<?, ?> downstream) {
        DescribableList<Publisher, Descriptor<Publisher>> upstreamPublishersLists = project.getPublishersList();
        for (Publisher upstreamPub : upstreamPublishersLists) {
            if (upstreamPub instanceof ManualTrigger) {
                List<AbstractProject> downstreamProjects = ((ManualTrigger) upstreamPub).getProjects();
                for (AbstractProject downstreamProject : downstreamProjects) {
                    if (downstream.equals(downstreamProject)) {
                        return (ManualTrigger) upstreamPub;
                    }
                }

            }
        }
        return null;
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
