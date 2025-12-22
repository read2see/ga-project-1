package com.acme.services;

import com.acme.models.Person;

import java.time.LocalDateTime;
import java.util.Optional;

public class LoginResult {

    public enum Status {
        SUCCESS,
        USER_NOT_FOUND,
        ACCOUNT_LOCKED,
        INVALID_PASSWORD,
        INVALID_PASSWORD_BANKER
    }

    private final Status status;
    private final Person person;
    private final int remainingAttempts;
    private final LocalDateTime lockedUntil;
    private final int maxAttempts;

    private LoginResult(Status status, Person person, int remainingAttempts, LocalDateTime lockedUntil, int maxAttempts) {
        this.status = status;
        this.person = person;
        this.remainingAttempts = remainingAttempts;
        this.lockedUntil = lockedUntil;
        this.maxAttempts = maxAttempts;
    }

    public static LoginResult success(Person person) {
        return new LoginResult(Status.SUCCESS, person, 0, null, 0);
    }

    public static LoginResult userNotFound() {
        return new LoginResult(Status.USER_NOT_FOUND, null, 0, null, 0);
    }

    public static LoginResult accountLocked(LocalDateTime lockedUntil) {
        return new LoginResult(Status.ACCOUNT_LOCKED, null, 0, lockedUntil, 0);
    }

    public static LoginResult invalidPassword(int remainingAttempts, int maxAttempts) {
        return new LoginResult(Status.INVALID_PASSWORD, null, remainingAttempts, null, maxAttempts);
    }

    public static LoginResult invalidPasswordBanker() {
        return new LoginResult(Status.INVALID_PASSWORD_BANKER, null, 0, null, 0);
    }

    public Status getStatus() {
        return status;
    }

    public Optional<Person> getPerson() {
        return Optional.ofNullable(person);
    }

    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }

    public int getRemainingAttempts() {
        return remainingAttempts;
    }

    public LocalDateTime getLockedUntil() {
        return lockedUntil;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }
}