package de.codecentric.jenkins.dashboard;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;

import org.kohsuke.stapler.DataBoundConstructor;

public class Environment extends AbstractDescribableImpl<Environment> {

    @Extension
    public static final EnvironmentDescriptor DESCRIPTOR = new EnvironmentDescriptor();

    private String name;

    @DataBoundConstructor
    public Environment(final String name) {
        setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public Descriptor getDescriptor() {
        return DESCRIPTOR;
    }

    public static class EnvironmentDescriptor extends Descriptor<Environment> {
        public String getDisplayName() {
            return Messages.Environment_displayName();
        }
    }
}