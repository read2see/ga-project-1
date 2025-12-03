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

    public Person(String var1, String var2, String var3, String var4) {
        this.firstName = var1;
        this.lastName = var2;
        this.email = var3;
        this.encryptedPassword = Hash.make(var4);
        this.failedLoginAttempts = 0;
        this.isLocked = false;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String var1) {
        this.firstName = var1;
    }

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String var1) {
        this.lastName = var1;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String var1) {
        this.email = var1;
    }

    public void setPassword(String var1) {
        this.encryptedPassword = Hash.make(var1);
    }

    public UUID getId() {
        return this.id;
    }

    public String getEncryptedPassword() {
        return this.encryptedPassword;
    }

    public void setEncryptedPassword(String var1) {
        this.encryptedPassword = var1;
    }

    public boolean isLocked() {
        return this.isLocked;
    }

    public void setLocked(boolean var1) {
        this.isLocked = var1;
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

    public void setFailedLoginAttempts(int var1) {
        this.failedLoginAttempts = var1;
    }

    public abstract String toJson();
}
