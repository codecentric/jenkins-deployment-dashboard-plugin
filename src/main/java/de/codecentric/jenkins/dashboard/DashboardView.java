package de.codecentric.jenkins.dashboard;

import static de.codecentric.jenkins.dashboard.util.LocalMessages.DASHBOARD_VIEW_DISPLAYNAME;
import hudson.Extension;
import hudson.model.Item;
import hudson.model.TopLevelItem;
import hudson.model.ViewGroup;
import hudson.model.Descriptor;
import hudson.model.ModifiableItemGroup;
import hudson.model.View;
import hudson.model.ViewDescriptor;
import hudson.util.FormValidation;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;

import jenkins.model.Jenkins;

import org.apache.commons.httpclient.HttpState;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.bind.JavaScriptMethod;
import org.springframework.util.StringUtils;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;

import de.codecentric.jenkins.dashboard.Environment.EnvironmentDescriptor;
import de.codecentric.jenkins.dashboard.api.environment.ServerEnvironment;
import de.codecentric.jenkins.dashboard.api.repository.Artifact;
import de.codecentric.jenkins.dashboard.artifactrepositories.ArtifactoryConnector;
import de.codecentric.jenkins.dashboard.ec2.EC2Connector;

/**
 * Dashboard View
 * 
 */
public class DashboardView extends View {

	private final static Logger LOGGER = Logger.getLogger(DashboardView.class.getName());

	@Extension
	public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

	private boolean showDeployField;
	
	private String artifactoryRestUri = "";
	private String username = "";
	private String password = "";
	private String artefactId = "";
	private String deployJobUri = "";
	private List<Environment> environments;
	
	private String awsAccessKey = "";
	private String awsSecretKey = "";
	
	public DashboardView(final String name) {
		super(name);
	}

	public DashboardView(final String name, final ViewGroup owner) {
		super(name, owner);
	}

	@DataBoundConstructor
	public DashboardView(
			final String name, final String artifactoryRestUri,
			final boolean showDeployField,
			final String username, final String password,
			final String awsAccessKey, final String awsAccessSecret,
			final String artefactId, final String deployJobUri,
			final List<Environment> environments, final String region) {
		this(name);
		setShowDeployField(showDeployField);
		setArtifactoryRestUri(artifactoryRestUri);
		setUsername(username);
		setPassword(password);
		setAwsAccessKey(awsAccessKey);
		setAwsSecretKey(awsSecretKey);
		setArtefactId(artefactId);
		setDeployJobUri(deployJobUri);
		setEnvironments(environments);
	}

	@Override
	public ViewDescriptor getDescriptor() {
		return DESCRIPTOR;
	}

	/**
	 * Gets all the items in this collection in a read-only view.
	 */
	@Override
	public Collection<TopLevelItem> getItems() {
		return new ArrayList<TopLevelItem>();
	}

	/**
	 * Checks if the job is in this collection.
	 *
	 * @param item
	 */
	@Override
	public boolean contains(final TopLevelItem item) {
		return false;
	}
	
	@JavaScriptMethod
    public String deploy(String version, String environment) {
		LOGGER.info("Deployment of version " + version + " for environment " + environment + " via " + deployJobUri);

		Client client = ClientBuilder.newClient();
		Invocation.Builder invocationBuilder = client.target(deployJobUri + "/buildWithParameters").queryParam("VERSION", version).queryParam("ENVIRONMENT", environment).request();
		Response response = invocationBuilder.get();
		if( response.getStatus() == Response.Status.CREATED.getStatusCode() ) {
			return "Deployment of version " + version + " for environment " + environment + " successfully triggered.";
		};  
		return "Deployment not successful.";
    }
	
	/**
	 * Handles the configuration submission.
	 * <p/>
	 * Load view-specific properties here.
	 *
	 * @param req
	 */
	@Override
	protected synchronized void submit(final StaplerRequest req) throws IOException, ServletException, Descriptor.FormException {
		LOGGER.info("DashboardView submit configuration");
		req.bindJSON(this, req.getSubmittedForm()); // Mapping the JSON directly should work
	}

	/**
	 * Creates a new {@link hudson.model.Item} in this collection.
	 * <p/>
	 * <p/>
	 * This method should call
	 * {@link ModifiableItemGroup#doCreateItem(org.kohsuke.stapler.StaplerRequest, org.kohsuke.stapler.StaplerResponse)}
	 * and then add the newly created item to this view.
	 *
	 * @param req
	 * @param rsp
	 * @return null if fails.
	 */
	@Override
	public Item doCreateItem(final StaplerRequest req, final StaplerResponse rsp) throws IOException, ServletException {
		return Jenkins.getInstance().doCreateItem(req, rsp);
	}

	public String getDisplayDeployField(){
		return showDeployField ? "" : "display:none;";
	}
	
