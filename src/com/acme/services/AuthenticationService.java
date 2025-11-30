package com.acme.services;

import com.acme.models.Person;

interface AuthenticationService {

    Person authenticate(String username, String password);
}
