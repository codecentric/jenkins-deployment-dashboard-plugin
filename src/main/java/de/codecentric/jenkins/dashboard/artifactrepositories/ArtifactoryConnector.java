package de.codecentric.jenkins.dashboard.artifactrepositories;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.jfrog.artifactory.client.Artifactory;
import org.jfrog.artifactory.client.ArtifactoryClient;
import org.jfrog.artifactory.client.model.RepoPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.codecentric.jenkins.dashboard.api.repository.Artifact;
import de.codecentric.jenkins.dashboard.api.repository.RepositoryInterface;

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
		this.username = username;
		this.password = password;
		this.repositoryURI = repositoryURI;
	}

	public boolean canConnect() {
		Response response = getResponse();
		int status = response.getStatus();
		if (status == 200) {
			return true;
		}
		LOGGER.warn("Could not connect to {}. ResponseCode: {}", repositoryURI, status);
		return false;
	}

	public List<Artifact> getArtefactList(String artifactId) {
		List<String> versions = new ArrayList<String>();
		Set<String> versionsSet = new TreeSet<String>();
		Artifactory artifactory = ArtifactoryClient.create(repositoryURI.toString(), username, password);
		List<RepoPath> results = artifactory.searches().artifactsByName(artifactId).doSearch();
		
		for (int i = 0; i < results.size(); i++) {
			String itemPath = results.get(i).getItemPath();
			String[] split = itemPath.split(artifactId);
			String version = split[1].replaceAll("/", "");
			versionsSet.add(version);
		}
		
		versions.addAll(versionsSet);
		Collections.sort(versions);

		List<Artifact> artifactList = new ArrayList<Artifact>();
		for (String version : versions) {
			artifactList.add(new Artifact(artifactId, version, ""));
		}

		return artifactList;
	}

	private Response getResponse() {
		Client client = buildClient();
		Invocation.Builder invocationBuilder = client.target(repositoryURI).request();
		return invocationBuilder.get();
	}

	private Client buildClient() {
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(username, password);
		Client client = ClientBuilder.newClient();
		client.register(feature);
		return client;
	}

}