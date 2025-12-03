package com.acme.models;

import static com.acme.models.Role.BANKER;

public class Banker extends Person {

    private Role role;

    public Banker(String firstName, String lastName, String email, String password){
        super(firstName, lastName, email, password);

        this.role = BANKER;

    }

    public Role getRole() {
        return role;
    }


    @Override
    public String toJson() {
        return "";
    }
}