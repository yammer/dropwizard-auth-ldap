package com.yammer.dropwizard.authenticator.healthchecks;

import com.codahale.metrics.health.HealthCheck;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;

import static com.google.common.base.Preconditions.checkNotNull;

public class LdapHealthCheck<T> extends HealthCheck {
    private final Authenticator<BasicCredentials, T> ldapAuthenticator;

    public LdapHealthCheck(Authenticator<BasicCredentials, T> ldapAuthenticator) {
        this.ldapAuthenticator = checkNotNull(ldapAuthenticator, "ldapAuthenticator cannot be null");
    }

    @Override
    public Result check() throws AuthenticationException {
        if (ldapAuthenticator.authenticate(new BasicCredentials("", "")).isPresent()) {
            return Result.healthy();
        } else {
            return Result.unhealthy("Cannot contact authentication service");
        }
    }
}