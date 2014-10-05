package de.codecentric.jenkins.dashboard;

/**
 * Specifies all supported Repository Types. Currently we support:
 * 
 * - Nexus
 * - Artifactory
 *
 */
public enum RepositoryType {
	
	NEXUS(Messages.RepositoryType_nexus(), Messages.RepositoryType_nexusFullName()),
	ARTIFACTORY(Messages.RepositoryType_artifactory(), Messages.RepositoryType_artifactoryFullName());
	
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