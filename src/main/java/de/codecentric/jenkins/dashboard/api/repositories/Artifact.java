package de.codecentric.jenkins.dashboard.api.repositories;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Artifact information in artifact repository. These information are used in
 * the deployment section of the dashboard view in the dropdown list.
 * 
 * @author marcel.birkner
 * 
 */
public class Artifact {

    private String name;
    private String version;
    private String uri;

    public Artifact(String name, String version, String uri) {
        this.name = name;
        this.version = version;
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}