package com.acme.services;

import com.acme.models.Banker;
import com.acme.models.Customer;
import com.acme.models.Person;

import java.time.Duration;
import java.util.Optional;

public class FileAuthenticationService implements AuthenticationService {

    private static final int MAX_ATTEMPTS = 3;
    private static final Duration LOCK_DURATION = Duration.ofMinutes(1);

    private final FileDatabaseService databaseService;

    public FileAuthenticationService(FileDatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @Override
    public Optional<Person> login(String email, String password) {
        Optional<Person> person = databaseService.findByEmail(email);
        if (person.isEmpty()) {
            return Optional.empty();
        }
        Person user = person.get();

        if (user instanceof Customer customer) {
            if (customer.shouldUnlock()) {
                databaseService.saveCustomer(customer);
            }
            if (customer.isLocked()) {
                return Optional.empty();
            }
            if (!customer.verifyPassword(password)) {
                customer.incrementFailedAttempts(MAX_ATTEMPTS, LOCK_DURATION);
                databaseService.saveCustomer(customer);
                return Optional.empty();
            }
            customer.resetFailedAttempts();
            databaseService.saveCustomer(customer);
            return Optional.of(customer);
        }

        if (user instanceof Banker banker) {
            if (!banker.verifyPassword(password)) {
                return Optional.empty();
            }
            return Optional.of(banker);
        }
        return Optional.empty();
    }

    @Override
    public Customer registerCustomer(String firstName, String lastName, String email, String password) {
        Customer customer = new Customer(firstName, lastName, email, password);
        databaseService.saveCustomer(customer);
        return customer;
    }

    @Override
    public Banker registerBanker(String firstName, String lastName, String email, String password, String employeeNumber) {
        Banker banker = new Banker(firstName, lastName, email, password, employeeNumber);
        databaseService.saveBanker(banker);
        return banker;
    }
}

