package com.yammer.dropwizard.authenticator;

import com.google.common.net.HostAndPort;
import com.yammer.dropwizard.auth.basic.BasicCredentials;
import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Timer;
import com.yammer.metrics.core.TimerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.AuthenticationException;
import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;

import static com.google.common.base.Preconditions.checkNotNull;

public class LdapAuthenticator {
    private static final int DEFAULT_LDAP_SSL_PORT = 636;
    private static final Logger LOG = LoggerFactory.getLogger(LdapAuthenticator.class);
    private static final Timer LDAP_AUTHENTICATION_TIMER = Metrics.defaultRegistry().newTimer(LdapAuthenticator.class, "authenticate");
    private final HostAndPort hostAndPort;

    public LdapAuthenticator(HostAndPort hostAndPort) {
        this.hostAndPort = checkNotNull(hostAndPort, "hostAndPort cannot be null");
    }

    private String providerUrl() {
        return String.format("ldaps://%s:%d/",
                hostAndPort.getHostText(),
                hostAndPort.getPortOrDefault(DEFAULT_LDAP_SSL_PORT));
    }

    public boolean canAuthenticate() {
        final Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, providerUrl());

        try {
            new InitialDirContext(env).close();
            return true;
        } catch (CommunicationException err) {
            //can't authenticate
        } catch (NamingException err) {
            //anything else is bad
        }
        return false;
    }

    public boolean authenticate(BasicCredentials basicCredentials) throws com.yammer.dropwizard.auth.AuthenticationException {
        final TimerContext ldapAuthenticationTimer = LDAP_AUTHENTICATION_TIMER.time();
        try {
            final Hashtable<String, String> env = new Hashtable<>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, providerUrl());
            env.put(Context.SECURITY_PRINCIPAL, String.format("cn=%s,ou=people,dc=yammer,dc=com", basicCredentials.getUsername()));
            env.put(Context.SECURITY_CREDENTIALS, basicCredentials.getPassword());

            try {
                new InitialDirContext(env).close();
                return true;
            } catch (AuthenticationException ae) {
                LOG.warn(String.format("%s failed to authenticate. "), basicCredentials.getUsername(), ae);
            } catch (NamingException err) {
                throw new com.yammer.dropwizard.auth.AuthenticationException(String.format("LDAP Authentication failure (username: %s)",
                        basicCredentials.getUsername()), err);
            }
            return false;
        } finally {
            ldapAuthenticationTimer.stop();
        }
    }
}