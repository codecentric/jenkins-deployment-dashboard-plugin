package de.codecentric.jenkins.dashboard.impl.repositories.nexus;

import org.sonatype.nexus.rest.model.NexusNGArtifact;
import org.sonatype.nexus.rest.model.NexusNGRepositoryDetail;

/**
 * Nexus artifact repository search response model. Used for JSON un-/marshalling.
 * 
 */
@javax.xml.bind.annotation.XmlRootElement(name = "searchResponse")
@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
public class SearchResponse implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The grand total number of results found on index.
     */
    private int totalCount = 0;

    /**
     * The starting index of the results.
     */
    private int from = 0;

    /**
     * The number of results in this response.
     */
    private int count = 0;

    /**
     * Flag that states if too many results were found.
     */
    private boolean tooManyResults = false;

    /**
     * Flag that states if result set is collapsed, and shows latest versions only.
     */
    private boolean collapsed = false;

    /**
     * Field repositoryDetails.
     */
    @javax.xml.bind.annotation.XmlElement(name = "repoDetails")
    private java.util.List<NexusNGRepositoryDetail> repositoryDetails;

    /**
     * Field data.
     */
    @javax.xml.bind.annotation.XmlElement(name = "data")
    private java.util.List<NexusArtifact> data;

    public int getCount() {
	return this.count;
    }

    public java.util.List<NexusArtifact> getData() {
	if (this.data == null) {
	    this.data = new java.util.ArrayList<NexusArtifact>();
	}
	return this.data;
    }

    public int getFrom() {
	return this.from;
    }

    public java.util.List<NexusNGRepositoryDetail> getRepoDetails() {
	if (this.repositoryDetails == null) {
	    this.repositoryDetails = new java.util.ArrayList<NexusNGRepositoryDetail>();
	}
	return this.repositoryDetails;
    }

    public int getTotalCount() {
	return this.totalCount;
    }

    public boolean isCollapsed() {
	return this.collapsed;
    }

    public boolean isTooManyResults() {
	return this.tooManyResults;
    }

    public void removeData(NexusNGArtifact nexusNGArtifact) {
	getData().remove(nexusNGArtifact);
    }

    public void removeRepoDetail(NexusNGRepositoryDetail nexusNGRepositoryDetail) {
	getRepoDetails().remove(nexusNGRepositoryDetail);
    }

    public void setCollapsed(boolean collapsed) {
	this.collapsed = collapsed;
    }

    public void setCount(int count) {
	this.count = count;
    }

    public void setData(java.util.List<NexusArtifact> data) {
	this.data = data;
    }

    public void setFrom(int from) {
	this.from = from;
    }

    public void setRepositoryDetail(java.util.List<NexusNGRepositoryDetail> repositoryDetails) {
	this.repositoryDetails = repositoryDetails;
    }

    public void setTooManyResults(boolean tooManyResults) {
	this.tooManyResults = tooManyResults;
    }

    public void setTotalCount(int totalCount) {
	this.totalCount = totalCount;
    }

}