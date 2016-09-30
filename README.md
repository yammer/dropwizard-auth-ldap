LDAP Authenticator [![Build Status](https://travis-ci.org/yammer/dropwizard-auth-ldap.svg)](https://travis-ci.org/yammer/dropwizard-auth-ldap) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.yammer.dropwizard/dropwizard-auth-ldap/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.yammer.dropwizard/dropwizard-auth-ldap)
==================

This is a simple dropwizard-auth module using Basic-Auth + LDAP for authentication. This is the module internal tools at Yammer
used to authenticate users.

Note: This module has only been subjected to the traffic of our engineering team. We have not used this to authenticate high-traffic or
tuned the JNDI connection pool as such.

Maven
-----
   
```xml
<dependency>
    <groupId>com.yammer.dropwizard</groupId>
    <artifactId>dropwizard-auth-ldap</artifactId>
    <version>1.0.0</version>
</dependency>
```

Legacy Dropwizard Support
------------------
0.0.x releases will contain bug/security updates.
0.1.x and beyond will support 0.7+ dropwizard

How To Use
----------

```java
LdapConfiguration configuration = new LdapConfiguration();
LdapAuthenticator authenticator = new LdapAuthenticator(configuration);
authenticator.authenticate(new BasicCredentials("user", "password"));
```

Add it to your Service
----------------------

I assume you are already familiar with dropwizard's authentication module.
You can find more information about dropwizard authentication at http://www.dropwizard.io/manual/auth.html

Here is an example how to add `LdapAuthenticator` using a `CachingAuthenticator` to your service:

```java
@Override
public void run(Configuration configuration, Environment environment) throws Exception {
    LdapConfiguration ldapConfiguration = configuration.getLdapConfiguration();
    Authenticator<BasicCredentials, BasicCredentials> ldapAuthenticator = new CachingAuthenticator<>(
            environment.metrics(),
            new ResourceAuthenticator(new LdapAuthenticator(ldapConfiguration)),
            ldapConfiguration.getCachePolicy());

    environment.jersey().register(AuthFactory.binder(new BasicAuthFactory<>(ldapAuthenticator, "realm", BasicCredentials.class));
    environment.healthChecks().register("ldap",
            new LdapHealthCheck<>(new ResourceAuthenticator(new LdapCanAuthenticate(ldapConfiguration))));
}
```

Additional Notes
----------------------

Make sure to register your resources. Example:

```java
environment.jersey().register(new YourResource());
```
Configuration
-------------

```yml
uri: ldaps://myldap.com:636
cachePolicy: maximumSize=10000, expireAfterWrite=10m
userFilter: ou=people,dc=yourcompany,dc=com
groupFilter: ou=groups,dc=yourcompany,dc=com
userNameAttribute: cn
groupNameAttribute: cn
groupMembershipAttribute: memberUid
groupClassName: posixGroup
restrictToGroups:
    - user
    - admin
    - bots
connectTimeout: 500ms
readTimeout: 500ms
```

Note: You can set `groupClassName` to `groupOfNames` and the `groupMembershipAttribute` to `member` to search for group membership using the full userDN.

CHANGELOG
---------
Check the [Changelog](https://github.com/yammer/dropwizard-auth-ldap/blob/master/CHANGELOG.md) for detailed updates.

Bugs and Feedback
-----------------
For bugs, questions, and discussions please use the [Github Issues](https://github.com/yammer/dropwizard-auth-ldap/issues)
