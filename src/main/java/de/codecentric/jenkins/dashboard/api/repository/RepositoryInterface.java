package de.codecentric.jenkins.dashboard.api.repository;

import java.util.List;

/**
 * Interface for accessing repository information.
 * 
 * @author marcel.birkner
 *
 */
public interface RepositoryInterface {

	public boolean canConnect();
	public List<Artifact> getArtefactList(String artifactId);
	public List<Artifact> getArtefactList(String groupId, String artifactId);
	
}