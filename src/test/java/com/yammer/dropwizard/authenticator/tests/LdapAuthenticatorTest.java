package com.yammer.dropwizard.authenticator.tests;

import com.google.common.net.HostAndPort;
import com.yammer.dropwizard.auth.basic.BasicCredentials;
import com.yammer.dropwizard.authenticator.LdapAuthenticator;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class LdapAuthenticatorTest {
    private final LdapAuthenticator ldapAuthenticator = new LdapAuthenticator(
            HostAndPort.fromParts("ns-001.sjc1.yammer.com", 636));

    @Test
    public void badUser() {
        assertThat(ldapAuthenticator.authenticate(new BasicCredentials("yo", "dog")), is(false));
    }

    @Test
    public void noPassword() {
        assertThat(ldapAuthenticator.authenticate(new BasicCredentials("yo", "")), is(false));
    }

    @Test
    public void noUser() {
        assertThat(ldapAuthenticator.authenticate(new BasicCredentials("", "password")), is(false));
    }

    @Test
    public void badServer() {
        final LdapAuthenticator badAuthenticator = new LdapAuthenticator(HostAndPort.fromParts("localhost", 1234));
        assertThat(badAuthenticator.authenticate(new BasicCredentials("yo", "dog")), is(false));
    }
}