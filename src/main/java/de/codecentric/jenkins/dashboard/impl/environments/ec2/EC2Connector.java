package de.codecentric.jenkins.dashboard.impl.environments.ec2;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import jenkins.model.Jenkins;

import com.amazonaws.regions.Region;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.Tag;
import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;

import de.codecentric.jenkins.dashboard.api.environments.EnvironmentInterface;
import de.codecentric.jenkins.dashboard.api.environments.EnvironmentTag;
import de.codecentric.jenkins.dashboard.api.environments.ServerEnvironment;
import de.codecentric.jenkins.dashboard.api.environments.ServerEnvironment.ENVIRONMENT_TYPES;
import de.codecentric.jenkins.dashboard.ec2.AwsKeyCredentials;
import de.codecentric.jenkins.dashboard.impl.deploy.DeployJobVariables;

/**
 * Implementation of EC2 environment integration
 * 
 * @author marcel.birkner
 * 
 */
public class EC2Connector implements EnvironmentInterface {

    public static final String JENKINS_VALUE = "JENKINS";
    public static final String STAGING_VALUE = "STAGING";
    public static final String TEST_VALUE = "TEST";
    public static final String PROD_VALUE = "PROD";

    public static final String VERSION_TAG = "Version";
    public static final String DEFAULT_INSTANCE_NAME_TAG = "Name";

    private final static Logger LOGGER = Logger.getLogger(EC2Connector.class.getName());

    private AmazonEC2 ec2;

    /**
     * Helper method to create a EC2Connector when only the credentialsId is
     * known.
     * 
     * @param credentialsId
     *            the credentialsId used to access Amazon AWS
     * @return either a connector to access AWS/EC2 or null if the credentials
     *         are not known.
     */
    public static EC2Connector getEC2Connector(final String credentialsId) {
        final DomainRequirement domain = new DomainRequirement();
        final AwsKeyCredentials credentials = CredentialsMatchers.firstOrNull(CredentialsProvider.lookupCredentials(AwsKeyCredentials.class, Jenkins.getInstance(), null, domain),
                CredentialsMatchers.withId(credentialsId));
        if (credentials == null) {
            LOGGER.warning("No credentials found for ID='" + credentialsId + "'");
            return null;
        }
        return new EC2Connector(new AmazonEC2Client(credentials.getAwsAuthCredentials()));
    }

    public EC2Connector(final AmazonEC2 ec2) {
        this.ec2 = ec2;
    }

    public boolean areAwsCredentialsValid() {
        try {
            ec2.describeInstances();
            return true;
        } catch (Exception e) {
            LOGGER.info("AWS is Invalid: " + e.getMessage());
            return false;
        }
    }

    public List<ServerEnvironment> getEnvironments(Region region) {
        List<ServerEnvironment> environments = new ArrayList<ServerEnvironment>();

        ec2.setRegion(region);
        DescribeInstancesResult instances = ec2.describeInstances();
        for (Reservation reservation : instances.getReservations()) {
            for (Instance instance : reservation.getInstances()) {
                environments.add(getEnvironmentFromInstance(instance));
            }
        }

        return environments;
    }

    public List<ServerEnvironment> getEnvironmentsByTag(Region region, String searchTag) {
        LOGGER.info("getEnvironmentsByTag " + region + " tag: " + searchTag);
        List<ServerEnvironment> environments = new ArrayList<ServerEnvironment>();

        ec2.setRegion(region);
        DescribeInstancesResult instances = ec2.describeInstances();
        for (Reservation reservation : instances.getReservations()) {
            for (Instance instance : reservation.getInstances()) {
                for (Tag tag : instance.getTags()) {
                    if (tag.getValue().equalsIgnoreCase(searchTag)) {
                        environments.add(getEnvironmentFromInstance(instance));
                    }
                }
            }
        }
        return environments;
    }

    private ServerEnvironment getEnvironmentFromInstance(Instance instance) {
        ServerEnvironment env = new ServerEnvironment(instance.getInstanceId(), instance.getInstanceType());

        List<EnvironmentTag> tags = new ArrayList<EnvironmentTag>();
        for (Tag tag : instance.getTags()) {
            EnvironmentTag envTag = new EnvironmentTag(tag.getKey(), tag.getValue());
            tags.add(envTag);
            if (tag.getKey().equalsIgnoreCase(DEFAULT_INSTANCE_NAME_TAG)) {
                env.setEnvironmentTag(tag.getValue());
                if (tag.getValue().contains(PROD_VALUE)) {
                    env.setType(ENVIRONMENT_TYPES.PRODUCTION);
                } else if (tag.getValue().contains(STAGING_VALUE)) {
                    env.setType(ENVIRONMENT_TYPES.STAGING);
                } else if (tag.getValue().contains(JENKINS_VALUE)) {
                    env.setType(ENVIRONMENT_TYPES.JENKINS);
                }
            }
            if (tag.getKey().equalsIgnoreCase(VERSION_TAG)) {
                env.setVersion(tag.getValue());
            }
        }
        env.setState(instance.getState());
        env.setLaunchTime(instance.getLaunchTime());
        env.setPublicIpAddress(instance.getPublicIpAddress());
        env.setTags(tags);
        return env;
    }

    @Override
    public boolean tagEnvironmentWithVersion(Region region, DeployJobVariables jobVariables) {
        String searchTag = jobVariables.getEnvironment();
        String version = jobVariables.getVersion();
        LOGGER.info("tagEnvironmentWithVersion " + region + " Tag " + searchTag + " version " + version);

        boolean environmentSuccessfulTagged = false;
        ec2.setRegion(region);
        DescribeInstancesResult instances = ec2.describeInstances();
        for (Reservation reservation : instances.getReservations()) {
            for (Instance instance : reservation.getInstances()) {
                for (Tag tag : instance.getTags()) {
                    if (tag.getValue().equalsIgnoreCase(searchTag)) {
                        CreateTagsRequest createTagsRequest = new CreateTagsRequest();
                        createTagsRequest.withResources(instance.getInstanceId()).withTags(new Tag(VERSION_TAG, version));
                        LOGGER.info("Create Tag " + version + " for instance " + instance.getInstanceId());
                        ec2.createTags(createTagsRequest);
                        environmentSuccessfulTagged = true;
                    }
                }
            }
        }
        return environmentSuccessfulTagged;
    }

}