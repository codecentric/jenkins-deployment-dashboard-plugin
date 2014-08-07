package de.codecentric.jenkins.dashboard.api.environment;

import java.util.List;

import com.amazonaws.regions.Region;

public interface EnvironmentInterface {

	public List<ServerEnvironment> getEnvironments(Region region);
	public List<ServerEnvironment> getEnvironmentsByTag(Region region, String tag);
	
}
