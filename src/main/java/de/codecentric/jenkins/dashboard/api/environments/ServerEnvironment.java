package de.codecentric.jenkins.dashboard.api.environments;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.amazonaws.services.ec2.model.InstanceState;

import de.codecentric.jenkins.dashboard.Messages;

/**
 * Detailed server environment information. These information are displayed on
 * the dashboard view.
 * 
 * @author marcel.birkner
 * 
 */
public class ServerEnvironment {

    private String instanceId;
    private String environmentTag;
    private String instanceType;
    private String version;

    private String publicIpAddress;
    private String urlPrefix;
    private String urlPostfix;

    private Date launchTime;
    private InstanceState state;
    private List<EnvironmentTag> tags;
    private ENVIRONMENT_TYPES type;

    public enum ENVIRONMENT_TYPES {
        TEST, PRODUCTION, STAGING, JENKINS
    }

    public ServerEnvironment(String instanceId, String instanceType) {
        this.instanceId = instanceId;
        this.instanceType = instanceType;
        this.type = ENVIRONMENT_TYPES.TEST;
    }

    public String getEnvironmentTag() {
        return environmentTag;
    }

    public void setEnvironmentTag(String environmentTag) {
        this.environmentTag = environmentTag;
    }

    public String getInstanceType() {
        return instanceType;
    }

    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<EnvironmentTag> getTags() {
        return tags;
    }

    public void setTags(List<EnvironmentTag> tags) {
        this.tags = tags;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public ENVIRONMENT_TYPES getType() {
        return type;
    }

    public void setType(ENVIRONMENT_TYPES type) {
        this.type = type;
    }

    public InstanceState getState() {
        return state;
    }

    public void setState(InstanceState state) {
        this.state = state;
    }

    public Date getLaunchTime() {
        return launchTime;
    }

    public void setLaunchTime(Date launchTime) {
        this.launchTime = launchTime;
    }

    public String getPublicIpAddress() {
        if (state.getName().equalsIgnoreCase("running")) {
            return publicIpAddress;
        }
        return Messages.ServerEnvironment_serverNotRunning();
    }

    public String getWebAppLink() {
        if (state.getName().equalsIgnoreCase("running")) {
            return urlPrefix + publicIpAddress + urlPostfix;
        }
        return Messages.ServerEnvironment_serverNotRunning();
    }

    public String displayWebAppLink() {
        if (state.getName().equalsIgnoreCase("running")) {
            return "true";
        }
        return "false";
    }

    public void setPublicIpAddress(String publicIpAddress) {
        this.publicIpAddress = publicIpAddress;
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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
