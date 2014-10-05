package de.codecentric.jenkins.dashboard.artifactrepositories.nexus;

/**
 * Nexus artifact repository model. Used for JSON un-/marshalling.
 * 
 */
@javax.xml.bind.annotation.XmlType(name = "nexusArtifact")
@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
public class NexusArtifact implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * The group id of the artifact.
	 */
	private String groupId;

	/**
	 * The artifact id of the artifact.
	 */
	private String artifactId;

	/**
	 * The version of the artifact.
	 */
	private String version;

	/**
	 * The latest snapshot version of the artifact.
	 */
	private String latestSnapshot;

	/**
	 * The repository of latest snapshot version of the artifact.
	 */
	private String latestSnapshotRepositoryId;

	/**
	 * The latest release version of the artifact.
	 */
	private String latestRelease;

	/**
	 * The repository of latest release version of the artifact.
	 */
	private String latestReleaseRepositoryId;

	/**
	 * A HTML highlighted fragment of the matched hit.
	 */
	private String highlightedFragment;

	@javax.xml.bind.annotation.XmlElement(name = "artifactHits")
	private java.util.List<NexusArtifactHit> artifactHits;

	public java.util.List<NexusArtifactHit> getArtifactHits() {
		if (this.artifactHits == null) {
			this.artifactHits = new java.util.ArrayList<NexusArtifactHit>();
		}
		return this.artifactHits;
	}
	public String getArtifactId() {
		return this.artifactId;
	}
	public String getGroupId() {
		return this.groupId;
	} 
	public String getHighlightedFragment() {
		return this.highlightedFragment;
	}
	public String getLatestRelease() {
		return this.latestRelease;
	}
	public String getLatestReleaseRepositoryId() {
		return this.latestReleaseRepositoryId;
	}
	public String getLatestSnapshot() {
		return this.latestSnapshot;
	}
	public String getLatestSnapshotRepositoryId() {
		return this.latestSnapshotRepositoryId;
	}
	public String getVersion() {
		return this.version;
	}
	public void setArtifactHits(java.util.List<NexusArtifactHit> artifactHits) {
		this.artifactHits = artifactHits;
	} 
	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public void setHighlightedFragment(String highlightedFragment) {
		this.highlightedFragment = highlightedFragment;
	}
	public void setLatestRelease(String latestRelease) {
		this.latestRelease = latestRelease;
	}
	public void setLatestReleaseRepositoryId(String latestReleaseRepositoryId) {
		this.latestReleaseRepositoryId = latestReleaseRepositoryId;
	}
	public void setLatestSnapshot(String latestSnapshot) {
		this.latestSnapshot = latestSnapshot;
	}
	public void setLatestSnapshotRepositoryId(String latestSnapshotRepositoryId) {
		this.latestSnapshotRepositoryId = latestSnapshotRepositoryId;
	}
	public void setVersion(String version) {
		this.version = version;
	}

}
