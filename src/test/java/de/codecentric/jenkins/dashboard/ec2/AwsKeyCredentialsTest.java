package de.codecentric.jenkins.dashboard.ec2;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import hudson.util.Secret;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.powermock.api.mockito.PowerMockito.mock;

import com.amazonaws.auth.AWSCredentials;
import com.cloudbees.plugins.credentials.CredentialsScope;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Secret.class)
public class AwsKeyCredentialsTest {

	final private static String ID = "abc";
	final private static String DESC = "a description";
	final private static String ACCESS = "myAccess";
	final private static String SECRET_VALUE = "mySecret";
	final private static Secret SECRET = mock(Secret.class);

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

}
