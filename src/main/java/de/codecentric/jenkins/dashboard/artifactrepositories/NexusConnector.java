package de.codecentric.jenkins.dashboard.artifactrepositories;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.nexus.rest.model.NexusNGArtifact;
import org.sonatype.nexus.rest.model.SearchNGResponse;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

import de.codecentric.jenkins.dashboard.api.repository.Artifact;
import de.codecentric.jenkins.dashboard.api.repository.RepositoryInterface;

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
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		client.addFilter(new HTTPBasicAuthFilter(username, password));
		WebResource service = client.resource(repositoryURI);
		ClientResponse nexusStatus = service.path("service").path("local")
				.path("status").accept(MediaType.APPLICATION_JSON)
				.get(ClientResponse.class);
		if (nexusStatus.getStatus() == Response.Status.OK.getStatusCode()) {
			return true;
		}

		LOGGER.warn("Could not connect to {}. ResponseCode: {}", repositoryURI, nexusStatus.getStatus());
		return false;
	}

	public List<Artifact> getArtefactList(String artifactId) {
		return getArtefactList("", artifactId);
	}
	
	public List<Artifact> getArtefactList(String groupId, String artifactId) {
		List<Artifact> list = new ArrayList<Artifact>();
	
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		client.addFilter( new HTTPBasicAuthFilter(username, password) ); 

		WebResource service = client.resource(repositoryURI);
		WebResource path = service.path("service").path("local").path("lucene").path("search");
		Builder builder = path.queryParam("g", groupId).queryParam("a", artifactId).accept(MediaType.APPLICATION_JSON);
		SearchNGResponse searchResult = builder.get(SearchNGResponse.class);
		
		List<NexusNGArtifact> data = searchResult.getData();
		for (NexusNGArtifact nexusNGArtifact : data) {
			Artifact a = new Artifact(nexusNGArtifact.getArtifactId(), nexusNGArtifact.getVersion(), "");
			list.add(a);
		}
		return list;
	}
}