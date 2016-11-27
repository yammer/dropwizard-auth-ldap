package com.yammer.dropwizard.authenticator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.NamingException;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.StartTlsRequest;
import javax.naming.ldap.StartTlsResponse;
import java.util.Hashtable;

public class AutoclosingLdapContext extends InitialLdapContext implements AutoCloseable {
    private static final Logger LOGGER = LoggerFactory.getLogger(AutoclosingLdapContext.class);
    private StartTlsResponse tls = null;

    protected AutoclosingLdapContext() throws NamingException {
        this(new Hashtable<>(), true);
    }

    public AutoclosingLdapContext(Hashtable<?, ?> environment, boolean negotiateTls) throws NamingException {
        super(environment, null);
        if (negotiateTls) {
            try {
                tls = (StartTlsResponse) this.extendedOperation(new StartTlsRequest());
                tls.negotiate();
            } catch (Exception err) {
                LOGGER.info("Could not negotiate TLS", err);
            }
        }
    }

    @Override
    public void close() throws NamingException {
        if (tls != null) {
            try {
                tls.close();
            } catch (Exception err) {
                LOGGER.error("Could not close TLS", err);
            }
        }
        super.close();
    }
}
