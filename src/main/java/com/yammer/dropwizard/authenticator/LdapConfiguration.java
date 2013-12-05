package com.yammer.dropwizard.authenticator;

import com.google.common.cache.CacheBuilderSpec;
import com.yammer.dropwizard.util.Duration;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;

public class LdapConfiguration {
    @NotNull
    @Valid
    private URI uri = URI.create("ldaps://www.example.com:636");
    @NotNull @Valid
    private CacheBuilderSpec cache = CacheBuilderSpec.disableCaching();
    @NotNull @NotEmpty
    private String securityPrincipal = "cn=%s";
    @NotNull @Valid
    private Duration connectTimeout = Duration.milliseconds(500);
    @NotNull @Valid
    private Duration readTimeout = Duration.milliseconds(500);

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public CacheBuilderSpec getCache() {
        return cache;
    }

    public void setCache(CacheBuilderSpec cache) {
        this.cache = cache;
    }

    public String getSecurityPrincipal() {
        return securityPrincipal;
    }

    public void setSecurityPrincipal(String securityPrincipal) {
        this.securityPrincipal = securityPrincipal;
    }

    public Duration getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Duration connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Duration getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(Duration readTimeout) {
        this.readTimeout = readTimeout;
    }
}