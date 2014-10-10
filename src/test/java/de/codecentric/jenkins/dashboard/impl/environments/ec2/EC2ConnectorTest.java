package de.codecentric.jenkins.dashboard.impl.environments.ec2;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.util.StringUtils;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;

import de.codecentric.jenkins.dashboard.api.environments.ServerEnvironment;

/**
 * Add your AWS credentials to run these integration tests
 * 
 * @author marcel.birkner
 * 
 */
@Ignore
public class EC2ConnectorTest {

    // FILL OUT AWS CREDENTIALS BEFORE RUNNING THE INTEGRATION TESTS
    private String awsAccessKey = "";
    private String awsSecretKey = "";
    // END

    private AWSCredentials awsCredentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);

    @Test
    public void testGetEnvironments() {
	assertTrue("Please provide your AWS credentials before running the tests.", StringUtils.hasText(awsAccessKey));
	assertTrue("Please provide your AWS credentials before running the tests.", StringUtils.hasText(awsSecretKey));

	EC2Connector env = new EC2Connector(awsCredentials);
	Region region = Region.getRegion(Regions.EU_WEST_1);
	List<ServerEnvironment> list = env.getEnvironments(region);
	assertTrue(list.size() > 0);
    }

    @Test
    public void testGetEnvironmentsByTag() {
	assertTrue("Please provide your AWS credentials before running the tests.", StringUtils.hasText(awsAccessKey));
	assertTrue("Please provide your AWS credentials before running the tests.", StringUtils.hasText(awsSecretKey));

	EC2Connector env = new EC2Connector(awsCredentials);
	Region region = Region.getRegion(Regions.EU_WEST_1);
	List<ServerEnvironment> list = env.getEnvironmentsByTag(region, "ALE_PROD_A");
	assertTrue(list.size() == 1);

    }
}
