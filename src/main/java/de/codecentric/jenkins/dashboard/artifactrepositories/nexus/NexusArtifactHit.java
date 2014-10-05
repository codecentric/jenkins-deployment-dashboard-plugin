package de.codecentric.jenkins.dashboard.artifactrepositories.nexus;

import org.sonatype.nexus.rest.model.NexusNGArtifactLink;

/**
 * Nexus artifact hit count model. Used for JSON un-/marshalling.
 * 
 */
@javax.xml.bind.annotation.XmlType(name = "artifactHit")
@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
public class NexusArtifactHit implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private String repositoryId;

	@javax.xml.bind.annotation.XmlElement(name = "artifactLinks")
	private java.util.List<NexusNGArtifactLink> artifactLinks;

	public java.util.List<NexusNGArtifactLink> getArtifactLinks() {
		if (this.artifactLinks == null) {
			this.artifactLinks = new java.util.ArrayList<NexusNGArtifactLink>();
		}
		return this.artifactLinks;
	}

	public String getRepositoryId() {
		return this.repositoryId;
	}

	public void setArtifactLinks(java.util.List<NexusNGArtifactLink> artifactLinks) {
		this.artifactLinks = artifactLinks;
	}

	public void setRepositoryId(String repositoryId) {
		this.repositoryId = repositoryId;
	} 

}