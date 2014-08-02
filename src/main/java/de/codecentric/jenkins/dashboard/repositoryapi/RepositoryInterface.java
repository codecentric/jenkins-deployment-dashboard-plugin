package de.codecentric.jenkins.dashboard.repositoryapi;

import java.util.List;

public interface RepositoryInterface {

	public boolean canConnect();
	public List<Artifact> getArtifactList(String artifactName);
}