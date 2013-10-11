package com.yammer.dropwizard.authenticator.healthchecks;

import com.yammer.dropwizard.auth.AuthenticationException;
import com.yammer.dropwizard.auth.Authenticator;
import com.yammer.dropwizard.auth.basic.BasicCredentials;
import com.yammer.metrics.core.HealthCheck;

import static com.google.common.base.Preconditions.checkNotNull;

public class LdapHealthCheck extends HealthCheck {
    private final Authenticator<BasicCredentials, BasicCredentials> ldapAuthenticator;

    public LdapHealthCheck(Authenticator<BasicCredentials, BasicCredentials> ldapAuthenticator) {
        super("ldap");
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