package de.codecentric.jenkins.dashboard;

import hudson.Extension;
import hudson.model.Item;
import hudson.model.TopLevelItem;
import hudson.model.ViewGroup;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Cause;
import hudson.model.Descriptor;
import hudson.model.ModifiableItemGroup;
import hudson.model.ParametersAction;
import hudson.model.StringParameterValue;
import hudson.model.View;
import hudson.model.ViewDescriptor;
import hudson.model.queue.QueueTaskFuture;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import jenkins.model.Jenkins;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.bind.JavaScriptMethod;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2Client;

import de.codecentric.jenkins.dashboard.artifactrepositories.ArtifactoryConnector;
import de.codecentric.jenkins.dashboard.api.environments.ServerEnvironment;
import de.codecentric.jenkins.dashboard.api.repositories.Artifact;
import de.codecentric.jenkins.dashboard.api.repositories.RepositoryInterface;
import de.codecentric.jenkins.dashboard.impl.environments.ec2.EC2Connector;
import de.codecentric.jenkins.dashboard.impl.repositories.RepositoryTypes;
import de.codecentric.jenkins.dashboard.impl.repositories.nexus.NexusConnector;

/**
 * Central class for the dashboard view. When adding a new view to Jenkins page, this DashboardView
 * will appear. Each time this view is loaded, this class will be called.
 * 
 */
public class DashboardView extends View {

    private final static Logger LOGGER = Logger.getLogger(DashboardView.class.getName());

    @Extension
    public static final DashboardViewDescriptor DESCRIPTOR = new DashboardViewDescriptor();

    public static final String PARAM_VERSION = "VERSION";
    public static final String PARAM_ENVIRONMENT = "ENVIRONMENT";

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
    public DashboardView(final String name, final boolean showDeployField, final String groupId, final String artefactId, final List<Environment> environments) {
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
		LOGGER.info("Deploy version " + version + " to environment " + environment);
	
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
		    return String.format(Messages.DashboardView_buildJobNotFound(), buildEnvironment.getName());
		}
	
		if ((!buildJob.isBuildable()) || (!buildJob.isParameterized())) {
		    return Messages.DashboardView_deploymentCannotBeExecuted();
		}
	
		final ParametersAction versionParam = new ParametersAction(new StringParameterValue(PARAM_VERSION, version));
		final ParametersAction environmentParam = new ParametersAction(new StringParameterValue(PARAM_ENVIRONMENT, environment));
	
		List<ParametersAction> actions = Arrays.asList(versionParam, environmentParam);
		QueueTaskFuture<AbstractBuild> scheduledBuild = buildJob.scheduleBuild2(2, new Cause.UserIdCause(), actions);
	
		Result result = Result.FAILURE;
		try {
		    AbstractBuild finishedBuild = scheduledBuild.get();
		    result = finishedBuild.getResult();
		    LOGGER.info("Build finished with result: " + result + " completed in: " + finishedBuild.getDurationString() + ". ");
		} catch (Exception e) {
		    LOGGER.severe("Error while waiting for build " + scheduledBuild.toString() + ".");
		    LOGGER.severe(e.getMessage());
		    LOGGER.severe(ExceptionUtils.getFullStackTrace(e));
		    return String.format(Messages.DashboardView_buildJobFailed(), buildJob.getName());
		}
		if (result == Result.SUCCESS) {
		    return String.format(Messages.DashboardView_buildJobScheduledSuccessfully(), buildJob.getName());
		}
		return String.format(Messages.DashboardView_buildJobSchedulingFailed(), buildJob.getName());
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
     * 
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

    public String getDisplayDeployField() {
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
	if (DESCRIPTOR.getRepositoryType() == null) {
	    return new ArrayList<Artifact>();
	}

	RepositoryInterface repository;
	try {
	    repository = createRepository();
	} catch (URISyntaxException e) {
	    e.printStackTrace();
	    return new ArrayList<Artifact>();
	}

	return repository.getArtefactList(groupId, artefactId);
    }

    private RepositoryInterface createRepository() throws URISyntaxException {
	URI repositoryURI = new URI(DESCRIPTOR.getRepositoryRestUri());
	RepositoryInterface repository;

	if (DESCRIPTOR.getRepositoryType().equalsIgnoreCase(RepositoryTypes.ARTIFACTORY.getid())) {
	    repository = new ArtifactoryConnector(DESCRIPTOR.getUsername(), DESCRIPTOR.getPassword(), repositoryURI);
	} else {
	    repository = new NexusConnector(DESCRIPTOR.getUsername(), DESCRIPTOR.getPassword(), repositoryURI);
	}
	return repository;
    }

	public List<ServerEnvironment> getMatchingEC2Environments() {
		final List<ServerEnvironment> list = new ArrayList<ServerEnvironment>();
		for (Environment env : environments) {
			final EC2Connector envConn = EC2Connector.getEC2Connector(env.getCredentials());
			if (envConn == null || !envConn.areAwsCredentialsValid()) {
				LOGGER.info("Invalid credentials in environment '" + env.getName() + "'");
				continue;
			}
			List<ServerEnvironment> foundEnvironment = envConn.getEnvironmentsByTag(Region.getRegion(Regions.fromName(env.getRegion())),
                    env.getAwsInstance());
			list.addAll(foundEnvironment);
		}
		return list;
	}

    private void updateEnvironmentsWithUrlPrePostFix(List<ServerEnvironment> foundEnvironments, Environment environment) {
		for (ServerEnvironment serverEnvironment : foundEnvironments) {
		    serverEnvironment.setUrlPrefix(environment.getUrlPrefix());
		    serverEnvironment.setUrlPostfix(environment.getUrlPostfix());
		}
    }

    public List<Environment> getEnvironments() {
    	return Collections.unmodifiableList(environments);
    }

    public void setEnvironments(final List<Environment> environmentsList) {
    	this.environments = environmentsList == null ? new ArrayList<Environment>() : new ArrayList<Environment>(environmentsList);
    }

}
