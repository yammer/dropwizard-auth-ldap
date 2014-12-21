package com.yammer.dropwizard.authenticator.healthchecks.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import io.dropwizard.auth.basic.BasicCredentials;

import org.junit.Test;

import com.codahale.metrics.health.HealthCheck;
import com.yammer.dropwizard.authenticator.LdapAuthenticator;
import com.yammer.dropwizard.authenticator.LdapCanAuthenticate;
import com.yammer.dropwizard.authenticator.LdapConfiguration;
import com.yammer.dropwizard.authenticator.ResourceAuthenticator;
import com.yammer.dropwizard.authenticator.healthchecks.LdapHealthCheck;

public class LdapHealthCheckTest {

    @Test
    public void healthy() throws Exception {
        LdapAuthenticator ldapAuthenticator = mock(LdapAuthenticator.class);
        when(ldapAuthenticator.authenticate(any(BasicCredentials.class))).thenReturn(true);
        LdapHealthCheck<BasicCredentials> healthCheck = new LdapHealthCheck<>(new ResourceAuthenticator(ldapAuthenticator));
        assertThat(healthCheck.check(), is(HealthCheck.Result.healthy()));
    }

    @Test
    public void unhealthy() throws Exception {
        final LdapAuthenticator badLdapAuthenticator = new LdapCanAuthenticate(new LdapConfiguration());
        final LdapHealthCheck<BasicCredentials> badHealthCheck = new LdapHealthCheck<>(new ResourceAuthenticator(badLdapAuthenticator));
        assertThat(badHealthCheck.check(), not(HealthCheck.Result.healthy()));
    }
}
