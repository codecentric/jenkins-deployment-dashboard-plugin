package de.codecentric.jenkins.dashboard.impl.repositories.artifactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.ws.rs.core.MediaType;

import org.jfrog.artifactory.client.Artifactory;
import org.jfrog.artifactory.client.ArtifactoryClient;
import org.jfrog.artifactory.client.model.RepoPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.client.apache.ApacheHttpClient;
import com.sun.jersey.client.apache.config.DefaultApacheHttpClientConfig;

import de.codecentric.jenkins.dashboard.api.repositories.Artifact;
import de.codecentric.jenkins.dashboard.api.repositories.RepositoryInterface;

/**
 * Implementation of Artifactory repository integration
 * 
 * @author marcel.birkner
 * 
 */
public class ArtifactoryConnector implements RepositoryInterface {

    private final static Logger LOGGER = LoggerFactory.getLogger(ArtifactoryConnector.class);

    private String username;
    private String password;
    private URI repositoryURI;

    public ArtifactoryConnector(String username, String password, URI repositoryURI) {
        LOGGER.info("ArtifactoryConnector");

        this.username = username;
        this.password = password;
        this.repositoryURI = repositoryURI;
    }

    public boolean canConnect() {
        LOGGER.info("Checking Artifactory connection");

        ClientResponse response = getResponse();
        return canConnect(response);
    }

    protected boolean canConnect(ClientResponse response) {
        int status = response.getStatus();
        if (status == httpStatusOk) {
            LOGGER.info("Connection ok");
            return true;
        }

        LOGGER.error("Could not connect to {}. ResponseCode: {}", repositoryURI, status);
        return false;
    }

    public List<Artifact> getArtefactList(String artifactId) {
        return getArtefactList("", artifactId);
    }

    public List<Artifact> getArtefactList(String groupId, String artifactId) {
        LOGGER.info("Getting Artefact List from Server [" + repositoryURI + "] for " + groupId + " and " + artifactId);

        List<Artifact> artifactList = new ArrayList<Artifact>();
        if (!org.springframework.util.StringUtils.hasText(artifactId)) {
            LOGGER.warn("artifactId is empty. Cannot search for artifacts.");
            return artifactList;
        }
        if (!canConnect()) {
            LOGGER.warn("Please validate your repository connection configuration under the global config.");
            return artifactList;
        }

        List<String> versions = new ArrayList<String>();
        Set<String> versionsSet = new TreeSet<String>();
        Artifactory artifactory = ArtifactoryClient.create(repositoryURI.toString(), username, password);
        List<RepoPath> results = artifactory.searches().artifactsByName(artifactId).doSearch();

        for (RepoPath item : results) {
            String itemPath = item.getItemPath();
            String[] split = itemPath.split(artifactId);
            String itemVersion = split[1].replaceAll("/", "");
            String itemGroupId = org.apache.commons.lang.StringUtils.removeEnd(split[0], "/").replace("/", ".");
            if (org.springframework.util.StringUtils.hasText(itemGroupId) && itemGroupId.equalsIgnoreCase(groupId)) {
                versionsSet.add(itemVersion);
            } else {
                versionsSet.add(itemVersion);
            }
        }

        versions.addAll(versionsSet);
        Collections.sort(versions);
        Collections.reverse(versions);

        for (String version : versions) {
            artifactList.add(new Artifact(artifactId, version, ""));
        }

        return artifactList;
    }

    private ClientResponse getResponse() {
        final Client client = buildClient();
        final WebResource restResource = client.resource(repositoryURI);
        final ClientResponse response = restResource.accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);
        return response;
    }

    private Client buildClient() {
        DefaultApacheHttpClientConfig config = new DefaultApacheHttpClientConfig();
        config.getState().setCredentials(null, null, -1, username, password);
        config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        Client restClient = ApacheHttpClient.create(config);
        restClient.setFollowRedirects(true);

        return restClient;
    }

}
