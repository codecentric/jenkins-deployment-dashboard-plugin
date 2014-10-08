package de.codecentric.jenkins.dashboard;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.springframework.util.StringUtils;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;

import de.codecentric.jenkins.dashboard.api.environment.ServerEnvironment;
import de.codecentric.jenkins.dashboard.artifactrepositories.ArtifactoryConnector;
import de.codecentric.jenkins.dashboard.ec2.AwsRegion;
import de.codecentric.jenkins.dashboard.ec2.EC2Connector;
import hudson.model.Descriptor;
import hudson.model.ViewDescriptor;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;

/**
 * Descriptor for the Dashboard View. This descriptor object contains the metadata about the Dashboard View.
 *
 */
public final class DashboardViewDescriptor extends ViewDescriptor {

    private final static Logger LOGGER = Logger.getLogger(DashboardViewDescriptor.class.getName());

    private String repositoryType;
    private String repositoryRestUri = "";
    private String username = "";
    private String password = "";

    private String awsAccessKey = "";
    private String awsSecretKey = "";
    private String awsRegion = "";

    public DashboardViewDescriptor() {
        super(DashboardView.class); // Have to provide the original class because there is no enclosing class
        load();
    }

    @Override
    public String getDisplayName() {
        return Messages.DashboardView_DisplayName();
    }

    public List<Environment.EnvironmentDescriptor> getEnvironmentDescriptors() {
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
            model.add(value.getName(), value.getIdentifier());
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

    public FormValidation doTestAwsConnection(
            @QueryParameter("awsAccessKey") final String accessKey,
            @QueryParameter("awsSecretKey") final String secretKey,
            @QueryParameter("awsRegion") final String region) {

        LOGGER.info("Verify AWS connection key " + accessKey + " in region " + region);

        FormValidation validationResult;
        try {
        	final AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
            final EC2Connector conn = new EC2Connector(awsCredentials);
            if (conn.areAwsCredentialsValid()) {
                validationResult = FormValidation.ok(Messages.DashboardView_awsConnectionSuccessful());
            } else {
                validationResult = FormValidation.warning(Messages.DashboardView_awsConnectionFailed());
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

    public List<ServerEnvironment> getAllEC2Environments() {
        final AWSCredentials awsCredentials = new BasicAWSCredentials(getAwsAccessKey(), getAwsSecretKey());
        final EC2Connector env = new EC2Connector(awsCredentials);

        if (! env.areAwsCredentialsValid()) {
            System.out.println("AWS Credentials are invalid");
            return new ArrayList<ServerEnvironment>();
        }

        return env.getEnvironments(Region.getRegion(Regions.fromName(getAwsRegion())));
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

}
