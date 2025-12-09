package com.acme.models;

public class Banker extends Person {

    private String employeeNumber;

    public Banker() { }

    public Banker(String firstName, String lastName, String email, String password, String employeeNumber) {
        super(Role.BANKER, firstName, lastName, email, password);
        this.employeeNumber = employeeNumber;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }
}

