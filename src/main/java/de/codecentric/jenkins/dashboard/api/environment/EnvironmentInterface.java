package de.codecentric.jenkins.dashboard.api.environment;

import java.util.List;

import com.amazonaws.regions.Region;

public interface EnvironmentInterface {

	public List<Environment> getEnvironments(Region region);
	public List<Environment> getEnvironmentsByTag(Region region, String tag);
	
}
