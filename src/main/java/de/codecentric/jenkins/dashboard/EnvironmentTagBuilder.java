package de.codecentric.jenkins.dashboard;

import hudson.Extension;
import hudson.Launcher;
import hudson.Util;
import hudson.model.BuildListener;
import hudson.model.ParameterValue;
import hudson.model.AbstractBuild;
import hudson.model.Descriptor;
import hudson.model.ParametersAction;
import hudson.security.ACL;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.ListBoxModel;

import java.util.List;

import jenkins.model.Jenkins;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;

import de.codecentric.jenkins.dashboard.ec2.AwsKeyCredentials;
import de.codecentric.jenkins.dashboard.impl.deploy.DeployJobVariables;
import de.codecentric.jenkins.dashboard.impl.deploy.DeployJobVariablesBuilder;
import de.codecentric.jenkins.dashboard.impl.environments.ec2.EC2Connector;

/**
 * This {@link Builder} tags the specified environment with a given version
 * number.
 * 
 * <p>
 * When the user configures the project and enable this builder,
 * {@link DescriptorImpl#newInstance(StaplerRequest)} is invoked and a new
 * {@link EnvironmentTagBuilder} is created. The created instance is persisted
 * to the project configuration XML by using XStream, so this allows you to use
 * instance fields (like {@link #awsAccessKey}) to remember the configuration.
 * </p>
 * 
 * <p>
 * When a build is performed, the
 * {@link #perform(Build, Launcher, BuildListener)} method will be invoked.
 * </p>
 * 
 * @author marcel.birkner
 * 
 */
public class EnvironmentTagBuilder extends Builder {

    private String credentials;
    
    /**
     * This annotation tells Hudson to call this constructor, with values from
     * the configuration form page with matching parameter names.
     */
    @DataBoundConstructor
    public EnvironmentTagBuilder(final String credentials) {
        this.setCredentials(credentials);
    }

    public String getCredentials() {
        return credentials;
    }

    public void setCredentials(String credentials) {
        this.credentials = credentials;
    }

    /**
     * We'll use this from the <tt>config.jelly</tt>.
     */

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
        DeployJobVariables jobVariables = extractDeployJobVariables(build);

        String message = "Tagging ENVIRONMENT [" + jobVariables.getEnvironment() + "] with VERSION [" + jobVariables.getVersion() + "]";
        listener.getLogger().println(message);

        EC2Connector connector = EC2Connector.getEC2Connector(getCredentials());
        boolean taggingSuccessful = connector.tagEnvironmentWithVersion(Region.getRegion(Regions.EU_WEST_1), jobVariables);
        if (!taggingSuccessful) {
            String failedMessage = "ERROR: Could not tag ENVIRONMENT [" + jobVariables.getEnvironment() + "] with VERSION [" + jobVariables.getVersion() + "]";
            listener.getLogger().println(failedMessage);
        }

        return taggingSuccessful;
    }

    private DeployJobVariables extractDeployJobVariables(AbstractBuild build) {
        String environment = DeployJobVariablesBuilder.UNDEFINED;
        String version = DeployJobVariablesBuilder.UNDEFINED;
        List<ParametersAction> actionList = Util.filter(build.getAllActions(), ParametersAction.class);
        for (ParametersAction parametersAction : actionList) {
            List<ParameterValue> params = parametersAction.getParameters();
            for (ParameterValue parameterValue : params) {
                if (DashboardView.PARAM_ENVIRONMENT.equalsIgnoreCase((String) parameterValue.getName())) {
                    environment = (String) parameterValue.getValue();
                }
                if (DashboardView.PARAM_VERSION.equalsIgnoreCase((String) parameterValue.getName())) {
                    version = (String) parameterValue.getValue();
                }
            }
        }
        return DeployJobVariablesBuilder.createBuilder().version(version).environment(environment).build();
    }

    /**
     * Hudson defines a method {@link Builder#getDescriptor()}, which returns
     * the corresponding {@link Descriptor} object.
     * 
     * Since we know that it's actually {@link DescriptorImpl}, override the
     * method and give a better return type, so that we can access
     * {@link DescriptorImpl} methods more easily.
     * 
     * This is not necessary, but just a coding style preference.
     */
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    /**
     * Descriptor for {@link EnvironmentTagBuilder}. The class is marked as
     * public so that it can be accessed from views. See
     * <tt>de/codecentric/jenkins/dashboard/EnvironmentTagBuilder/*.jelly</tt>
     * for the actual HTML fragment for the configuration screen.
     */
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        /**
         * To persist global configuration information, simply store it in a
         * field and call save().
         * 
         * <p>
         * If you don't want fields to be persisted, use <tt>transient</tt>.
         */
        public DescriptorImpl() {
            load();
        }

        public ListBoxModel doFillCredentialsItems() {
            final ListBoxModel model = new ListBoxModel();

            DomainRequirement domain = new DomainRequirement();
            for (AwsKeyCredentials credentials : CredentialsProvider.lookupCredentials(AwsKeyCredentials.class, Jenkins.getInstance(), ACL.SYSTEM, domain)) {
                model.add(credentials.getId());
            }
            return model;
        }
        
        /**
         * This human readable name is used in the configuration screen.
         */
        @Override
        public String getDisplayName() {
            return Messages.Environment_DisplayName();
        }

        /**
         * Applicable to any kind of project.
         */
        @Override
        public boolean isApplicable(Class type) {
            return true;
        }
    }

}
