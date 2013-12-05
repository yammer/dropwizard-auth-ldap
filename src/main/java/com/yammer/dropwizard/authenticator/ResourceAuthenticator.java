package com.yammer.dropwizard.authenticator;

import com.google.common.base.Optional;
import com.yammer.dropwizard.auth.AuthenticationException;
import com.yammer.dropwizard.auth.Authenticator;
import com.yammer.dropwizard.auth.basic.BasicCredentials;

import static com.google.common.base.Preconditions.checkNotNull;

public class ResourceAuthenticator implements Authenticator<BasicCredentials, BasicCredentials> {

    private final LdapAuthenticator ldapAuthenticator;

    public ResourceAuthenticator(LdapAuthenticator ldapAuthenticator) {
        this.ldapAuthenticator = checkNotNull(ldapAuthenticator);
    }

    @Override
    public Optional<BasicCredentials> authenticate(BasicCredentials credentials) throws AuthenticationException {
        if (ldapAuthenticator.authenticate(credentials)) {
            return Optional.of(credentials);
        } else {
            return Optional.absent();
        }
    }
}
