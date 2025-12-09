package com.acme.services;

import com.acme.models.Banker;
import com.acme.models.Customer;
import com.acme.models.Person;

import java.util.Optional;

public interface AuthenticationService {

    Optional<Person> login(String email, String password);

    Customer registerCustomer(String firstName, String lastName, String email, String password);

    Banker registerBanker(String firstName, String lastName, String email, String password, String employeeNumber);
}

