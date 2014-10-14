package de.codecentric.jenkins.dashboard.artifactrepositories;
 
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.net.URI;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.sun.jersey.api.client.ClientResponse;
import de.codecentric.jenkins.dashboard.artifactrepositories.ArtifactoryConnector;

public class ArtifactoryConnectorTest {
	
	private static final String USER = "jenkins";
	private static final String PASS = "c0d3c3ntr1c";
	private URI repositoryURI;
	private static final int PORT = 99;
	
	private ArtifactoryConnector repositoryInterface;
		
	@Before
	public void setUp() throws Exception {
		repositoryURI = new URI("http://localhost:" + PORT + "/test");
		repositoryInterface = new ArtifactoryConnector(USER, PASS, repositoryURI);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCanConnectTrue() throws Exception {
		ClientResponse mockedResponse = Mockito.mock(ClientResponse.class);
		Mockito.when(mockedResponse.getStatus()).thenReturn(200);
		assertTrue(repositoryInterface.canConnect(mockedResponse));
	}


	@Test
	public void testCanConnectFalse() throws Exception {
		ClientResponse mockedResponse = Mockito.mock(ClientResponse.class);
		Mockito.when(mockedResponse.getStatus()).thenReturn(500);
		assertFalse(repositoryInterface.canConnect(mockedResponse));
	}
}
