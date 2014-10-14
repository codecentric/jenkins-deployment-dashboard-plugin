package de.codecentric.jenkins.dashboard.ec2;

import hudson.Extension;
import hudson.util.Secret;

import javax.annotation.Nonnull;

import org.kohsuke.stapler.DataBoundConstructor;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.cloudbees.plugins.credentials.common.StandardCredentials;
import com.cloudbees.plugins.credentials.impl.BaseStandardCredentials;
import com.cloudbees.plugins.credentials.Credentials;
import com.cloudbees.plugins.credentials.CredentialsDescriptor;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.NameWith;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import de.codecentric.jenkins.dashboard.Messages;

@NameWith(value = StandardCredentials.NameProvider.class, priority = -16)
public class AwsKeyCredentials extends BaseStandardCredentials {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7732948788576624160L;
  
	public String key;
	
	public Secret secret;
	
    @DataBoundConstructor public AwsKeyCredentials(@CheckForNull CredentialsScope scope, @CheckForNull String id, 
    		@CheckForNull String description, @Nonnull String key,  @Nonnull Secret secret) {
    	super(scope, id != null ? id : key, description);
    	this.key = key;
    	this.secret = secret;
    }

    public AWSCredentials getAwsAuthCredentials() {
    	return new BasicAWSCredentials(key, secret.getPlainText());
    }
    
    @Extension 
    public static class AwsKeyCredentialsDescriptor extends CredentialsDescriptor {

    	
        public AwsKeyCredentialsDescriptor(Class<? extends Credentials> clazz) {
            super(clazz);
        }

        public AwsKeyCredentialsDescriptor() {
        }

        @Override 
        public String getDisplayName() {
            return Messages.AwsKeyCredentials_name();
        }

    }
 }
