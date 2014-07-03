package com.yammer.dropwizard.authenticator;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.basic.BasicCredentials;

public class LdapCanAuthenticate extends LdapAuthenticator {
    public LdapCanAuthenticate(LdapConfiguration configuration) {
        super(configuration);
    }

    @Override
    public boolean authenticate(BasicCredentials credentials) throws AuthenticationException {
        return canAuthenticate();
    }
}