package com.yammer.dropwizard.authenticator;

import com.google.common.base.Optional;
import com.google.common.net.HostAndPort;
import com.yammer.dropwizard.auth.basic.BasicCredentials;
import com.yammer.dropwizard.logging.Log;
import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Timer;
import com.yammer.metrics.core.TimerContext;

import javax.naming.*;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import java.util.Hashtable;

import static com.google.common.base.Preconditions.checkNotNull;

public class LdapAuthenticator {
    private final HostAndPort hostAndPort;
    private static final int DEFAULT_LDAP_SSL_PORT = 636;
    private static final Log LOG = Log.forClass(LdapAuthenticator.class);
    private static final Timer LDAP_AUTHENTICATION_TIMER = Metrics.newTimer(LdapAuthenticator.class, "authenticate");

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

    //Under ou=people the entries are either
    //1.cn=cgray
    //2.uid=cgray
    //This determines which by searching for that person and then returns a formatted string
    //cn=username,ou=people,dc=yammer,dc=com (replace cn with uid if necessary)
    protected Optional<String> getBindDN(String username) {
        final Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, providerUrl());

        try {
            final DirContext ctx = new InitialDirContext(env);
            final SearchControls controls = new SearchControls();
            controls.setReturningAttributes(new String[] {"givenName", "sn"});
            controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            final NamingEnumeration<SearchResult> answers = ctx.search("dc=yammer,dc=com",
                    String.format("(uid=%s)", username), controls);
            ctx.close();
            if (answers.hasMore()) {
                final String result = answers.next().getNameInNamespace();
                return result.isEmpty() ? Optional.<String>absent() : Optional.of(result);
            }
        } catch (AuthenticationException err) {
            LOG.debug(username + " failed to authenticate", err);
        } catch (NamingException err) {
            LOG.warn("Authentication failure", err);
        }

        return Optional.absent();
    }

    public boolean authenticate(BasicCredentials basicCredentials) {
        final TimerContext ldapAuthenticationTimer = LDAP_AUTHENTICATION_TIMER.time();
        try {
            final Optional<String> bindDN = getBindDN(basicCredentials.getUsername());
            if (bindDN.isPresent()) {
                final Hashtable<String, String> env = new Hashtable<String, String>();
                env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
                env.put(Context.PROVIDER_URL, providerUrl());
                env.put(Context.SECURITY_PRINCIPAL, bindDN.get());
                env.put(Context.SECURITY_CREDENTIALS, basicCredentials.getPassword());

                try {
                    new InitialDirContext(env).close();
                    return true;
                } catch (AuthenticationException err) {
                    LOG.debug(basicCredentials.getUsername() + " failed to authenticate", err);
                } catch (NamingException err) {
                    LOG.warn("Authentication failure", err);
                }
            }
            return false;
        } finally {
            ldapAuthenticationTimer.stop();
        }
    }
}