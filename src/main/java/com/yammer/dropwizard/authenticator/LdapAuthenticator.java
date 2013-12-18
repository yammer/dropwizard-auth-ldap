package com.yammer.dropwizard.authenticator;

import com.yammer.dropwizard.auth.basic.BasicCredentials;
import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Timer;
import com.yammer.metrics.core.TimerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;

import static com.google.common.base.Preconditions.checkNotNull;

public class LdapAuthenticator {
    private static final Logger LOG = LoggerFactory.getLogger(LdapAuthenticator.class);
    private static final Timer LDAP_AUTHENTICATION_TIMER = Metrics.defaultRegistry().newTimer(LdapAuthenticator.class, "authenticate");
    protected final LdapConfiguration configuration;

    public LdapAuthenticator(LdapConfiguration configuration) {
        this.configuration = checkNotNull(configuration);
    }

    public boolean canAuthenticate() {
        try {
            new InitialDirContext(contextConfiguration()).close();
            return true;
        } catch (Exception err) {
            //can't authenticate
        }
        return false;
    }

    public boolean authenticate(BasicCredentials basicCredentials) throws com.yammer.dropwizard.auth.AuthenticationException {
        final TimerContext ldapAuthenticationTimer = LDAP_AUTHENTICATION_TIMER.time();
        try {
            final String sanitizedUsername = sanitizeUsername(basicCredentials.getUsername());
            final Hashtable<String, String> env = contextConfiguration();
            env.put(Context.SECURITY_PRINCIPAL, String.format(configuration.getSecurityPrincipal(), sanitizedUsername));
            env.put(Context.SECURITY_CREDENTIALS, sanitizedUsername);

            try {
                new InitialDirContext(env).close();
                return true;
            } catch (AuthenticationException ae) {
                LOG.debug("{} failed to authenticate. {}", sanitizedUsername, ae);
            } catch (NamingException err) {
                throw new com.yammer.dropwizard.auth.AuthenticationException(String.format("LDAP Authentication failure (username: %s)",
                        sanitizedUsername), err);
            }
            return false;
        } finally {
            ldapAuthenticationTimer.stop();
        }
    }

    private Hashtable<String, String> contextConfiguration() {
        final Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, configuration.getUri().toString());
        env.put("com.sun.jndi.ldap.connect.timeout", String.valueOf(configuration.getConnectTimeout().toMilliseconds()));
        env.put("com.sun.jndi.ldap.read.timeout", String.valueOf(configuration.getReadTimeout().toMilliseconds()));
        env.put("com.sun.jndi.ldap.connect.pool", "true");
        return env;
    }

    private static String sanitizeUsername(String username) {
        return username.replaceAll("[^A-Za-z0-9-_.]", "");
    }
}
