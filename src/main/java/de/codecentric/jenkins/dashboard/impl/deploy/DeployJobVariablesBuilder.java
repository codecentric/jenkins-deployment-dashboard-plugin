package de.codecentric.jenkins.dashboard.impl.deploy;

public class DeployJobVariablesBuilder {

    private String version;
    private String environment;

    public DeployJobVariablesBuilder() {
    };

    public DeployJobVariablesBuilder version(String version) {
	this.version = version;
	return this;
    }

    public DeployJobVariablesBuilder environment(String environment) {
	this.environment = environment;
	return this;
    }

    public DeployJobVariables build() {
	return new DeployJobVariables(version, environment);
    }

}
