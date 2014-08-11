package de.codecentric.jenkins.dashboard.artifactory;

import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp;
import static com.xebialabs.restito.semantics.Action.status;
import static com.xebialabs.restito.semantics.Condition.get;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.URI;

import org.glassfish.grizzly.http.util.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.xebialabs.restito.server.StubServer;

import de.codecentric.jenkins.dashboard.api.repository.RepositoryInterface;
import de.codecentric.jenkins.dashboard.artifactrepositories.ArtifactoryConnector;

@Ignore
public class ArtifactoryConnectorTest {
	
	private static final String USER = "jenkins";
	private static final String PASS = "c0d3c3ntr1c";
	private URI repositoryURI;
	
	private RepositoryInterface repositoryInterface;
	
	private StubServer server;

	@Before
	public void setUp() throws Exception {
		server = new StubServer().run();
		int port = server.getPort();
		repositoryURI = new URI("http://localhost:" + port + "/test");
		repositoryInterface = new ArtifactoryConnector(USER, PASS, repositoryURI);
	}

	@After
	public void tearDown() throws Exception {
		server.stop();
		server = null;
		repositoryURI = null;
		repositoryInterface = null;
	}

	@Test
	public void testCanConnect() throws Exception {
		whenHttp(server).match(get("/test")).then(status(HttpStatus.OK_200));
		assertTrue(repositoryInterface.canConnect());
	}

	@Test
	public void testCantConnect() throws Exception{
		assertFalse(repositoryInterface.canConnect());
	}
}
