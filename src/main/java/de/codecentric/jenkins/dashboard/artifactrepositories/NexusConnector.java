package de.codecentric.jenkins.dashboard.artifactrepositories;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.codecentric.jenkins.dashboard.api.repository.Artifact;
import de.codecentric.jenkins.dashboard.api.repository.RepositoryInterface;
import de.codecentric.jenkins.dashboard.artifactrepositories.nexus.NexusArtifact;
import de.codecentric.jenkins.dashboard.artifactrepositories.nexus.SearchResponse;

/**
 * Implementation of Sonatype Nexus repository integration
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

		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(username, password);
		Client client = ClientBuilder.newClient();
		client.register(feature);
		Invocation.Builder invocationBuilder = client.target(repositoryURI).request();
		Response response = invocationBuilder.get();
		int status = response.getStatus();
		if (status == 200) {
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
	
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(username, password);
		Client client = ClientBuilder.newClient();
		client.register(feature);
		WebTarget path = client.target(repositoryURI).path("service").path("local").path("lucene").path("search");
		Invocation.Builder builder = path.queryParam("g", groupId).queryParam("a", artifactId).request(MediaType.APPLICATION_JSON);
		SearchResponse response= builder.get(SearchResponse.class);

		List<NexusArtifact> data = response.getData();
		for (NexusArtifact nexusArtifact : data) {
			Artifact a = new Artifact(nexusArtifact.getArtifactId(), nexusArtifact.getVersion(), "");
			list.add(a);
		}
		
		return list;
	}

}