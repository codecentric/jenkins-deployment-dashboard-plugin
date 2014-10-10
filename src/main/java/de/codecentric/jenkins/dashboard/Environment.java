package de.codecentric.jenkins.dashboard;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;

import org.kohsuke.stapler.DataBoundConstructor;

import de.codecentric.jenkins.dashboard.impl.environments.EnvironmentType;

public class Environment extends AbstractDescribableImpl<Environment> {

    @Extension
    public static final EnvironmentDescriptor DESCRIPTOR = new EnvironmentDescriptor();

    private String name;
    private String urlPrefix;
    private String urlPostfix;
    private EnvironmentType environmentType;
    private String awsInstance;
    private String buildJob;

    @DataBoundConstructor
    public Environment(final String name, final String environmentType, final String urlPrefix, final String urlPostfix, final String awsInstance,
	    final String buildJob) {
	setName(name);
	setEnvironmentType(environmentType);
	setAwsInstance(awsInstance);
	setBuildJob(buildJob);
	setUrlPostfix(urlPostfix);
	setUrlPrefix(urlPrefix);
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

    public String getUrlPrefix() {
	return urlPrefix;
    }

    public void setUrlPrefix(String urlPrefix) {
	this.urlPrefix = urlPrefix;
    }

    public String getUrlPostfix() {
	return urlPostfix;
    }

    public void setUrlPostfix(String urlPostfix) {
	this.urlPostfix = urlPostfix;
    }

}
