package de.codecentric.jenkins.dashboard.api.repository;

public class Artifact {
	
	private String name;
	private String version;
	private String uri;
	
	public Artifact(String name, String version, String uri) {
		this.name = name;
		this.version = version;
		this.uri = uri;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}

}