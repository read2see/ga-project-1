package com.acme.services;

import com.acme.models.Banker;
import com.acme.models.Customer;

public interface AuthenticationService {

    LoginResult login(String email, String password);

    Customer registerCustomer(String firstName, String lastName, String email, String password);

    Banker registerBanker(String firstName, String lastName, String email, String password, String employeeNumber);
}

