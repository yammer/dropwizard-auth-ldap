package com.yammer.dropwizard.authenticator.tests;

import com.yammer.dropwizard.authenticator.*;
import com.yammer.dropwizard.authenticator.healthchecks.LdapHealthCheck;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.CachingAuthenticator;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.auth.basic.BasicCredentials;
import io.dropwizard.setup.Environment;

public class ExampleAppTest {
    static class ExampleAppConfiguration extends Configuration {
        private LdapConfiguration ldap = new LdapConfiguration();

        LdapConfiguration getLdapConfiguration() {
            return ldap;
        }
    }

    static class ExampleApp extends Application<ExampleAppConfiguration> {
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
                    new ResourceAuthenticator(new LdapCanAuthenticate(ldapConfiguration))));
        }
    }
}
