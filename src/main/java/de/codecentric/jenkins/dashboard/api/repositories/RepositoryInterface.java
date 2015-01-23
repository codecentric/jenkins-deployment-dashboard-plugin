package de.codecentric.jenkins.dashboard.api.repositories;

import java.util.List;

/**
 * Interface for accessing artifact repository information.
 * 
 * @author marcel.birkner
 * 
 */
public interface RepositoryInterface {

    int httpStatusOk = 200;

    public boolean canConnect();

    public List<Artifact> getArtefactList(String artifactId);

    public List<Artifact> getArtefactList(String groupId, String artifactId);

}