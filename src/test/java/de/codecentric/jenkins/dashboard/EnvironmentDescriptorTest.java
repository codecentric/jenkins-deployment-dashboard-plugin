package de.codecentric.jenkins.dashboard;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.when;
import hudson.util.ListBoxModel;

import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;

import de.codecentric.jenkins.dashboard.Environment.EnvironmentDescriptor;
import de.codecentric.jenkins.dashboard.api.environments.ServerEnvironment;
import de.codecentric.jenkins.dashboard.impl.environments.ec2.EC2Connector;

@RunWith(PowerMockRunner.class)
@PrepareForTest(EC2Connector.class)
public class EnvironmentDescriptorTest {

    private static final String REGION_NAME = "eu-west-1";
    private static final String CREDENTIAL_NAME = "credential";
    private static final EC2Connector ec2 = PowerMockito.mock(EC2Connector.class);
    private static final Region REGION = Region.getRegion(Regions.fromName(REGION_NAME));
    private static final String SERVER_TAG = "ABC";
    private static final ServerEnvironment server1 = new ServerEnvironment("id", "type");

    @BeforeClass
    public static void setup() {
        server1.setEnvironmentTag(SERVER_TAG);
        final ServerEnvironment array[] = { server1 };
        final List<ServerEnvironment> ENVS = Arrays.asList(array);

        PowerMockito.mockStatic(EC2Connector.class);
        when(EC2Connector.getEC2Connector(CREDENTIAL_NAME)).thenReturn(ec2);
        when(ec2.getEnvironments(REGION)).thenReturn(ENVS);
    }

    @Test
    public void testDoFillAwsInstanceItems() {
        final EnvironmentDescriptor desc = new Environment.EnvironmentDescriptor();
        final ListBoxModel values = desc.doFillAwsInstanceItems(REGION_NAME, CREDENTIAL_NAME);
        assertThat(values.size(), is(1));
        assertThat(values.get(0).name, is(SERVER_TAG));
        assertThat(values.get(0).value, is(SERVER_TAG));
    }

}
