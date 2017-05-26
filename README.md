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
    <version>1.0.4</version>
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
public void run(ExampleAppConfiguration configuration, Environment environment) throws Exception {
      final LdapConfiguration ldapConfiguration = configuration.getLdapConfiguration();
    
      Authenticator<BasicCredentials, User> ldapAuthenticator = new CachingAuthenticator<>(
              environment.metrics(),
              new ResourceAuthenticator(new LdapAuthenticator(ldapConfiguration)),
              ldapConfiguration.getCachePolicy());
    
      environment.jersey().register(new AuthDynamicFeature(
              new BasicCredentialAuthFilter.Builder<User>()
                      .setAuthenticator(ldapAuthenticator)
                      .setRealm("LDAP")
                      .buildAuthFilter()));
    
      environment.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));
    
      environment.healthChecks().register("ldap", new LdapHealthCheck<>(
              new ResourceAuthenticator(new LdapCanAuthenticate(ldapConfiguration))));}
```

https://github.com/yammer/dropwizard-auth-ldap/blob/master/src/test/java/com/yammer/dropwizard/authenticator/tests/ExampleAppTest.java

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
negotiateTls: strict
```

* Group filtering is done by default using only the username provided. The full DN of the user's account will be used
if `groupClassName` and `groupMembershipAttribute` are set to either `groupOfNames` and `member` or `groupOfUniqueNames` 
and `uniqueMember`.
* `negotiateTls` can be `NONE`, `ATTEMPT`, or `STRICT`. Where `ATTEMPT` tries to negotiate TLS if possible and `STRICT` 
fails the entire operation if TLS does not succeed in being established. Note that you may see exceptions related to the
initial TLS negotiation attempt in your logs if negotation fails.


CHANGELOG
---------
Check the [Changelog](https://github.com/yammer/dropwizard-auth-ldap/blob/master/CHANGELOG.md) for detailed updates.

Bugs and Feedback
-----------------
For bugs, questions, and discussions please use the [Github Issues](https://github.com/yammer/dropwizard-auth-ldap/issues)
