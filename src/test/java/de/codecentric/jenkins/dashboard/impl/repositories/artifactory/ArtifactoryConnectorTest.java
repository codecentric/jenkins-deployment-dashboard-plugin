package de.codecentric.jenkins.dashboard.impl.repositories.artifactory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import com.sun.jersey.api.client.ClientResponse;

public class ArtifactoryConnectorTest {

    private static final String USER = "jenkins";
    private static final String PASS = "c0d3c3ntr1c";
    private URI repositoryURI;
    private static final int PORT = 99;

    private ArtifactoryConnector repositoryInterface;

    private static final ClientResponse mockedResponse = mock(ClientResponse.class);

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
        when(mockedResponse.getStatus()).thenReturn(200);
        boolean canConnect = Whitebox.<Boolean> invokeMethod(repositoryInterface, "canConnect", mockedResponse);
        assertThat(canConnect, is(true));
    }

    @Test
    public void testCanConnectFalse() throws Exception {
        when(mockedResponse.getStatus()).thenReturn(500);
        boolean canConnect = Whitebox.<Boolean> invokeMethod(repositoryInterface, "canConnect", mockedResponse);
        assertThat(canConnect, is(false));
    }

}
