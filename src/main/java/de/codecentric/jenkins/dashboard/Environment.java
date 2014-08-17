package de.codecentric.jenkins.dashboard;

import java.util.Collection;
import java.util.List;


import de.codecentric.jenkins.dashboard.api.environment.ServerEnvironment;
import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.model.View;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;


import org.kohsuke.stapler.DataBoundConstructor;

public class Environment extends AbstractDescribableImpl<Environment> {

    @Extension
    public static final EnvironmentDescriptor DESCRIPTOR = new EnvironmentDescriptor();

    private String name;
    private EnvironmentType environmentType;
    private String awsInstance;


//            - tag of the environment (i suggest we search for the instanceId via the tag)
//    - type (production / test makes sense)

    @DataBoundConstructor
    public Environment(final String name, final String environmentType, final String awsInstance) {
        setName(name);
        setEnvironmentType(environmentType);
        setAwsInstance(awsInstance);
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getEnvironmentType() {
        return environmentType.name();
    }

    public void setEnvironmentType(final String environmentType) {
        this.environmentType = EnvironmentType.valueOf(environmentType);
    }

    public String getAwsInstance() {
        return awsInstance;
    }

    public void setAwsInstance(final String awsInstance) {
        this.awsInstance = awsInstance;
    }

    @Override
    public Descriptor getDescriptor() {
        return DESCRIPTOR;
    }

    public static class EnvironmentDescriptor extends Descriptor<Environment> {
        public String getDisplayName() {
            return Messages.Environment_DisplayName();
        }

        public ListBoxModel doFillEnvironmentTypeItems() {
            ListBoxModel model = new ListBoxModel();

            for (EnvironmentType value : EnvironmentType.values()) {
                model.add(value.getDescription(), value.name());
            }

            return model;
        }

        public ListBoxModel doFillAwsInstanceItems() {
            final ListBoxModel model = new ListBoxModel();
            final Collection<View> views = Jenkins.getInstance().getViews();
            for (View view : views) {
                if (view instanceof DashboardView) {
                    final List<ServerEnvironment> allEC2Environments = ((DashboardView) view).getAllEC2Environments();

                    for (ServerEnvironment env : allEC2Environments) {
                        model.add(env.getEnvironmentTag());
                    }
                }
            }

            return model;
        }

    }
}
