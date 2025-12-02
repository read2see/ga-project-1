package com.acme.models;

import java.util.ArrayList;
import java.util.List;

import static com.acme.models.Role.CUSTOMER;

public class Customer extends Person {

    private List<Account> accounts;
    private boolean isLocked;
    private int failedLoginAttempts;
    private Role role;

    public Customer(String firstName, String lastName, String email, String password){
        super(firstName, lastName, email, password);

        this.accounts = new ArrayList<>();
        this.isLocked = false;
        this.failedLoginAttempts = 0;
        this.role = CUSTOMER;

    }

    public void addAccount(Account account){
        this.accounts.add(account);
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public void incrementFailedLoginAttempts(){
        this.failedLoginAttempts++ ;
    }

    public void resetFailedLoginAttempts(){
        this.failedLoginAttempts = 0;
    }

    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void setFailedLoginAttempts(int failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public Role getRole() {
        return role;
    }
}
