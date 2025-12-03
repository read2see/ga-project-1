package com.acme.models;

import com.acme.utils.Hash;
import java.time.LocalDateTime;
import java.util.UUID;

public abstract class Person {
    private UUID id = UUID.randomUUID();
    private String firstName;
    private String lastName;
    private String email;
    private String encryptedPassword;
    private int failedLoginAttempts;
    private boolean isLocked;
    private LocalDateTime lockUntil;

    public Person(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.encryptedPassword = Hash.make(password);
        this.failedLoginAttempts = 0;
        this.isLocked = false;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.encryptedPassword = Hash.make(password);
    }

    public UUID getId() {
        return this.id;
    }

    public String getEncryptedPassword() {
        return this.encryptedPassword;
    }

    public void setEncryptedPassword(String password) {
        this.encryptedPassword = password;
    }

    public boolean isLocked() {
        return this.isLocked;
    }

    public void setLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }

    public void incrementFailedLoginAttempts() {
        ++this.failedLoginAttempts;
    }

    public void resetFailedLoginAttempts() {
        this.failedLoginAttempts = 0;
    }

    public int getFailedLoginAttempts() {
        return this.failedLoginAttempts;
    }

    public void setFailedLoginAttempts(int failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public abstract String toJson();


}
