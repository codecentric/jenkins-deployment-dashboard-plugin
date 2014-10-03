package de.codecentric.jenkins.dashboard;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.bind.JavaScriptMethod;


import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;

import de.codecentric.jenkins.dashboard.api.environment.ServerEnvironment;
import de.codecentric.jenkins.dashboard.api.repository.Artifact;
import de.codecentric.jenkins.dashboard.api.repository.RepositoryInterface;
import de.codecentric.jenkins.dashboard.artifactrepositories.ArtifactoryConnector;
import de.codecentric.jenkins.dashboard.artifactrepositories.NexusConnector;
import de.codecentric.jenkins.dashboard.ec2.EC2Connector;
import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Cause;
import hudson.model.Descriptor;
import hudson.model.Item;
import hudson.model.ModifiableItemGroup;
import hudson.model.ParametersAction;
import hudson.model.StringParameterValue;
import hudson.model.TopLevelItem;
import hudson.model.View;
import hudson.model.ViewDescriptor;
import hudson.model.ViewGroup;
import jenkins.model.Jenkins;

/**
 * Dashboard View
 * 
 */
public class DashboardView extends View {

	private final static Logger LOGGER = Logger.getLogger(DashboardView.class.getName());

	@Extension
	public static final DashboardViewDescriptor DESCRIPTOR = new DashboardViewDescriptor();

	private boolean showDeployField;

	private String groupId = "";
	private String artefactId = "";
	
	private List<Environment> environments;
	
	public DashboardView(final String name) {
		super(name);
	}

	public DashboardView(final String name, final ViewGroup owner) {
		super(name, owner);
	}

    @DataBoundConstructor
    public DashboardView(
            final String name, final boolean showDeployField,
            final String groupId, final String artefactId, final List<Environment> environments) {
        this(name);
        setShowDeployField(showDeployField);
        setGroupId(groupId);
        setArtefactId(artefactId);
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
		LOGGER.info("Deployment of version " + version + " for environment " + environment);

        // Get the environment with corresponding build-job
        Environment buildEnvironment = null;
        for (Environment env : environments) {
            if (env.getAwsInstance().equals(environment)) {
                buildEnvironment = env;
                break;
            }
        }

        final AbstractProject buildJob = Jenkins.getInstance().getItemByFullName(buildEnvironment.getBuildJob(), AbstractProject.class);
        LOGGER.info("Executing job: " + buildJob);

        if (buildJob == null) {
            return String.format("Build job corresponding to environment %s cannot be found, please try to configure again.",
                    buildEnvironment.getName());
        }

        if ((!buildJob.isBuildable()) || (!buildJob.isParameterized())) {
            return "Deployment cannot be executed. Please check that the Deployment Job is not disabled and if its correctly "
                    + "configured as a parameterized job that takes one parameter [VERSION].";
        }

        final ParametersAction versionParam = new ParametersAction(new StringParameterValue("VERSION", version));
        final ParametersAction environmentParams = new ParametersAction(new StringParameterValue("ENVIRONMENT", environment));
        
        // TODO change to using 'scheduleBuild2' which will return a Future object so we can wait for completion.
        final boolean schedulingSuccessful = buildJob.scheduleBuild(2, new Cause.UserIdCause(), versionParam, environmentParams);

        if (schedulingSuccessful) {
            return String.format("Successfully scheduled build job %s, waiting for completion...", buildJob.getName());
        } else {
            return String.format("Failed scheduling build job %s for unknown reason. Please check the configuration.", buildJob.getName());
        }
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

	public String getArtefactId() {
		return artefactId;
	}

	public void setArtefactId(final String artefactId) {
		this.artefactId = artefactId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public List<Artifact> getArtifacts() {
		LOGGER.info("Getting artifacts for " + DESCRIPTOR.getRepositoryType());

		// User needs to configure an artifact repository on the global config page
		if( DESCRIPTOR.getRepositoryType() == null ) {
			return new ArrayList<Artifact>();
		}
		
		RepositoryInterface repository;
		try {
			repository = createRepository();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return new ArrayList<Artifact>();
		}
		
		List<Artifact> versions = repository.getArtefactList(groupId, artefactId);
		return versions;
	}

	private RepositoryInterface createRepository() throws URISyntaxException {
		URI repositoryURI = new URI(DESCRIPTOR.getRepositoryRestUri());
		RepositoryInterface repository;
		
		System.out.println(DESCRIPTOR.getRepositoryType());

		if( DESCRIPTOR.getRepositoryType().equalsIgnoreCase( RepositoryType.ARTIFACTORY.getid() )) {
			repository = new ArtifactoryConnector(DESCRIPTOR.getUsername(), DESCRIPTOR.getPassword(), repositoryURI);
		} else {
			repository = new NexusConnector(DESCRIPTOR.getUsername(), DESCRIPTOR.getPassword(), repositoryURI);
		}
		return repository;
	}

	public List<ServerEnvironment> getMatchingEC2Environments() {
		final AWSCredentials awsCredentials = new BasicAWSCredentials(DESCRIPTOR.getAwsAccessKey(), DESCRIPTOR.getAwsSecretKey());
		final EC2Connector env = new EC2Connector(awsCredentials);
		
		if (! env.areAwsCredentialsValid()) {
			System.out.println("AWS Credentials are invalid");
			return new ArrayList<ServerEnvironment>();
		}
		
		final List<ServerEnvironment> list = new ArrayList<ServerEnvironment>();
		for (Environment envTag : environments) {
			List<ServerEnvironment> foundEnvironment = env.getEnvironmentsByTag(Region.getRegion(Regions.fromName(DESCRIPTOR.getAwsRegion())),
                    envTag.getAwsInstance());
			list.addAll(foundEnvironment);
		}
		return list;
	}
	
	public List<ServerEnvironment> getAllEC2Environments() {
		final AWSCredentials awsCredentials = new BasicAWSCredentials(DESCRIPTOR.getAwsAccessKey(), DESCRIPTOR.getAwsSecretKey());
		final EC2Connector env = new EC2Connector(awsCredentials);

		if (! env.areAwsCredentialsValid()) {
			System.out.println("AWS Credentials are invalid");
			return new ArrayList<ServerEnvironment>();
		}

        return env.getEnvironments(Region.getRegion(Regions.fromName(DESCRIPTOR.getAwsRegion())));
	}

	public List<Environment> getEnvironments() {
		return Collections.unmodifiableList(environments);
	}

	public void setEnvironments(final List<Environment> environmentsList) {
		this.environments = environmentsList == null ? new ArrayList<Environment>()
				: new ArrayList<Environment>(environmentsList);
	}

}
