package de.codecentric.jenkins.dashboard;

import hudson.model.Descriptor;
import hudson.model.ViewDescriptor;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;

import java.net.URI;
import java.util.List;
import java.util.logging.Logger;

import jenkins.model.Jenkins;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.springframework.util.StringUtils;

import de.codecentric.jenkins.dashboard.artifactrepositories.ArtifactoryConnector;

/**
 * Descriptor for the Dashboard View. This descriptor object contains the metadata about the Dashboard View.
 *
 * The descriptor contains access to a repository and credentials to access an AWS/EC2 environment. This can be either explicit
 * key/secret or globally defined AwsKeyCredentials.
 */
public final class DashboardViewDescriptor extends ViewDescriptor {

    private final static Logger LOGGER = Logger.getLogger(DashboardViewDescriptor.class.getName());

    private String repositoryType;
    private String repositoryRestUri = "";
    private String username = "";
    private String password = "";

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
