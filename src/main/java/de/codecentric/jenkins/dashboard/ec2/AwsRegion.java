package de.codecentric.jenkins.dashboard.ec2;

/**
 * Amazon EC2 Region enum. Constants for all available regions.
 * Access keys are configured on a per region basis. Therefore we
 * need to be able to select the region that matches the configured 
 * access keys. 
 *
 */
public enum AwsRegion {

    AP_NORTHEAST_1("ap-northeast-1", "Asia Pacific (Tokyo) Region"),
    AP_SOUTHEAST_1("ap-southeast-1", "Asia Pacific (Singapore) Region"),
    AP_SOUTHEAST_2("ap-southeast-2", "Asia Pacific (Sydney) Region"),
    EU_WEST_1("eu-west-1", "EU (Ireland) Region"),
    SA_EAST_1("sa-east-1", "South America (Sao Paulo) Region"),
    US_EAST_1("us-east-1", "US East (Northern Virginia) Region"),
    US_WEST_1("us-west-1", "US West (Northern California) Region"),
    US_WEST_2("us-west-2", "US West (Oregon) Region");

    private final String identifier;
    private final String name;

    private AwsRegion(final String identifier, final String name) {
        this.identifier = identifier;
        this.name = name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getName() {
        return name;
    }
}
