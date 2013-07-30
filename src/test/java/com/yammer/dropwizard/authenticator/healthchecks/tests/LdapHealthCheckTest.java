package com.yammer.dropwizard.authenticator.healthchecks.tests;

import com.google.common.net.HostAndPort;
import com.yammer.dropwizard.authenticator.LdapAuthenticator;
import com.yammer.dropwizard.authenticator.healthchecks.LdapHealthCheck;
import com.yammer.metrics.core.HealthCheck;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

@Ignore("this test is not self contained and it needs external infrastructure")
public class LdapHealthCheckTest {
    private final LdapHealthCheck ldapHealthCheck = new LdapHealthCheck(
            new LdapAuthenticator(HostAndPort.fromParts("ldap.sjc1.yammer.com", 636)));

    @Test
    public void healthy() throws Exception {
        assertThat(ldapHealthCheck.check(), is(HealthCheck.Result.healthy()));
    }

    @Test
    public void unhealthy() throws Exception {
        final LdapAuthenticator badLdapAuthenticator = new LdapAuthenticator(HostAndPort.fromParts("badHost", 1234));
        final LdapHealthCheck badHealthCheck = new LdapHealthCheck(badLdapAuthenticator);
        assertThat(badHealthCheck.check(), not(HealthCheck.Result.healthy()));
    }
}
