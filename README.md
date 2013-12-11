LDAP Authenticator
==================

This is a dropwizard-auth module using Basic-Auth + LDAP for authentication. This is the module internal tools at Yammer
used to authenticate users.

Note: This module has only been subjected to the traffic of our engineering team. We have not used this to authenticate high-traffic or
tuned the JNDI connection pool as such.

Maven
-----
    <dependency>
        <groupId>com.yammer.dropwizard</groupId>
        <artifactId>dropwizard-auth-ldap</artifactId>
        <version>0.0.16</version>
    </dependency>

How To Use
----------

    LdapConfiguration configuration = new LdapConfiguration();
    LdapAuthenticator authenticator = new LdapAuthenticator(configuration);
    authenticator.authenticate(new BasicCredentials("user", "password"));

Add it to your Service
----------------------

I assume you are already familiar with dropwizard's authentication module.
You can find more information about dropwizard authentication at http://www.dropwizard.io/manual/auth/

Here is an example how to add `LdapAuthenticator` using a `CachingAuthenticator` to your service:

    @Override
    public void run(Configuration configuration, Environment environment) throws Exception {
        LdapConfiguration ldapConfiguration = configuration.getLdapConfiguration();
        Authenticator<BasicCredentials, BasicCredentials> ldapAuthenticator =
            CachingAuthenticator.wrap(
                new ResourceAuthenticator(new LdapAuthenticator(ldapConfiguration)),
                ldapConfiguration.getCachePolicy());

        environment.addProvider(new BasicAuthProvider<>(ldapAuthenticator, "realm"));
    }

Configuration
-------------
    uri: "ldaps://myldap.com:636"
    cachePolicy: maximumSize=10000, expireAfterAccess=10m
    securityPrincipal: "cn=%s,dc=yourcompany,dc=com";
    connectTimeout: 500ms
    readTimeout: 500ms
