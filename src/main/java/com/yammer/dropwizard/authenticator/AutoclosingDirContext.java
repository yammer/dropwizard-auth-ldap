package com.yammer.dropwizard.authenticator;

import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;

public class AutoclosingDirContext extends InitialDirContext implements AutoCloseable{

    protected AutoclosingDirContext(boolean lazy) throws NamingException {
        super(lazy);
    }

    public AutoclosingDirContext() throws NamingException {
    }

    public AutoclosingDirContext(Hashtable<?, ?> environment) throws NamingException {
        super(environment);
    }

    @Override
    public void close() throws NamingException {
        super.close();
    }
}
