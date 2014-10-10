package de.codecentric.jenkins.dashboard;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.ComboBoxModel;
import hudson.util.ListBoxModel;

import java.util.List;

import jenkins.model.Jenkins;

import org.kohsuke.stapler.DataBoundConstructor;

import de.codecentric.jenkins.dashboard.api.environments.ServerEnvironment;
import de.codecentric.jenkins.dashboard.impl.environments.EnvironmentType;

public class Environment extends AbstractDescribableImpl<Environment> {

    @Extension
    public static final EnvironmentDescriptor DESCRIPTOR = new EnvironmentDescriptor();

    private String name;
    private EnvironmentType environmentType;
    private String buildJob;
    private String awsInstance;

    @DataBoundConstructor
    public Environment(final String name, final String environmentType, final String awsInstance, final String buildJob) {
	setName(name);
	setEnvironmentType(environmentType);
	setAwsInstance(awsInstance);
	setBuildJob(buildJob);
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

    public String getBuildJob() {
	return buildJob;
    }

    public void setBuildJob(final String buildJob) {
	this.buildJob = buildJob;
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
}
