package com.yammer.dropwizard.authenticator.healthchecks.tests;

import com.yammer.dropwizard.auth.basic.BasicCredentials;
import com.yammer.dropwizard.authenticator.LdapAuthenticator;
import com.yammer.dropwizard.authenticator.LdapCanAuthenticate;
import com.yammer.dropwizard.authenticator.LdapConfiguration;
import com.yammer.dropwizard.authenticator.ResourceAuthenticator;
import com.yammer.dropwizard.authenticator.healthchecks.LdapHealthCheck;
import com.yammer.metrics.core.HealthCheck;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LdapHealthCheckTest {
    private LdapAuthenticator ldapAuthenticator;
    private LdapHealthCheck healthCheck;

    @Before
    public void setup() {
        ldapAuthenticator = new LdapCanAuthenticate(new LdapConfiguration());
        healthCheck = new LdapHealthCheck(new ResourceAuthenticator(ldapAuthenticator));
    }

    @Test
    public void healthy() throws Exception {
        ldapAuthenticator = mock(LdapAuthenticator.class);
        when(ldapAuthenticator.authenticate(any(BasicCredentials.class))).thenReturn(true);
        healthCheck = new LdapHealthCheck(new ResourceAuthenticator(ldapAuthenticator));
        assertThat(healthCheck.check(), is(HealthCheck.Result.healthy()));
    }

    @Test
    public void unhealthy() throws Exception {
        final LdapAuthenticator badLdapAuthenticator = new LdapCanAuthenticate(new LdapConfiguration());
        final LdapHealthCheck badHealthCheck = new LdapHealthCheck(new ResourceAuthenticator(badLdapAuthenticator));
        assertThat(badHealthCheck.check(), not(HealthCheck.Result.healthy()));
    }
}
