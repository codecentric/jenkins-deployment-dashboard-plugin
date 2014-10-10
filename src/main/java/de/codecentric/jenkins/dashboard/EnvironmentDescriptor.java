package de.codecentric.jenkins.dashboard;

import hudson.model.Descriptor;
import hudson.util.ComboBoxModel;
import hudson.util.ListBoxModel;

import java.util.List;

import jenkins.model.Jenkins;
import de.codecentric.jenkins.dashboard.api.environments.ServerEnvironment;
import de.codecentric.jenkins.dashboard.impl.environments.EnvironmentType;

/**
 * Descriptor for the Environment. This descriptor object contains the metadata about the
 * Environment configuration.
 */
public final class EnvironmentDescriptor extends Descriptor<Environment> {

    public EnvironmentDescriptor() {
	super(Environment.class); // Have to provide the original class because there is no
				  // enclosing class
	load();
    }

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
	final DashboardViewDescriptor descriptor = (DashboardViewDescriptor) Jenkins.getInstance().getDescriptor(DashboardView.class);
	final List<ServerEnvironment> allEC2Environments = descriptor.getAllEC2Environments();

	for (ServerEnvironment env : allEC2Environments) {
	    model.add(env.getEnvironmentTag());
	}

	return model;
    }

    public ComboBoxModel doFillBuildJobItems() {
	ComboBoxModel model = new ComboBoxModel();

	for (String jobName : Jenkins.getInstance().getJobNames()) {
	    model.add(jobName);
	}

	return model;
    }

}