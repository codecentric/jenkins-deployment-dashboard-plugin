package de.codecentric.jenkins.dashboard.ec2;

import org.joda.time.DateTime;

/**
 *
 * @author Andreas Houben
 */
public class ServerInstance {
    private String version;
    private String environment;
    private String user;
    private DateTime timeStamp;

    public ServerInstance(String version, String environment, String user) {
    	this.version = version;
    	this.environment = environment;
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
