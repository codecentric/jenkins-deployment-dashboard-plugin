package de.codecentric.jenkins.dashboard.impl.deploy;

/**
 * These variables are used when running the deploy job. The parameters are
 * passed to the deploy job and used to tag the server instances.
 * 
 * @author marcel.birkner
 * 
 */
public class DeployJobVariables {

    private String version;
    private String environment;

    @SuppressWarnings("unused")
    private DeployJobVariables() {
    };

    public DeployJobVariables(String version, String environment) {
        setVersion(version);
        setEnvironment(environment);
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
