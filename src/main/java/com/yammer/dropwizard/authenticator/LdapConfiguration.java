package com.yammer.dropwizard.authenticator;

import com.google.common.cache.CacheBuilderSpec;
import com.google.common.collect.Sets;
import com.yammer.dropwizard.util.Duration;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.Set;

public class LdapConfiguration {
    @NotNull
    @Valid
    private URI uri = URI.create("ldaps://www.example.com:636");

    @NotNull
    @Valid
    private CacheBuilderSpec cachePolicy = CacheBuilderSpec.disableCaching();

    @NotNull
    @NotEmpty
    private String securityPrincipal = "dc=com";

    @NotNull
    @Valid
    private Duration connectTimeout = Duration.milliseconds(500);

    @NotNull
    @Valid
    private Duration readTimeout = Duration.milliseconds(500);

    @NotNull
    @Valid
    private Set<String> restrictToGroups = Sets.newHashSet();

    public URI getUri() {
        return uri;
    }

    public LdapConfiguration setUri(URI uri) {
        this.uri = uri;
        return this;
    }

    public CacheBuilderSpec getCachePolicy() {
        return cachePolicy;
    }

    public LdapConfiguration setCachePolicy(CacheBuilderSpec cachePolicy) {
        this.cachePolicy = cachePolicy;
        return this;
    }

    public String getSecurityPrincipal() {
        return securityPrincipal;
    }

    public LdapConfiguration setSecurityPrincipal(String securityPrincipal) {
        this.securityPrincipal = securityPrincipal;
        return this;
    }

    public Duration getConnectTimeout() {
        return connectTimeout;
    }

    public LdapConfiguration setConnectTimeout(Duration connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public Duration getReadTimeout() {
        return readTimeout;
    }

    public LdapConfiguration setReadTimeout(Duration readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    public Set<String> getRestrictToGroups() {
        return restrictToGroups;
    }

    public LdapConfiguration addRestrictedGroup(String group) {
        restrictToGroups.add(group);
        return this;
    }
}