package se.diabol.jenkins.pipeline.model;

import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

@ExportedBean(defaultVisibility = 100)
public class ManualStep {
    private String upstreamProject;
    private String upstreamId;
    private boolean enabled;

    public ManualStep(String upstreamProject, String upstreamId, boolean enabled) {
        this.upstreamProject = upstreamProject;
        this.upstreamId = upstreamId;
        this.enabled = enabled;
    }

    @Exported
    public String getUpstreamProject() {
        return upstreamProject;
    }

    @Exported
    public String getUpstreamId() {
        return upstreamId;
    }

    @Exported
    public boolean isEnabled() {
        return enabled;
    }
}
