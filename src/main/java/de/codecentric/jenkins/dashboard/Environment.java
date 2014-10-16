package de.codecentric.jenkins.dashboard;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.security.ACL;
import hudson.util.ComboBoxModel;
import hudson.util.ListBoxModel;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nonnull;

import jenkins.model.Jenkins;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;

import de.codecentric.jenkins.dashboard.api.environments.ServerEnvironment;
import de.codecentric.jenkins.dashboard.ec2.AwsKeyCredentials;
import de.codecentric.jenkins.dashboard.impl.environments.EnvironmentType;
import de.codecentric.jenkins.dashboard.impl.environments.ec2.AwsRegion;
import de.codecentric.jenkins.dashboard.impl.environments.ec2.EC2Connector;

/**
 * Describes the environment configuration.
 */
public class Environment extends AbstractDescribableImpl<Environment> {

    private final static Logger LOGGER = Logger.getLogger(Environment.class.getName());

    @Extension
    public static final EnvironmentDescriptor DESCRIPTOR = new EnvironmentDescriptor();

    private String name;
    private String urlPrefix;
    private String urlPostfix;
    private EnvironmentType environmentType;
    private String awsInstance;
    private String region;
    private String credentials;
    private String buildJob;

    @DataBoundConstructor
    public Environment(@Nonnull final String name, final String urlPrefix, final String urlPostfix, @Nonnull final String credentials, @Nonnull final String region, @Nonnull final String environmentType, final String awsInstance, final String buildJob) {
        LOGGER.info("New environment created: " + credentials + ", " + region);
    	setName(name);
        setCredentials(credentials);
        setRegion(region);
        setEnvironmentType(environmentType);
        setAwsInstance(awsInstance);
        setBuildJob(buildJob);
    	setUrlPostfix(urlPostfix);
    	setUrlPrefix(urlPrefix);
    }

    public String getName() {
    	return name;
    }

    public void setName(final String name) {
    	this.name = name;
    }

    public String getEnvironmentType() {
    	return environmentType.name();
    }

    public void setEnvironmentType(final String environmentType) {
    	this.environmentType = EnvironmentType.valueOf(environmentType);
    }

    public String getAwsInstance() {
    	return awsInstance;
    }

    public void setAwsInstance(final String awsInstance) {
    	this.awsInstance = awsInstance;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(final String region) {
        this.region = region;
    }

    public String getCredentials() {
        return credentials;
    }

    public void setCredentials(final String credentials) {
        this.credentials = credentials;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public Descriptor getDescriptor() {
    	return DESCRIPTOR;
    }

    public String getBuildJob() {
    	return buildJob;
    }

    public void setBuildJob(final String buildJob) {
    	this.buildJob = buildJob;
    }

    @Extension
    public static class EnvironmentDescriptor extends Descriptor<Environment> {
        public String getDisplayName() {
            return Messages.Environment_DisplayName();
        }

        public ListBoxModel doFillEnvironmentTypeItems() {
            ListBoxModel model = new ListBoxModel();

            for (EnvironmentType value : EnvironmentType.values()) {
                model.add(value.getDescription(), value.name());
            }

            return model;
        }

        public ListBoxModel doFillCredentialsItems() {
        	final ListBoxModel model = new ListBoxModel();
        	
        	DomainRequirement domain = new DomainRequirement();
        	for (AwsKeyCredentials credentials : CredentialsProvider.lookupCredentials(AwsKeyCredentials.class, Jenkins.getInstance(), ACL.SYSTEM, domain)) {
        		model.add(credentials.getId());
        	}
        	return model;
        }
        
        public ListBoxModel doFillAwsInstanceItems(@QueryParameter String region, @QueryParameter String credentials) {
            final ListBoxModel model = new ListBoxModel();

            LOGGER.info("Looking for instances in " + region);
            if (StringUtils.isBlank(region)) {
            	LOGGER.info("Region is empty");
            	return model;
            }
            for (ServerEnvironment env : getEC2Instances(region, credentials)) {
                model.add(env.getInstanceId());
            }

            return model;
        }

        private List<ServerEnvironment> getEC2Instances(String region, String credentialsId) {
        	final EC2Connector ec2 = EC2Connector.getEC2Connector(credentialsId);
        	if (ec2 == null)
        		return Collections.<ServerEnvironment>emptyList();
        	return ec2.getEnvironments(Region.getRegion(Regions.fromName(region)));
		}

		public ListBoxModel doFillRegionItems() {
           final ListBoxModel model = new ListBoxModel();

            for (AwsRegion value : AwsRegion.values()) {
                model.add(value.getName(), value.getIdentifier());
            }

            return model;
        }
               
        public ComboBoxModel doFillBuildJobItems() {
            ComboBoxModel model = new ComboBoxModel();
            
            for (String jobName : Jenkins.getInstance().getJobNames()) {
                model.add(jobName);
            }

            return model;
        }
    }

    public String getUrlPrefix() {
    	return urlPrefix;
    }

    public void setUrlPrefix(String urlPrefix) {
	this.urlPrefix = urlPrefix;
    }

    public String getUrlPostfix() {
	return urlPostfix;
    }

    public void setUrlPostfix(String urlPostfix) {
	this.urlPostfix = urlPostfix;
    }

}
