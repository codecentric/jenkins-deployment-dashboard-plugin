package de.codecentric.jenkins.dashboard.ec2;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.Tag;

import de.codecentric.jenkins.dashboard.DeployJobVariables;
import de.codecentric.jenkins.dashboard.api.environment.EnvironmentInterface;
import de.codecentric.jenkins.dashboard.api.environment.ServerEnvironment;
import de.codecentric.jenkins.dashboard.api.environment.ServerEnvironment.ENVIRONMENT_TYPES;

/**
 * Implementation of EC2 environment integration
 * 
 * @author marcel.birkner
 *
 */
public class EC2Connector implements EnvironmentInterface {

    private final static Logger LOGGER = Logger.getLogger(EC2Connector.class.getName());

    private static final String DEFAULT_INSTANCE_NAME_TAG = "Name";

    private AmazonEC2 ec2;

    public EC2Connector(AWSCredentials awsCredentials) {
	this.ec2 = new AmazonEC2Client(awsCredentials);
    }

    public boolean tagEnvironmentWithVersion(Region region, DeployJobVariables jobVariables) {
	LOGGER.info("tagEnvironmentWithVersion: Region=" + region + " Environment=" + jobVariables.getEnvironment() + " Version=" + jobVariables.getVersion());

	boolean environmentSuccessfulTagged = false;
	ec2.setRegion(region);
	DescribeInstancesResult instances = ec2.describeInstances();
	for (Reservation reservation : instances.getReservations()) {
	    for (Instance instance : reservation.getInstances()) {
		for (Tag tag : instance.getTags()) {
		    if (tag.getValue().equalsIgnoreCase(jobVariables.getEnvironment())) {
			CreateTagsRequest createTagsRequest = new CreateTagsRequest();
			createTagsRequest.withResources(instance.getInstanceId()).withTags(new Tag("Version", jobVariables.getVersion()));
			LOGGER.info("Create Tag " + jobVariables.getVersion() + " for instance " + instance.getInstanceId());
			ec2.createTags(createTagsRequest);
			environmentSuccessfulTagged = true;
		    }
		}
	    }
	}
	return environmentSuccessfulTagged;
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

	List<de.codecentric.jenkins.dashboard.api.environment.Tag> tags = new ArrayList<de.codecentric.jenkins.dashboard.api.environment.Tag>();
	for (Tag tag : instance.getTags()) {
	    de.codecentric.jenkins.dashboard.api.environment.Tag envTag = new de.codecentric.jenkins.dashboard.api.environment.Tag(tag.getKey(), tag.getValue());
	    tags.add(envTag);
	    if (tag.getKey().equalsIgnoreCase(DEFAULT_INSTANCE_NAME_TAG)) {
		env.setEnvironmentTag(tag.getValue());
		if (tag.getValue().contains("PROD")) {
		    env.setType(ENVIRONMENT_TYPES.PRODUCTION);
		} else if (tag.getValue().contains("STAGING")) {
		    env.setType(ENVIRONMENT_TYPES.STAGING);
		} else if (tag.getValue().contains("JENKINS")) {
		    env.setType(ENVIRONMENT_TYPES.JENKINS);
		}
	    }
	    if (tag.getKey().equalsIgnoreCase("Version")) {
		env.setVersion(tag.getValue());
	    }
	}
	env.setState(instance.getState());
	env.setLaunchTime(instance.getLaunchTime());
	env.setPublicIpAddress(instance.getPublicIpAddress());
	env.setTags(tags);
	return env;
    }

}