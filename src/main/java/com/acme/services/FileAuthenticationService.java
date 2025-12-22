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
    public LoginResult login(String email, String password) {
        Optional<Person> person = databaseService.findByEmail(email);
        if (person.isEmpty()) {
            return LoginResult.userNotFound();
        }
        Person user = person.get();

        if (user instanceof Customer customer) {
            if (customer.shouldUnlock()) {
                databaseService.saveCustomer(customer);
            }
            if (customer.isLocked()) {
                return LoginResult.accountLocked(customer.getLockedUntil());
            }
            if (!customer.verifyPassword(password)) {
                customer.incrementFailedAttempts(MAX_ATTEMPTS, LOCK_DURATION);
                databaseService.saveCustomer(customer);

                int currentAttempts = customer.getFailedLoginAttempts();
                int remainingAttempts = MAX_ATTEMPTS - currentAttempts;

                if (customer.isLocked()) {
                    return LoginResult.accountLocked(customer.getLockedUntil());
                }

                return LoginResult.invalidPassword(remainingAttempts, MAX_ATTEMPTS);
            }
            customer.resetFailedAttempts();
            databaseService.saveCustomer(customer);
            return LoginResult.success(customer);
        }

        if (user instanceof Banker banker) {
            if (!banker.verifyPassword(password)) {
                return LoginResult.invalidPasswordBanker();
            }
            return LoginResult.success(banker);
        }
        return LoginResult.userNotFound();
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

