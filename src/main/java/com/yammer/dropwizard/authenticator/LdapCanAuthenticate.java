package com.yammer.dropwizard.authenticator;

import com.google.common.net.HostAndPort;
import com.yammer.dropwizard.auth.AuthenticationException;
import com.yammer.dropwizard.auth.basic.BasicCredentials;

public class LdapCanAuthenticate extends LdapAuthenticator {
    public LdapCanAuthenticate(HostAndPort hostAndPort) {
        super(hostAndPort);
    }

    @Override
    public boolean authenticate(BasicCredentials basicCredentials) throws AuthenticationException {
        return canAuthenticate();
    }
}