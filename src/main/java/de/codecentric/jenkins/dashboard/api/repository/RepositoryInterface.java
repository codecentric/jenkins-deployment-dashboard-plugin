package de.codecentric.jenkins.dashboard.api.repository;

import java.util.List;

public interface RepositoryInterface {

	public boolean canConnect();
	public List<Artifact> getArtefactList(String artifactId);
}