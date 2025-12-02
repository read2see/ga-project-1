package com.acme.models;

import static com.acme.models.Role.BANKER;

public class Banker extends Person {

    private Role role;
    private String employeeNumber;

    public Banker(String firstName, String lastName, String email, String password, String employeeNumber){
        super(firstName, lastName, email, password);

        this.role = BANKER;
        this.employeeNumber = employeeNumber;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public Role getRole() {
        return role;
    }
}
