package de.codecentric.jenkins.dashboard;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Cause;
import hudson.model.Item;
import hudson.model.ParametersAction;
import hudson.model.StringParameterValue;
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


import net.sf.json.JSONObject;


import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;


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
import de.codecentric.jenkins.dashboard.api.repository.RepositoryInterface;
import de.codecentric.jenkins.dashboard.artifactrepositories.ArtifactoryConnector;
import de.codecentric.jenkins.dashboard.artifactrepositories.NexusConnector;
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
	private String artefactId = "";
	private List<Environment> environments;
	
	private String awsAccessKey = "";
	private String awsSecretKey = "";
    private String awsRegion = "";
	
	public DashboardView(final String name) {
		super(name);
	}

	public DashboardView(final String name, final ViewGroup owner) {
		super(name, owner);
	}

    @DataBoundConstructor
    public DashboardView(
            final String name, final boolean showDeployField,
            final String awsAccessKey, final String awsSecretKey, final String awsRegion,
            final String artefactId, final List<Environment> environments) {
        this(name);
        setShowDeployField(showDeployField);
        setAwsAccessKey(awsAccessKey);
        setAwsSecretKey(awsSecretKey);
        setAwsRegion(awsRegion);
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

        final ParametersAction params = new ParametersAction(new StringParameterValue("version", version));

        // TODO change to using 'scheduleBuild2' which will return a Future object so we can wait for completion.
        final boolean schedulingSuccessful = buildJob.scheduleBuild(2, new Cause.UserIdCause(), params);

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

	public List<Artifact> getArtifacts() {
		LOGGER.info("Getting artifacts for " + DESCRIPTOR.getRepositoryType());

		RepositoryInterface repository;
		try {
			repository = createRepository();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return new ArrayList<Artifact>();
		}
		
		List<Artifact> versions = repository.getArtefactList(artefactId);
		return versions;
	}

	private RepositoryInterface createRepository() throws URISyntaxException {
		URI repositoryURI;
		repositoryURI = new URI(DESCRIPTOR.getRepositoryRestUri());
		RepositoryInterface repository;
		if( DESCRIPTOR.getRepositoryType().equalsIgnoreCase( RepositoryType.ARTIFACTORY.getid() )) {
			repository = new ArtifactoryConnector(DESCRIPTOR.getUsername(), DESCRIPTOR.getPassword(), repositoryURI);
		} else {
			repository = new NexusConnector(DESCRIPTOR.getUsername(), DESCRIPTOR.getPassword(), repositoryURI);
		}
		return repository;
	}

	public List<ServerEnvironment> getMatchingEC2Environments() {
		final AWSCredentials awsCredentials = new BasicAWSCredentials(getAwsAccessKey(), getAwsSecretKey());
		final EC2Connector env = new EC2Connector(awsCredentials);
		
		if (! env.areAwsCredentialsValid()) {
			System.out.println("AWS Credentials are invalid");
			return new ArrayList<ServerEnvironment>();
		}
		
		final List<ServerEnvironment> list = new ArrayList<ServerEnvironment>();
		for (Environment envTag : environments) {
			List<ServerEnvironment> foundEnvironment = env.getEnvironmentsByTag(Region.getRegion(Regions.fromName(awsRegion)), envTag.getAwsInstance());
			list.addAll(foundEnvironment);
		}
		return list;
	}
	
	public List<ServerEnvironment> getAllEC2Environments() {
		final AWSCredentials awsCredentials = new BasicAWSCredentials(getAwsAccessKey(), getAwsSecretKey());
		final EC2Connector env = new EC2Connector(awsCredentials);

		if (! env.areAwsCredentialsValid()) {
			System.out.println("AWS Credentials are invalid");
			return new ArrayList<ServerEnvironment>();
		}

        return env.getEnvironments(Region.getRegion(Regions.fromName(awsRegion)));
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

    public String getAwsRegion() {
        return awsRegion;
    }

    public void setAwsRegion(final String awsRegion) {
        this.awsRegion = awsRegion;
    }

    public static enum AwsRegion {
        AP_NORTHEAST_1("ap-northeast-1", "Asia Pacific (Tokyo) Region"),
        AP_SOUTHEAST_1("ap-southeast-1", "Asia Pacific (Singapore) Region"),
        AP_SOUTHEAST_2("ap-southeast-2", "Asia Pacific (Sydney) Region"),
        EU_WEST_1("eu-west-1", "EU (Ireland) Region"),
        SA_EAST_1("sa-east-1", "South America (Sao Paulo) Region"),
        US_EAST_1("us-east-1", "US East (Northern Virginia) Region"),
        US_WEST_1("us-west-1", "US West (Northern California) Region"),
        US_WEST_2("us-west-2", "US West (Oregon) Region");

        private final String identifier;
        private final String name;

        private AwsRegion(final String identifier, final String name) {
            this.identifier = identifier;
            this.name = name;
        }
    }

    public static final class DescriptorImpl extends ViewDescriptor {

        private String repositoryType;
        private String repositoryRestUri = "";
        private String username = "";
        private String password = "";

        public DescriptorImpl() {
            super();
        }

		@Override
		public String getDisplayName() {
			return Messages.DashboardView_DisplayName();
		}

		public List<EnvironmentDescriptor> getEnvironmentDescriptors() {
			return Jenkins.getInstance().getDescriptorList(Environment.class);
		}

		@Override
		public String getHelpFile() {
			return "/plugin/jenkins-deployment-dashboard-plugin/help.html";
		}

        public ListBoxModel doFillRepositoryTypeItems() {
            final ListBoxModel model = new ListBoxModel();

            for (RepositoryType value : RepositoryType.values()) {
                model.add(value.getDescription(), value.getid());
            }

            return model;
        }

        public ListBoxModel doFillAwsRegionItems() {
            final ListBoxModel model = new ListBoxModel();

            for (AwsRegion value : AwsRegion.values()) {
                model.add(value.name, value.identifier);
            }

            return model;
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

		public FormValidation doTestRepositoryConnection(
				@QueryParameter("repositoryRestUri") final String repositoryRestUri,
				@QueryParameter("username") final String username,
				@QueryParameter("password") final String password) {

			LOGGER.info("Verify Repository connection for URI " + repositoryRestUri);

			FormValidation validationResult;
			try {
				URI repositoryURI = new URI(repositoryRestUri);
				ArtifactoryConnector repository = new ArtifactoryConnector(username, password, repositoryURI);
				LOGGER.info("Artifactory config valid? " + repository.canConnect());
				if (repository.canConnect()) {
					validationResult = FormValidation.ok(Messages.DashboardView_artifactoryConnectionSuccessful());
				} else {
					validationResult = FormValidation.warning(Messages.DashboardView_artifactoryConnectionFailed() + repositoryRestUri);
				}

			} catch (Exception e) {
				LOGGER.severe(e.getMessage());
				validationResult = FormValidation.error(Messages.DashboardView_artifactoryConnectionCritical() + e.getMessage());
			}

			return validationResult;
		}

        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws Descriptor.FormException {
            req.bindJSON(this, json.getJSONObject("deployment-dashboard"));
            save();
            return true;
        }

        public String getRepositoryType() {
            return repositoryType;
        }

        public void setRepositoryType(String repositoryType) {
            this.repositoryType = repositoryType;
        }

        public String getRepositoryRestUri() {
            return repositoryRestUri;
        }

        public void setRepositoryRestUri(final String repositoryRestUri) {
            this.repositoryRestUri = repositoryRestUri;
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

    }
}
