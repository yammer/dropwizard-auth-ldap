package com.yammer.dropwizard.authenticator;

import com.google.common.base.Optional;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;

import java.util.Collections;

import static com.google.common.base.Preconditions.checkNotNull;

public class ResourceAuthenticator implements Authenticator<BasicCredentials, User> {

    private final LdapAuthenticator ldapAuthenticator;

    public ResourceAuthenticator(LdapAuthenticator ldapAuthenticator) {
        this.ldapAuthenticator = checkNotNull(ldapAuthenticator);
    }

    @Override
    public Optional<User> authenticate(BasicCredentials credentials) throws AuthenticationException {
        if (ldapAuthenticator.authenticate(credentials)) {
            return Optional.of(new User(credentials.getUsername(), Collections.<String>emptySet()));
        } else {
            return Optional.absent();
        }
    }
}