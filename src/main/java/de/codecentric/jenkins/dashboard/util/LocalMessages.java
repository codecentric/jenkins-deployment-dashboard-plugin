package de.codecentric.jenkins.dashboard.util;

import java.util.ResourceBundle;

/**
 * Class provides access to the Messages {@link java.util.ResourceBundle} and hides initialization of the
 * properties file.
 */
public enum LocalMessages {

  DASHBOARD_VIEW_DISPLAYNAME("DashboardView.DisplayName");

  private final static ResourceBundle MESSAGES = ResourceBundle.getBundle("de.codecentric.jenkins.dashboard.Messages");
  private final String msgRef;


  private LocalMessages(final String msgReference) {
    msgRef = msgReference;
  }

  @Override
  public String toString() {
    return MESSAGES.getString(msgRef);
  }
  
}
