package de.codecentric.jenkins.dashboard.persistence;

import org.joda.time.DateTime;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import de.codecentric.jenkins.dashboard.impl.deploy.DeployJobVariables;

/**
 * @author Andreas Houben
 */
@XStreamAlias("instance")
public class ServerInstance {

    @XStreamAlias("version")
    private String version;

    @XStreamAlias("environment")
    private String environment;

    @XStreamAlias("user")
    private String user;

    @XStreamAlias("timestamp")
    private DateTime timeStamp;

    public ServerInstance(DeployJobVariables deployJobVariables, String user) {
        this.version = deployJobVariables.getVersion();
        this.environment = deployJobVariables.getEnvironment();
        this.user = user;
        this.timeStamp = new DateTime();
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

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public DateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(DateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

}