package de.codecentric.jenkins.dashboard.impl.deploy;


import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class DeployJobVariablesBuilderTest {

    @Test
    public void builder() throws Exception {
        String version = "v1.0.0";
        String environment = "TEST";
        DeployJobVariables deployJob = DeployJobVariablesBuilder.createBuilder().version(version).environment(environment).build();

        assertThat(deployJob.getEnvironment(), is(environment));
        assertThat(deployJob.getVersion(), is(version));
    }

}
