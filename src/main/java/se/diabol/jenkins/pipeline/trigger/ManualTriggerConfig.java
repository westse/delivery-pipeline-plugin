package se.diabol.jenkins.pipeline.trigger;

import hudson.Extension;
import hudson.model.*;
import hudson.plugins.parameterizedtrigger.AbstractBuildParameters;
import hudson.plugins.parameterizedtrigger.BuildTriggerConfig;
import hudson.plugins.parameterizedtrigger.ResultCondition;
import hudson.util.ListBoxModel;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import se.diabol.jenkins.pipeline.util.ProjectUtil;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Future;

public class ManualTriggerConfig extends BuildTriggerConfig {


    @DataBoundConstructor
    public ManualTriggerConfig(String projects, ResultCondition condition,
                              boolean triggerWithNoParameters, List<AbstractBuildParameters> configs) {
        super(projects, condition, triggerWithNoParameters, null, configs);
    }

    public AbstractProject getProject() {
        return ProjectUtil.getProject(getProjects());
    }

    protected Future schedule(AbstractBuild<?, ?> build, AbstractProject project, List<Action> list) throws InterruptedException, IOException {
        //TODO add who triggered manual step
        return project.scheduleBuild2(project.getQuietPeriod(),
                new Cause.UpstreamCause((Run) build),
                list.toArray(new Action[list.size()]));
    }


    @Extension
    public static class DescriptorImpl extends BuildTriggerConfig.DescriptorImpl {

        public ListBoxModel doFillProjectsItems(@AncestorInPath ItemGroup<?> context) {
            return ProjectUtil.fillAllProjects(context);
        }

    }

}
