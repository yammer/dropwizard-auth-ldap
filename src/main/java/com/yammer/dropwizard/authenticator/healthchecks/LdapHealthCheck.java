package com.yammer.dropwizard.authenticator.healthchecks;

import com.yammer.dropwizard.authenticator.LdapAuthenticator;
import com.yammer.metrics.core.HealthCheck;

import static com.google.common.base.Preconditions.checkNotNull;

public class LdapHealthCheck extends HealthCheck {
    private final LdapAuthenticator ldapAuthenticator;

    public LdapHealthCheck(LdapAuthenticator ldapAuthenticator) {
        super("ldap");
        this.ldapAuthenticator = checkNotNull(ldapAuthenticator, "ldapAuthenticator cannot be null");
    }

    @Override
    public Result check() {
        if (ldapAuthenticator.canAuthenticate()) {
            return Result.healthy();
        } else {
            return Result.unhealthy("Cannot contact authentication service");
        }
    }
}