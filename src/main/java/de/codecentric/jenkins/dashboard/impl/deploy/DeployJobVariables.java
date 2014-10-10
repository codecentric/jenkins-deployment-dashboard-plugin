package de.codecentric.jenkins.dashboard.impl.deploy;

public class DeployJobVariables {

    private String version;
    private String environment;

    @SuppressWarnings("unused")
    private DeployJobVariables() {
    };

    public DeployJobVariables(String version, String environment) {
	this.version = version;
	this.environment = environment;
    }

    public String getVersion() {
	return version;
    }

    public void setVersion(String version) {
	this.version = version;
    }

    public String getEnvironment() {
	return environment;
    }

    public void setEnvironment(String environment) {
	this.environment = environment;
    }

}
