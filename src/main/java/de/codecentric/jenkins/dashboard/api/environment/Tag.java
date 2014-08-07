package de.codecentric.jenkins.dashboard.api.environment;

import org.apache.commons.lang.builder.ToStringBuilder;

public class Tag {
	
	private String key;
	private String value;

	public Tag(String key, String value) {
		this.key = key;
		this.value = value;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}
