package com.yammer.dropwizard.authenticator;

import com.google.common.collect.ImmutableSet;

public class User {

    private final String name;
    private final  String password;
    private final ImmutableSet<String> roles;

    public User(String name, String password, ImmutableSet<String> roles) {
        this.name = name;
        this.password = password;
        this.roles = roles;
    }

    public String getName() {
        return name;
    }

    public ImmutableSet<String> getRoles() {
        return roles;
    }

    public String getPassword() {
        return password;
    }
}
