package de.codecentric.jenkins.dashboard.ec2;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.junit.Ignore;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;

import de.codecentric.jenkins.dashboard.environmentapi.Environment;

/**
 * Please add your AWS credentials to your user home to successfully run these unit tests 
 * 
 * $USER_HOME/.aws/credentials 
 *  
 * [default]
 *  aws_access_key_id = <KEY_ID>
 *  aws_secret_access_key = <ACCESS_KEY>
 * 
 * @author marcel.birkner
 * 
 */
@Ignore
public class EC2ConnectorTest {
	
	@Test
	public void testGetEnvironments() {
		EC2Connector env = new EC2Connector();
		Region region = Region.getRegion(Regions.EU_WEST_1);
		List<Environment> list = env.getEnvironments(region);
		assertTrue(list.size() > 0);
	}

	@Test
	public void testGetEnvironmentsByTag() {
		EC2Connector env = new EC2Connector();
		Region region = Region.getRegion(Regions.EU_WEST_1);
		List<Environment> list = env.getEnvironmentsByTag(region, "ALE_PROD_A");
		assertTrue(list.size() == 1);

	}

}
