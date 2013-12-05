package com.yammer.dropwizard.authenticator;

import com.yammer.dropwizard.auth.AuthenticationException;
import com.yammer.dropwizard.auth.basic.BasicCredentials;

public class LdapCanAuthenticate extends LdapAuthenticator {
    public LdapCanAuthenticate(LdapConfiguration configuration) {
        super(configuration);
    }

    @Override
    public boolean authenticate(BasicCredentials basicCredentials) throws AuthenticationException {
        return canAuthenticate();
    }
}