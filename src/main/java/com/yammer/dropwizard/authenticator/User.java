package com.yammer.dropwizard.authenticator;

import com.google.common.collect.ImmutableSet;

public class User {

    private final String name;
    private final ImmutableSet<String> roles;

    public User(String name, ImmutableSet<String> roles) {
        this.name = name;
        this.roles = roles;
    }

    public String getName() {
        return name;
    }

    public ImmutableSet<String> getRoles() {
        return roles;
    }
}
