package com.yammer.dropwizard.authenticator.tests;

import com.yammer.dropwizard.authenticator.LdapAuthenticator;
import com.yammer.dropwizard.authenticator.LdapConfiguration;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.basic.BasicCredentials;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LdapAuthenticatorTest {
    private static LdapAuthenticator ldapAuthenticator;

    @BeforeClass
    public static void setup() throws Exception {
        final LdapConfiguration configuration = new LdapConfiguration();
        ldapAuthenticator = new LdapAuthenticator(configuration);
    }

    @Test(expected = AuthenticationException.class)
    public void badUser() throws AuthenticationException {
        assertThat(ldapAuthenticator.authenticate(new BasicCredentials("user", "password")), is(false));
    }

    @Test(expected = AuthenticationException.class)
    public void noPassword() throws AuthenticationException {
        assertThat(ldapAuthenticator.authenticate(new BasicCredentials("user", "")), is(false));
    }

    @Test(expected = AuthenticationException.class)
    public void noUser() throws AuthenticationException {
        assertThat(ldapAuthenticator.authenticate(new BasicCredentials("", "password")), is(false));
    }

    @Test(expected = AuthenticationException.class)
    public void badServer() throws AuthenticationException {
        assertThat(ldapAuthenticator.authenticate(new BasicCredentials("user", "password")), is(false));
    }
}