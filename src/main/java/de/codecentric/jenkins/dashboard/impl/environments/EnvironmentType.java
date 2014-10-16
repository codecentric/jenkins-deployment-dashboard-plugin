package de.codecentric.jenkins.dashboard.impl.environments;

import org.jvnet.localizer.Localizable;

import de.codecentric.jenkins.dashboard.Messages;

/**
 * Enum for all available Environment Types that can be configured on the Dashboard View
 * configuration page.
 */
public enum EnvironmentType {

    TEST(Messages._EnvironmentType_test()), //
    STAGING(Messages._EnvironmentType_staging()), //
    PRODUCTION(Messages._EnvironmentType_production());

    private Localizable localizable;

    private EnvironmentType(Localizable localizerId) {
	this.localizable = localizerId;
    }

    public String getDescription() {
	return localizable.toString();
    }

}
