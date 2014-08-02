package de.codecentric.jenkins.dashboard.artifactory;

import java.net.URI;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.codecentric.jenkins.dashboard.repositoryapi.Artifact;
import de.codecentric.jenkins.dashboard.repositoryapi.RepositoryInterface;

public class ArtifactoryConnector implements RepositoryInterface{
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ArtifactoryConnector.class);

	private String userName;
	private String password;
	private URI repositoryURI;

	public ArtifactoryConnector(String userName, String password, URI repositoryURI){
		this.userName = userName;
		this.password = password;
		this.repositoryURI = repositoryURI;
	}
	
	public boolean canConnect() {
		Response response = getResponse();
		int status = response.getStatus();
		if (status == 200){
			return true;
		}
		LOGGER.warn("Could not connect to %s. ResponseCode: %s", repositoryURI, status);
		return false;
	}

	public List<Artifact> getArtifactList(String artifactName) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private Response getResponse() {
		Client client = buildClient();
		Invocation.Builder invocationBuilder =
				client.target(repositoryURI).request();
		return invocationBuilder.get();
	}
	private Client buildClient() {
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(userName, password);
		Client client = ClientBuilder.newClient();
		client.register(feature);
		return client;
	}

	

}