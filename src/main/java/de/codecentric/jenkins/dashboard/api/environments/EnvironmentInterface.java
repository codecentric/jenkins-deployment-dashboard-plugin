package de.codecentric.jenkins.dashboard.api.environments;

import java.util.List;

import com.amazonaws.regions.Region;

import de.codecentric.jenkins.dashboard.impl.deploy.DeployJobVariables;

/**
 * Interface for accessing environment information. EC2 instances can be accessed by region name.
 * Additionally we want to be able to get environment information for specific environments that
 * have a tag.
 * 
 * @author marcel.birkner
 * 
 */
public interface EnvironmentInterface {

    public boolean tagEnvironmentWithVersion(Region region, DeployJobVariables jobVariables);

    public List<ServerEnvironment> getEnvironments(Region region);

    public List<ServerEnvironment> getEnvironmentsByTag(Region region, String tag);

}
