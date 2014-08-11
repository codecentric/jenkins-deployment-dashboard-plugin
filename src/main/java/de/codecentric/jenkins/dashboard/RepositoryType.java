package de.codecentric.jenkins.dashboard;

public enum RepositoryType {
	
	NEXUS("nexus", "Sonatype Nexus"),
	ARTIFACTORY("artifactory", "jFrog Artifactory");
	
	private String id;
	private String description;

	private RepositoryType(String id, String description) {
		this.id = id;
		this.description = description;
	}
	
	public String getid() {
		return id;
	}
	
	public String getDescription() {
		return description;
	}
}