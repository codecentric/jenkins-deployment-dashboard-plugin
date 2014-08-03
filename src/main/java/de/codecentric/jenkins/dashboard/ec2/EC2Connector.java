package de.codecentric.jenkins.dashboard.ec2;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.regions.Region;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.Tag;

import de.codecentric.jenkins.dashboard.environmentapi.Environment;
import de.codecentric.jenkins.dashboard.environmentapi.EnvironmentInterface;

public class EC2Connector implements EnvironmentInterface {

	private static final String DEFAULT_INSTANCE_NAME_TAG = "Name";
	
	public List<Environment> getEnvironments(Region region) {
		List<Environment> environments = new ArrayList<Environment>();
		
		AmazonEC2 ec2 = new AmazonEC2Client();
		ec2.setRegion(region);
		DescribeInstancesResult instances = ec2.describeInstances();
		for (Reservation reservation : instances.getReservations()) {
			for (Instance instance :  reservation.getInstances()) {
				environments.add(getEnvironmentFromInstance(instance));
			}
		}
		
		return environments;
	}

	public List<Environment> getEnvironmentsByTag(Region region, String searchTag) {
		List<Environment> environments = new ArrayList<Environment>();
		AmazonEC2 ec2 = new AmazonEC2Client();
		ec2.setRegion(region);
		DescribeInstancesResult instances = ec2.describeInstances();
		for (Reservation reservation : instances.getReservations()) {
			for (Instance instance :  reservation.getInstances()) {
				for (Tag tag : instance.getTags()) {
					if( tag.getValue().equalsIgnoreCase(searchTag)) {
						environments.add( getEnvironmentFromInstance(instance) );
					}
				}
			}
		}
		return environments;
	}

	private Environment getEnvironmentFromInstance(Instance instance) {
		Environment env = new Environment(instance.getInstanceId(), instance.getInstanceType());
		List<de.codecentric.jenkins.dashboard.environmentapi.Tag> tags = new ArrayList<de.codecentric.jenkins.dashboard.environmentapi.Tag>();
		for (Tag tag : instance.getTags()) {
			de.codecentric.jenkins.dashboard.environmentapi.Tag envTag = new de.codecentric.jenkins.dashboard.environmentapi.Tag(tag.getKey(), tag.getValue());
			tags.add(envTag);
			if( tag.getKey().equalsIgnoreCase(DEFAULT_INSTANCE_NAME_TAG)) {
				env.setEnvironmentTag(tag.getValue());
			}
		}
		env.setTags(tags);
		return env;
	}
}