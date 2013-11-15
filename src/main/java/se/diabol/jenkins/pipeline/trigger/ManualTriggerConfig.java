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
