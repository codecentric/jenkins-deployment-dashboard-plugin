package de.codecentric.jenkins.dashboard;

import jenkins.model.Jenkins;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.Descriptor;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;

import org.kohsuke.stapler.DataBoundConstructor;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;

import de.codecentric.jenkins.dashboard.ec2.EC2Connector;
import de.codecentric.jenkins.dashboard.ec2.ServerInstance;
import de.codecentric.jenkins.dashboard.persistence.XStreamHelper;

/**
 * This {@link Builder} tags the specified environment with a given version number.
 *
 * <p>
 * When the user configures the project and enable this builder,
 * {@link DescriptorImpl#newInstance(StaplerRequest)} is invoked
 * and a new {@link EnvironmentTagBuilder} is created. The created
 * instance is persisted to the project configuration XML by using
 * XStream, so this allows you to use instance fields (like {@link #awsAccessKey})
 * to remember the configuration.
 * 
 * <p>
 * When a build is performed, the {@link #perform(Build, Launcher, BuildListener)} method
 * will be invoked. 
 *  
 * @author marcel.birkner
 *
 */
public class EnvironmentTagBuilder extends Builder {

	private String awsAccessKey;
	private String awsSecretKey;

	/**
	 * This annotation tells Hudson to call this constructor, with
	 * values from the configuration form page with matching parameter names.
	 */
	@DataBoundConstructor
	public EnvironmentTagBuilder(final String awsAccessKey, final String awsSecretKey) {
		this.awsAccessKey = awsAccessKey;
		this.awsSecretKey = awsSecretKey;
	}

	/**
	 * We'll use this from the <tt>config.jelly</tt>.
	 */
	public String getAwsAccessKey() {
		return awsAccessKey;
	}
	public String getAwsSecretKey() {
		return awsSecretKey;
	}
	
	@Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
        String environment = (String) build.getBuildVariableResolver().resolve("ENVIRONMENT");
        String version = (String) build.getBuildVariableResolver().resolve("VERSION");
        listener.getLogger().println("Tagging ENVIRONMENT [" + environment + "] with Version [" + version + "]");

        AWSCredentials awsCredentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
        EC2Connector connector = new EC2Connector(awsCredentials);
        boolean taggingSuccessful = connector.tagEnvironmentWithVersion(Region.getRegion(Regions.EU_WEST_1), environment, version);
        if( ! taggingSuccessful ) {
        	listener.getLogger().println("ERROR: Could not tag ENVIRONMENT [" + environment + "] with Version [" + version + "]");
        }
        
        ServerInstance instance = new ServerInstance(version, environment, Jenkins.getAuthentication().getName());
        XStreamHelper xstream = XStreamHelper.getInstance();
        xstream.toXML(instance);
        System.out.println("WRITE DATA TO FILE");
        
        return taggingSuccessful;
	}

    /**
     * Hudson defines a method {@link Builder#getDescriptor()}, which
     * returns the corresponding {@link Descriptor} object.
     *
     * Since we know that it's actually {@link DescriptorImpl}, override
     * the method and give a better return type, so that we can access
     * {@link DescriptorImpl} methods more easily.
     *
     * This is not necessary, but just a coding style preference.
     */
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }   
    
    /**
     * Descriptor for {@link EnvironmentTagBuilder}.
     * The class is marked as public so that it can be accessed from views.
     * See <tt>de/codecentric/jenkins/dashboard/EnvironmentTagBuilder/*.jelly</tt>
     * for the actual HTML fragment for the configuration screen.
     */
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        /**
         * To persist global configuration information,
         * simply store it in a field and call save().
         *
         * <p>
         * If you don't want fields to be persisted, use <tt>transient</tt>.
         */
        public DescriptorImpl() {
            load();
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