	public boolean getShowDeployField() {
		return showDeployField;
	}

	public void setShowDeployField(boolean showDeployField) {
		this.showDeployField = showDeployField;
	}

	public String getArtifactoryRestUri() {
		return artifactoryRestUri;
	}

	public void setArtifactoryRestUri(final String artifactoryRestUri) {
		this.artifactoryRestUri = artifactoryRestUri;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	public String getArtefactId() {
		return artefactId;
	}

	public void setArtefactId(final String artefactId) {
		this.artefactId = artefactId;
	}

	public String getDeployJobUri() {
		return deployJobUri;
	}

	public void setDeployJobUri(final String deployJobUri) {
		this.deployJobUri = deployJobUri;
	}

	public List<Artifact> getArtifacts() {
		LOGGER.info("Getting artifacts");

		URI repositoryURI;
		try {
			repositoryURI = new URI(artifactoryRestUri);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return new ArrayList<Artifact>();
		}
		ArtifactoryConnector repository = new ArtifactoryConnector(username, password, repositoryURI);
		List<Artifact> versions = repository.getArtefactList(artefactId);
		return versions;
	}

	public List<ServerEnvironment> getEC2Environments() {
		AWSCredentials awsCredentials = new BasicAWSCredentials(getAwsAccessKey(), getAwsSecretKey());
		EC2Connector env = new EC2Connector(awsCredentials);
		
		if( ! env.areAwsCredentialsValid() ) {
			System.out.println("AWS Credentials are invalid");
			return new ArrayList<ServerEnvironment>();
		}
		
		List<ServerEnvironment> list = new ArrayList<ServerEnvironment>();
		for( Environment envTag : environments) {
			List<ServerEnvironment> foundEnvironment = env.getEnvironmentsByTag(Region.getRegion(Regions.EU_WEST_1), envTag.getName());
			list.addAll(foundEnvironment);
		}
		return list;
	}
	
	public List<Environment> getEnvironments() {
		return Collections.unmodifiableList(environments);
	}

	public void setEnvironments(final List<Environment> environmentsList) {
		this.environments = environmentsList == null ? new ArrayList<Environment>()
				: new ArrayList<Environment>(environmentsList);
	}

	public String getAwsAccessKey() {
		return awsAccessKey;
	}

	public void setAwsAccessKey(String awsAccessKey) {
		this.awsAccessKey = awsAccessKey;
	}

	public String getAwsSecretKey() {
		return awsSecretKey;
	}

	public void setAwsSecretKey(String awsSecretKey) {
		this.awsSecretKey = awsSecretKey;
	}

	public static class DescriptorImpl extends ViewDescriptor {

		@Override
		public String getDisplayName() {
			return DASHBOARD_VIEW_DISPLAYNAME.toString();
		}

		public List<EnvironmentDescriptor> getEnvironmentDescriptors() {
			return Jenkins.getInstance().getDescriptorList(Environment.class);
		}

		@Override
		public String getHelpFile() {
			return "/plugin/jenkins-deployment-dashboard-plugin/help.html";
		}

		public FormValidation doCheckArtifactoryRestUri(@QueryParameter final String artifactoryRestUri) {
			return FormValidation.ok();
		}

		public FormValidation doCheckUsername(@QueryParameter final String username) {
			
			if( StringUtils.hasText(username) ) {
				return FormValidation.ok();
			}
			return FormValidation.warning(Messages.DashboardView_artifactoryUsername());
		}

		public FormValidation doCheckPassword(@QueryParameter final String password) {

			if( StringUtils.hasText(password) ) {
				return FormValidation.ok();
			}
			return FormValidation.warning(Messages.DashboardView_artifactoryPassword());
		}

		public FormValidation doTestArtifactoryConnection(
				@QueryParameter("artifactoryRestUri") final String artifactoryRestUri,
				@QueryParameter("username") final String username,
				@QueryParameter("password") final String password) {

			LOGGER.info("Verify Artifactory connection for URI " + artifactoryRestUri);

			FormValidation validationResult;
			try {
				URI repositoryURI = new URI(artifactoryRestUri);
				ArtifactoryConnector repository = new ArtifactoryConnector(username, password, repositoryURI);
				LOGGER.info("Artifactory config valid? " + repository.canConnect());
				if (repository.canConnect()) {
					validationResult = FormValidation.ok(Messages.DashboardView_artifactoryConnectionSuccessful());
				} else {
					validationResult = FormValidation.warning(Messages.DashboardView_artifactoryConnectionFailed() + artifactoryRestUri);
				}

			} catch (Exception e) {
				LOGGER.severe(e.getMessage());
				validationResult = FormValidation.error(Messages.DashboardView_artifactoryConnectionCritical() + e.getMessage());
			}

			return validationResult;
		}

	}

}
