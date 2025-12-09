package com.acme.models;

import com.acme.utils.Hash;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.UUID;

public abstract class Person {

    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String passwordHash;
    private Role role;

    protected Person() { }

    protected Person(Role role, String firstName, String lastName, String email, String password) {
        this.id = UUID.randomUUID();
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email.toLowerCase();
        this.passwordHash = Hash.make(password);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email.toLowerCase();
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @JsonIgnore
    public String getFullName() {
        return "%s-%s".formatted(firstName, lastName);
    }

    public boolean verifyPassword(String candidate) {
        return Hash.compare(candidate, passwordHash);
    }
}

