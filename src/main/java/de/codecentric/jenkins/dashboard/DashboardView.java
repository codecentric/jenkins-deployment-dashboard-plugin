package de.codecentric.jenkins.dashboard;

import static de.codecentric.jenkins.dashboard.util.LocalMessages.DASHBOARD_VIEW_DISPLAYNAME;
import hudson.Extension;
import hudson.model.Item;
import hudson.model.TopLevelItem;
import hudson.model.ViewGroup;
import hudson.model.ChoiceParameterDefinition;
import hudson.model.Descriptor;
import hudson.model.ModifiableItemGroup;
import hudson.model.View;
import hudson.model.ViewDescriptor;
import hudson.util.FormValidation;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import jenkins.model.Jenkins;
import net.sf.json.JSONObject;

import org.jfrog.artifactory.client.Artifactory;
import org.jfrog.artifactory.client.ArtifactoryClient;
import org.jfrog.artifactory.client.model.RepoPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import de.codecentric.jenkins.dashboard.artifactory.ArtifactoryConnector;

public class DashboardView extends View {

	private final static Logger LOG = Logger.getLogger(DashboardView.class.getName());

	@Extension
	public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

	private String artifactoryRestUri = "";
	private String username = "";
	private String password = "";
	private String artefactId = "";
	private String deployJobUri = "";
	private String environmentConfig = "";
	private List<String> environments = new ArrayList<String>();
	
	public DashboardView(final String name) {
		super(name);
	}

	public DashboardView(final String name, final ViewGroup owner) {
		super(name, owner);
	}

	@DataBoundConstructor
	public DashboardView(
			final String name, final String artifactoryRestUri,
			final String username, final String password, 
			final String artefactId, final String deployJobUri,
			final String environmentConfig) {
		this(name);
		setArtifactoryRestUri(artifactoryRestUri);
		setUsername(username);
		setPassword(password);
		setArtefactId(artefactId);
		setDeployJobUri(deployJobUri);
		setEnvironmentConfig(environmentConfig);
		LOG.info("DataBoundConstructor");
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

	/**
	 * Handles the configuration submission.
	 * <p/>
	 * Load view-specific properties here.
	 * 
	 * @param req
	 */
	@Override
	protected synchronized void submit(final StaplerRequest req)
			throws IOException, ServletException, Descriptor.FormException {
		LOG.info("DashboardView submitted configuration");

		JSONObject json = req.getSubmittedForm();
		this.artifactoryRestUri = (String) json.get("artifactoryRestUri");
		this.username = (String) json.get("username");
		this.password = (String) json.get("password");
		this.artefactId = (String) json.get("artefactId");
		this.deployJobUri = (String) json.get("deployJobUri");
		this.environmentConfig = (String) json.get("environmentConfig");

		ChoiceParameterDefinition choices = new ChoiceParameterDefinition("choice", this.environmentConfig, "Description");
		this.environments = choices.getChoices();
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
	public Item doCreateItem(final StaplerRequest req, final StaplerResponse rsp)
			throws IOException, ServletException {
		return Jenkins.getInstance().doCreateItem(req, rsp);
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

	public void setArtefactId(String artefactId) {
		this.artefactId = artefactId;
	}

	public String getDeployJobUri() {
		return deployJobUri;
	}

	public void setDeployJobUri(String deployJobUri) {
		this.deployJobUri = deployJobUri;
	}

	public List<String> getVersions() {
		LOG.info("getting all versions from artefact repository");

		List<String> versions = new ArrayList<String>();
		Set<String> versionsSet = new TreeSet<String>();
		Artifactory artifactory = ArtifactoryClient.create(artifactoryRestUri, username, password);		
		List<RepoPath> results = artifactory.searches().artifactsByName(artefactId).doSearch();
		LOG.info("Found " + results.size() + " matching artefacts");
		for (int i = 0; i < results.size(); i++) {
			String itemPath = results.get(i).getItemPath();
			String[] split = itemPath.split(artefactId);
			String version = split[1].replaceAll("/", "");
			versionsSet.add(version);
		}
		versions.addAll(versionsSet);
		Collections.sort(versions);
		return versions;
	}

	/**
	 * @return the environments
	 */
	public List<String> getEnvironments() {
		return environments;
	}

	/**
	 * @param environments the environments to set
	 */
	public void setEnvironments(List<String> environments) {
		this.environments = environments;
	}

	/**
	 * @return the environmentConfig
	 */
	public String getEnvironmentConfig() {
		return environmentConfig;
	}

	/**
	 * @param environmentConfig the environmentConfig to set
	 */
	public void setEnvironmentConfig(String environmentConfig) {
		this.environmentConfig = environmentConfig;
	}

	public static class DescriptorImpl extends ViewDescriptor {

		@Override
		public String getDisplayName() {
			return DASHBOARD_VIEW_DISPLAYNAME.toString();
		}

		@Override
		public String getHelpFile() {
			return "/plugin/jenkins-deployment-dashboard-plugin/help.html";
		}

		public FormValidation doCheckArtifactoryRestUri(
				@QueryParameter final String artifactoryRestUri) {
			return FormValidation.ok();
		}

		public FormValidation doCheckUsername(
				@QueryParameter final String username) {
			return FormValidation.ok();
		}

		public FormValidation doCheckPassword(
				@QueryParameter final String password) {
			return FormValidation.ok();
		}

		public FormValidation doTestArtifactoryConnection(
				@QueryParameter("artifactoryRestUri") final String artifactoryRestUri,
				@QueryParameter("username") final String username,
				@QueryParameter("password") final String password) {

			LOG.info("Verify Artifactory Connection for URI "
					+ artifactoryRestUri);

			FormValidation validationResult;
			try {
				URI repositoryURI = new URI(artifactoryRestUri);
				ArtifactoryConnector repository = new ArtifactoryConnector(
						username, password, repositoryURI);
				LOG.info("Artifactory config valid? " + repository.canConnect());
				if (repository.canConnect()) {
					validationResult = FormValidation
							.ok("Connection with Artifactory successful.");
				} else {
					validationResult = FormValidation
							.warning("Connection with Artifactory could not be established. Please check your credentials and URL "
									+ artifactoryRestUri);
				}

			} catch (Exception e) {
				LOG.severe(e.getMessage());
				validationResult = FormValidation
						.error("A critical error occured when checking your configuration settings."
								+ e.getMessage());
			}

			return validationResult;
		}

	}

}
