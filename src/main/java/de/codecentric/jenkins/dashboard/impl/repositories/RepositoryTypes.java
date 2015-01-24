package de.codecentric.jenkins.dashboard.impl.repositories;

import de.codecentric.jenkins.dashboard.Messages;

/**
 * Specify supported Repository Types. Currently we support <b>Nexus</b> and
 * <b>Artifactory</b>
 */
public enum RepositoryTypes {

    NEXUS(Messages.RepositoryType_nexus(), Messages.RepositoryType_nexusFullName()), //
    ARTIFACTORY(Messages.RepositoryType_artifactory(), Messages.RepositoryType_artifactoryFullName());

    private String id;
    private String description;

    private RepositoryTypes(String id, String description) {
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