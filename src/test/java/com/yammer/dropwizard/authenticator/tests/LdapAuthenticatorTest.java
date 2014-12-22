package com.yammer.dropwizard.authenticator.tests;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.basic.BasicCredentials;

import org.junit.BeforeClass;
import org.junit.Test;

import com.yammer.dropwizard.authenticator.LdapAuthenticator;
import com.yammer.dropwizard.authenticator.LdapConfiguration;

public class LdapAuthenticatorTest {
    private static LdapAuthenticator ldapAuthenticator;

    @BeforeClass
    public static void setup() throws Exception {
        final LdapConfiguration configuration = new LdapConfiguration();
        ldapAuthenticator = new LdapAuthenticator(configuration);
    }

    @Test(expected = AuthenticationException.class)
    public void badUser() throws AuthenticationException {
        ldapAuthenticator.authenticate(new BasicCredentials("user", "password"));
    }

    @Test(expected = AuthenticationException.class)
    public void noPassword() throws AuthenticationException {
        ldapAuthenticator.authenticate(new BasicCredentials("user", ""));
    }

    @Test(expected = AuthenticationException.class)
    public void noUser() throws AuthenticationException {
        ldapAuthenticator.authenticate(new BasicCredentials("", "password"));
    }

    @Test(expected = AuthenticationException.class)
    public void badServer() throws AuthenticationException {
        ldapAuthenticator.authenticate(new BasicCredentials("user", "password"));
    }
}