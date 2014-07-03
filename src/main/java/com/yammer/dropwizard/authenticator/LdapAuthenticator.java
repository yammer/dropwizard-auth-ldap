package com.yammer.dropwizard.authenticator;

import com.codahale.metrics.annotation.Timed;
import com.google.common.collect.ImmutableSet;
import io.dropwizard.auth.basic.BasicCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import java.util.Hashtable;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class LdapAuthenticator {
    private static final Logger LOG = LoggerFactory.getLogger(LdapAuthenticator.class);

    private static String sanitizeEntity(String name) {
        return name.replaceAll("[^A-Za-z0-9-_.]", "");
    }

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

    private boolean filterByGroup(InitialDirContext context, String sanitizedUsername) throws NamingException {
        final Set<String> restrictedToGroups = configuration.getRestrictToGroups();
        if (restrictedToGroups.isEmpty()) {
            return true;
        }

        final StringBuilder groupFilter = new StringBuilder();
        for (String group : restrictedToGroups) {
            final String sanitizedGroup = sanitizeEntity(group);
            groupFilter.append(String.format("(%s=%s)", configuration.getGroupNameAttribute(), sanitizedGroup));
        }

        final String filter = String.format("(&(%s=%s)(|%s))", configuration.getGroupMembershipAttribute(), sanitizedUsername, groupFilter.toString());

        final NamingEnumeration<SearchResult> result = context.search(configuration.getGroupFilter(), filter, new SearchControls());
        try {
            return result.hasMore();
        }
        finally {
            result.close();
        }
    }

    private ImmutableSet<String> getGroupMembershipsIntersectingWithRestrictedGroups(InitialDirContext context, String userName) throws NamingException {

        final String filter = String.format("(&(%s=%s)(objectClass=posixGroup))", configuration.getGroupMembershipAttribute(), userName);
        final NamingEnumeration<SearchResult> result = context.search(configuration.getGroupFilter(), filter, new SearchControls());


        ImmutableSet.Builder<String> setBuilder = new ImmutableSet.Builder<>();
        try {
            while(result.hasMore()){
                SearchResult next = result.next();
                if(next.getAttributes() != null && next.getAttributes().get(configuration.getGroupNameAttribute()) != null){
                    String group = (String) next.getAttributes().get(configuration.getGroupNameAttribute()).get(0);
                    if(configuration.getRestrictToGroups().contains(group)){
                        setBuilder.add(group);
                    }
                }
            }
            return setBuilder.build();
        }
        finally {
            result.close();
        }
    }

    @Timed
    public boolean authenticate(BasicCredentials credentials) throws io.dropwizard.auth.AuthenticationException {
        final String sanitizedUsername = sanitizeEntity(credentials.getUsername());
        try {
            try (AutoclosingDirContext context = buildContext(sanitizedUsername, credentials.getPassword())){
                return filterByGroup(context, sanitizedUsername);
            }
        } catch (AuthenticationException ae) {
            LOG.debug("{} failed to authenticate. {}", sanitizedUsername, ae);
        } catch (NamingException err) {
            throw new io.dropwizard.auth.AuthenticationException(String.format("LDAP Authentication failure (username: %s)",
                    sanitizedUsername), err);
        }
        return false;
    }

    private AutoclosingDirContext buildContext(String sanitizedUsername, String password) throws NamingException {
        final String userDN = String.format("%s=%s,%s", configuration.getUserNameAttribute(), sanitizedUsername, configuration.getUserFilter());

        final Hashtable<String, String> env = contextConfiguration();

        env.put(Context.SECURITY_PRINCIPAL, userDN);
        env.put(Context.SECURITY_CREDENTIALS, password);

        return new AutoclosingDirContext(env);
    }

    @Timed
    public User authenticateAndReturnPermittedGroups(BasicCredentials credentials) throws io.dropwizard.auth.AuthenticationException {
        final String sanitizedUsername = sanitizeEntity(credentials.getUsername());
        try {
            try (AutoclosingDirContext context = buildContext(sanitizedUsername, credentials.getPassword())){
                ImmutableSet<String> groupMemberships = getGroupMembershipsIntersectingWithRestrictedGroups(context, sanitizedUsername);
                if(groupMemberships.isEmpty()){
                    throw new AuthenticationException("No group memberships matching restricted groups");
                }
                return new User(sanitizedUsername, groupMemberships);
            }
        } catch (AuthenticationException ae) {
            LOG.debug("{} failed to authenticate. {}", sanitizedUsername, ae);
        } catch (NamingException err) {
            throw new io.dropwizard.auth.AuthenticationException(String.format("LDAP Authentication failure (username: %s)",
                    sanitizedUsername), err);
        }
        throw new io.dropwizard.auth.AuthenticationException(String.format("LDAP Authentication failure (username: %s)",
                sanitizedUsername));
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
}
