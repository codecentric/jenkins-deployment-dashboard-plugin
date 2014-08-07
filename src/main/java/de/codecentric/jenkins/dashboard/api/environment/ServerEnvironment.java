package de.codecentric.jenkins.dashboard.api.environment;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

public class ServerEnvironment {

	private String instanceId;
	private String environmentTag;
	private String instanceType;
	private String version;
	private List<Tag> tags;
	private ENVIRONMENT_TYPES type;

	public enum ENVIRONMENT_TYPES {
		TEST, PRODUCTION, STAGING, JENKINS
	}
	
	public ServerEnvironment(String instanceId, String instanceType) {
		this.instanceId = instanceId;
		this.instanceType = instanceType;
		this.setType(ENVIRONMENT_TYPES.TEST);
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
	public List<Tag> getTags() {
		return tags;
	}
	public void setTags(List<Tag> tags) {
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
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}
