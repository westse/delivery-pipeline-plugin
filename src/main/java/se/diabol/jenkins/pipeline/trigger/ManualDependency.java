package se.diabol.jenkins.pipeline.trigger;

import hudson.model.*;

import java.util.List;

public class ManualDependency extends DependencyGraph.Dependency {

    public ManualDependency(AbstractProject upstream, AbstractProject downstream) {
        super(upstream, downstream);
    }

    @Override
    public boolean shouldTriggerBuild(AbstractBuild build, TaskListener listener, List<Action> actions) {
        return false;
    }
}
