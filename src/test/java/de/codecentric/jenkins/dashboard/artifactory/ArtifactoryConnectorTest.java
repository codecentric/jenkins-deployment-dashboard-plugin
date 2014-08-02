package de.codecentric.jenkins.dashboard.artifactory;

import static org.junit.Assert.*;

import java.net.URI;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.codecentric.jenkins.dashboard.repositoryapi.RepositoryInterface;

public class ArtifactoryConnectorTest {
	
	private static final String USER = "jenkins";
	private static final String PASS = "c0d3c3ntr1c";
	private URI repositoryURI;

	private RepositoryInterface repositoryInterface;
	
	@Before
	public void setUp() throws Exception {
		repositoryURI = new URI("http://23.21.157.166:8081/artifactory/api/search/artifact?name=worblehat-web&repos=libs-release-local");
		repositoryInterface = new ArtifactoryConnector(USER, PASS, repositoryURI);
	}

	@After
	public void tearDown() throws Exception {
		repositoryURI = null;
		repositoryInterface = null;
	}

	@Test
	public void testCanConnect() throws Exception {
		assertTrue(repositoryInterface.canConnect());
	}

}
