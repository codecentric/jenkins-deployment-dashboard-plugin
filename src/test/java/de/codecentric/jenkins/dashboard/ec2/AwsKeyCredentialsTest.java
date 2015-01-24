package de.codecentric.jenkins.dashboard.ec2;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.mock;
import hudson.util.Secret;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.amazonaws.auth.AWSCredentials;
import com.cloudbees.plugins.credentials.CredentialsScope;

import de.codecentric.jenkins.dashboard.ec2.AwsKeyCredentials.AwsKeyCredentialsDescriptor;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Secret.class)
public class AwsKeyCredentialsTest {

    private static final String ID = "abc";
    private static final String DESC = "a description";
    private static final String ACCESS = "myAccess";
    private static final String SECRET_VALUE = "mySecret";
    private static final Secret SECRET = mock(Secret.class);

    private static final AwsKeyCredentialsDescriptor descriptor = new AwsKeyCredentialsDescriptor();

    @BeforeClass
    static public void setup() {
        Mockito.when(SECRET.getPlainText()).thenReturn(SECRET_VALUE);
    }

    @Test
    public void testAwsKeyCredentials() {
        final AwsKeyCredentials credentials = new AwsKeyCredentials(CredentialsScope.GLOBAL, ID, DESC, ACCESS, SECRET);
        assertThat(credentials.key, is(ACCESS));
        assertThat(credentials.secret, is(SECRET));
        assertThat(credentials.getId(), is(ID));
        assertThat(credentials.getScope(), is(CredentialsScope.GLOBAL));
    }

    @Test
    public void testAwsKeyCredentialsWithIdNull() {
        final AwsKeyCredentials credentials = new AwsKeyCredentials(CredentialsScope.GLOBAL, null, DESC, ACCESS, SECRET);
        assertThat(credentials.key, is(ACCESS));
        assertThat(credentials.secret, is(SECRET));
        assertThat(credentials.getId(), is(ACCESS));
        assertThat(credentials.getScope(), is(CredentialsScope.GLOBAL));
    }

    @Test
    public void testGetAwsAuthCredentials() {
        final AwsKeyCredentials credentials = new AwsKeyCredentials(CredentialsScope.GLOBAL, null, DESC, ACCESS, SECRET);
        final AWSCredentials awsAuthCredentials = credentials.getAwsAuthCredentials();
        assertThat(awsAuthCredentials, notNullValue());
        assertThat(awsAuthCredentials.getAWSAccessKeyId(), is(ACCESS));
        assertThat(awsAuthCredentials.getAWSSecretKey(), is(SECRET_VALUE));
    }

    @Test
    public void testCreateCredentialsAccessSecret() {

        AWSCredentials credentials = descriptor.createCredentials(ACCESS, SECRET_VALUE);
        assertThat(credentials, notNullValue());
        assertThat(credentials.getAWSAccessKeyId(), is(ACCESS));
        assertThat(credentials.getAWSSecretKey(), is(SECRET_VALUE));
    }

    @Test
    public void testCreateCredentialsMissingData() {
        AWSCredentials credentials = descriptor.createCredentials(null, null);
        assertThat(credentials, nullValue());
    }

    @Test
    public void testCreateCredentialsMissingSecret() {
        AWSCredentials credentials = descriptor.createCredentials(ACCESS, "");
        assertThat(credentials, nullValue());
    }

    @Test
    public void testCreateCredentialsMissingAccess() {
        AWSCredentials credentials = descriptor.createCredentials(" ", SECRET_VALUE);
        assertThat(credentials, nullValue());
    }

}
