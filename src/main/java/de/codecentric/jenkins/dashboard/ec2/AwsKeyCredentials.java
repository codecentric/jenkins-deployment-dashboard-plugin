package de.codecentric.jenkins.dashboard.ec2;

import hudson.Extension;
import hudson.util.FormValidation;
import hudson.util.Secret;

import java.util.logging.Logger;

import javax.annotation.Nonnull;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.springframework.util.StringUtils;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.cloudbees.plugins.credentials.Credentials;
import com.cloudbees.plugins.credentials.CredentialsDescriptor;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.NameWith;
import com.cloudbees.plugins.credentials.common.StandardCredentials;
import com.cloudbees.plugins.credentials.impl.BaseStandardCredentials;

import de.codecentric.jenkins.dashboard.Messages;
import de.codecentric.jenkins.dashboard.impl.environments.ec2.EC2Connector;
import edu.umd.cs.findbugs.annotations.CheckForNull;

@NameWith(value = StandardCredentials.NameProvider.class, priority = -16)
public class AwsKeyCredentials extends BaseStandardCredentials {

    private final static Logger LOGGER = Logger.getLogger(AwsKeyCredentials.class.getName());

    private static final long serialVersionUID = 7732948788576624160L;

    public String key;

    public Secret secret;

    @DataBoundConstructor
    public AwsKeyCredentials(@CheckForNull CredentialsScope scope, @CheckForNull String id, @CheckForNull String description, @Nonnull String key, @Nonnull Secret secret) {
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
            // required
        }

        @Override
        public String getDisplayName() {
            return Messages.AwsKeyCredentials_name();
        }

        public FormValidation doTestAwsConnection(@QueryParameter("key") final String accessKey, @QueryParameter("secret") final Secret secretKey) {
            LOGGER.info("Verify AWS connection key " + accessKey);

            FormValidation validationResult;
            try {
                final AWSCredentials awsCredentials = createCredentials(accessKey, secretKey.getPlainText());
                final EC2Connector conn = new EC2Connector(new AmazonEC2Client(awsCredentials));
                validationResult = conn.areAwsCredentialsValid() ? FormValidation.ok(Messages.AwsKeyCredentials_awsConnectionSuccessful()) : FormValidation.warning(Messages
                        .AwsKeyCredentials_awsConnectionFailed());

            } catch (Exception e) {
                LOGGER.severe(e.getMessage());
                validationResult = FormValidation.error(Messages.AwsKeyCredentials_awsConnectionCritical() + e.getMessage());
            }
            return validationResult;
        }

        /**
         * create AWS-credentials from either access/secret-strings or global
         * credentials Explicit strings have priority above credentials
         * 
         * @param accessKey AWS access key
         * @param secretKey AWS secret key
         * @return a credentials-object or null if no valid credentials where
         *         provided
         */
        protected AWSCredentials createCredentials(final String accessKey, final String secretKey) {
            AWSCredentials result = null;
            if (StringUtils.hasText(accessKey) && StringUtils.hasText(secretKey)) {
                result = new BasicAWSCredentials(accessKey, secretKey);
            }
            return result;
        }

    }
}
