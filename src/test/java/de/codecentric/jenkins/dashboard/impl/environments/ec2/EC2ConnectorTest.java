package de.codecentric.jenkins.dashboard.impl.environments.ec2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceState;
import com.amazonaws.services.ec2.model.InstanceStateName;
import com.amazonaws.services.ec2.model.InstanceType;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.Tag;

import de.codecentric.jenkins.dashboard.api.environments.ServerEnvironment;
import de.codecentric.jenkins.dashboard.api.environments.ServerEnvironment.ENVIRONMENT_TYPES;

public class EC2ConnectorTest {

    private static final AmazonEC2 amazonEC2 = mock(AmazonEC2Client.class);
    private static final DescribeInstancesResult instanceResult = mock(DescribeInstancesResult.class);
    private static final EC2Connector env = new EC2Connector(amazonEC2);

    private static Instance instance1 = new Instance().withTags(new Tag("1", "tag"), new Tag("2", "tag2"));
    private static Instance instance2 = new Instance().withTags(new Tag("7", "TAG"), new Tag("8", "TAG3"));
    private static Instance instance3 = new Instance();
    private static Reservation reservations[] = { new Reservation().withInstances(instance1, instance2, instance3) };

    @BeforeClass
    public static void setup() {
        when(amazonEC2.describeInstances()).thenReturn(instanceResult);
        when(instanceResult.getReservations()).thenReturn(Arrays.asList(reservations));
    }

    @Test
    public void testGetEnvironments() {
        EC2Connector env = new EC2Connector(amazonEC2);
        Region region = Region.getRegion(Regions.EU_WEST_1);
        List<ServerEnvironment> list = env.getEnvironments(region);
        assertThat(list.size(), is(3));
    }

    @Test
    public void testGetEnvironmentsByTag() {
        final EC2Connector env = new EC2Connector(amazonEC2);
        final Region region = Region.getRegion(Regions.EU_WEST_1);
        final List<ServerEnvironment> list = env.getEnvironmentsByTag(region, "tag");
        assertThat(list.size(), is(2));
    }

    @Test
    public void testGetEnvironmentFromInstance() throws Exception {
        final Date launchTime = new Date();
        final Instance instance = new Instance().withInstanceId("instance").withInstanceType(InstanceType.C1Xlarge).withTags(new Tag(EC2Connector.DEFAULT_INSTANCE_NAME_TAG, "unknown"))
                .withState(new InstanceState().withName(InstanceStateName.Running)).withLaunchTime(launchTime).withPublicIpAddress("127.0.0.1");
        ServerEnvironment serverEnv = Whitebox.<ServerEnvironment> invokeMethod(env, "getEnvironmentFromInstance", instance);
        assertThat(serverEnv, notNullValue());
        assertThat(serverEnv.getInstanceId(), is("instance"));
        assertThat(serverEnv.getInstanceType(), is(InstanceType.C1Xlarge.toString()));
        assertThat(serverEnv.getType(), is(ENVIRONMENT_TYPES.TEST));
        assertThat(serverEnv.getEnvironmentTag(), is("unknown"));
        assertThat(serverEnv.getState().getName(), is(InstanceStateName.Running.toString()));
        assertThat(serverEnv.getLaunchTime(), is(launchTime));
        assertThat(serverEnv.getPublicIpAddress(), is("127.0.0.1"));
    }

    @Test
    public void testGetEnvironmentFromInstanceProd() throws Exception {
        final Instance instance = new Instance().withInstanceId("instance").withInstanceType(InstanceType.C1Xlarge).withTags(new Tag(EC2Connector.DEFAULT_INSTANCE_NAME_TAG, EC2Connector.PROD_VALUE));
        ServerEnvironment serverEnv = Whitebox.<ServerEnvironment> invokeMethod(env, "getEnvironmentFromInstance", instance);
        assertThat(serverEnv, notNullValue());
        assertThat(serverEnv.getType(), is(ENVIRONMENT_TYPES.PRODUCTION));
    }

    @Test
    public void testGetEnvironmentFromInstanceStage() throws Exception {
        final Instance instance = new Instance().withInstanceId("instance").withInstanceType(InstanceType.C1Xlarge)
                .withTags(new Tag(EC2Connector.DEFAULT_INSTANCE_NAME_TAG, EC2Connector.STAGING_VALUE));
        ServerEnvironment serverEnv = Whitebox.<ServerEnvironment> invokeMethod(env, "getEnvironmentFromInstance", instance);
        assertThat(serverEnv, notNullValue());
        assertThat(serverEnv.getType(), is(ENVIRONMENT_TYPES.STAGING));
    }

    @Test
    public void testGetEnvironmentFromInstanceJenkins() throws Exception {
        final Instance instance = new Instance().withInstanceId("instance").withInstanceType(InstanceType.C1Xlarge)
                .withTags(new Tag(EC2Connector.DEFAULT_INSTANCE_NAME_TAG, EC2Connector.JENKINS_VALUE));
        ServerEnvironment serverEnv = Whitebox.<ServerEnvironment> invokeMethod(env, "getEnvironmentFromInstance", instance);
        assertThat(serverEnv, notNullValue());
        assertThat(serverEnv.getType(), is(ENVIRONMENT_TYPES.JENKINS));
    }

    @Test
    public void testGetEnvironmentFromInstanceTest() throws Exception {
        final Instance instance = new Instance().withInstanceId("instance").withInstanceType(InstanceType.C1Xlarge).withTags(new Tag(EC2Connector.DEFAULT_INSTANCE_NAME_TAG, EC2Connector.TEST_VALUE));
        ServerEnvironment serverEnv = Whitebox.<ServerEnvironment> invokeMethod(env, "getEnvironmentFromInstance", instance);
        assertThat(serverEnv, notNullValue());
        assertThat(serverEnv.getType(), is(ENVIRONMENT_TYPES.TEST));
    }

    @Test
    public void testGetEnvironmentFromInstanceVersion() throws Exception {
        final Instance instance = new Instance().withInstanceId("instance").withInstanceType(InstanceType.C1Xlarge).withTags(new Tag(EC2Connector.VERSION_TAG, "1.2.3"));
        ServerEnvironment serverEnv = Whitebox.<ServerEnvironment> invokeMethod(env, "getEnvironmentFromInstance", instance);
        assertThat(serverEnv, notNullValue());
        assertThat(serverEnv.getVersion(), is("1.2.3"));
    }

}
