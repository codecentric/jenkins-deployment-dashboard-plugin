package de.codecentric.jenkins.dashboard.impl.repositories.nexus;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

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
 * Implementation of Sonatype Nexus repository integration.
 * 
 * @author marcel.birkner
 * 
 */
public class NexusConnector implements RepositoryInterface {

    private final static Logger LOGGER = LoggerFactory.getLogger(NexusConnector.class);

    private String username;
    private String password;
    private URI repositoryURI;

    public NexusConnector(String username, String password, URI repositoryURI) {
        this.username = username;
        this.password = password;
        this.repositoryURI = repositoryURI;
    }

    public boolean canConnect() {
        LOGGER.info("Checking Nexus connection");

        final Client client = buildClient();
        final WebResource restResource = client.resource(repositoryURI);
        final ClientResponse response = restResource.accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);

        int status = response.getStatus();
        if (status == httpStatusOk) {
            return true;
        }
        LOGGER.warn("Could not connect to {}. ResponseCode: {}", repositoryURI, status);
        return false;
    }

    public List<Artifact> getArtefactList(String artifactId) {
        return getArtefactList("", artifactId);
    }

    public List<Artifact> getArtefactList(String groupId, String artifactId) {
        LOGGER.info("Get artifact list for " + groupId + " " + artifactId);
        List<Artifact> list = new ArrayList<Artifact>();

        final Client client = buildClient();
        final WebResource restResource = client.resource(repositoryURI);
        WebResource path = restResource.path("service").path("local").path("lucene").path("search");
        WebResource queryParam = path.queryParam("g", groupId).queryParam("a", artifactId);
        final ClientResponse clientResponse = queryParam.accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);

        final SearchResponse response = clientResponse.getEntity(SearchResponse.class);
        List<NexusArtifact> data = response.getData();
        for (NexusArtifact nexusArtifact : data) {
            Artifact a = new Artifact(nexusArtifact.getArtifactId(), nexusArtifact.getVersion(), "");
            list.add(a);
        }

        return list;
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