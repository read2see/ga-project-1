package com.acme;

import com.acme.models.*;
import com.acme.services.AuthenticationService;
import com.acme.services.BankingService;
import com.acme.services.FileAuthenticationService;
import com.acme.services.FileDatabaseService;
import com.acme.services.TransactionService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.Optional;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        FileDatabaseService database = new FileDatabaseService();
        AuthenticationService auth = new FileAuthenticationService(database);
        BankingService banking = new BankingService(database);
        TransactionService transactions = new TransactionService(database);

        preloadDefaultBanker(database);

        System.out.println("Welcome to ACME Bank CLI");
        while (true) {
            System.out.println("1) Login");
            System.out.println("2) Register customer");
            System.out.println("3) Exit");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> handleLogin(auth, banking, transactions);
                case "2" -> registerCustomer(auth);
                case "3" -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid choice");
            }
        }
    }


    // Credentials: email=boss@acme.com , password=Boss123!
    private static void preloadDefaultBanker(FileDatabaseService database) {
        try {
            Path usersDir = Path.of("data", "users");
            if (Files.exists(usersDir)) {
                try (var stream = Files.list(usersDir)) {
                    stream.filter(p -> p.getFileName().toString().startsWith("Banker-"))
                            .forEach(p -> {
                                try {
                                    Files.deleteIfExists(p);
                                } catch (IOException ignored) {
                                }
                            });
                }
            }
        } catch (IOException ignored) { }

        Banker banker = new Banker("Default", "Boss", "boss@acme.com", "Boss123!", "EMP-0001");
        banker.setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        database.saveBanker(banker);
    }

    private static void handleLogin(AuthenticationService auth,
                                    BankingService banking,
                                    TransactionService transactions) {
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        Optional<Person> logged = auth.login(email, password);
        if (logged.isEmpty()) {
            System.out.println("Login failed or account locked. Try again later.");
            return;
        }
        Person user = logged.get();
        if (user.getRole() == Role.BANKER) {
            System.out.println("Logged in as Banker " + user.getFullName());
            bankerMenu((Banker) user, banking, auth);
        } else {
            Customer customer = (Customer) user;
            System.out.println("Logged in as Customer " + customer.getFullName());
            customerMenu(customer, banking, transactions);
        }
    }

    private static void registerCustomer(AuthenticationService auth) {
        System.out.print("First name: ");
        String first = scanner.nextLine();
        System.out.print("Last name: ");
        String last = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        Customer created = auth.registerCustomer(first, last, email, password);
        System.out.println("Customer created with id " + created.getId());
    }

    private static void bankerMenu(Banker banker, BankingService banking, AuthenticationService auth) {
        while (true) {
            System.out.println("""
                    Banker actions:
                    1) Register new customer
                    2) Register new banker
                    3) Lookup customer by email
                    4) Logout""");
            String choice = scanner.nextLine();
            try {
                switch (choice) {
                    case "1" -> registerCustomer(auth);
                    case "2" -> {
                        System.out.print("First name: ");
                        String first = scanner.nextLine();
                        System.out.print("Last name: ");
                        String last = scanner.nextLine();
                        System.out.print("Email: ");
                        String email = scanner.nextLine();
                        System.out.print("Password: ");
                        String pass = scanner.nextLine();
                        System.out.print("Employee number: ");
                        String emp = scanner.nextLine();
                        auth.registerBanker(first, last, email, pass, emp);
                        System.out.println("Banker registered.");
                    }
                    case "3" -> {
                        System.out.print("Customer email: ");
                        String email = scanner.nextLine();
                        Optional<Customer> found = banking.findCustomerByEmail(email);
                        if (found.isPresent()) {
                            Customer c = found.get();
                            System.out.printf("Customer: %s (%s) Accounts: %d\n",
                                    c.getFullName(), c.getEmail(), c.getAccounts().size());
                        } else {
                            System.out.println("Customer not found.");
                        }
                    }
                    case "4" -> {
                        return;
                    }
                    default -> System.out.println("Invalid choice");
                }
            } catch (Exception ex) {
                System.out.println("Operation failed: " + ex.getMessage());
            }
        }
    }
    private static void customerMenu(Customer customer,
                                     BankingService banking,
                                     TransactionService transactions) {
        while (true) {
            System.out.println("""
                    1) Create account
                    2) Deposit
                    3) Withdraw
                    4) Transfer
                    5) Statement
                    6) Filter transactions
                    7) Logout""");
            String choice = scanner.nextLine();
            try {
                switch (choice) {
                    case "1" -> createAccount(customer, banking);
                    case "2" -> doDeposit(customer, banking);
                    case "3" -> doWithdraw(customer, banking);
                    case "4" -> doTransfer(customer, banking);
                    case "5" -> transactions.getStatement(customer.getId())
                            .forEach(tx -> System.out.printf("%s - %s %s (Balance: %s)\n",
                                    tx.getCreatedAt(), tx.getType(), tx.getDescription(), tx.getPostBalance()));
                    case "6" -> filterTransactions(customer, transactions);
                    case "7" -> {
                        return;
                    }
                    default -> System.out.println("Invalid choice");
                }
            } catch (Exception ex) {
                System.out.println("Operation failed: " + ex.getMessage());
            }
        }
    }

    private static void createAccount(Customer customer, BankingService banking) {
        System.out.println("""
         Account type:
         1) CHECKING
         2) SAVINGS""");
        String typeChoice = scanner.nextLine();
        String type = switch (typeChoice) {
            case "1" -> "CHECKING";
            case "2" -> "SAVINGS";
            default -> throw new IllegalArgumentException("Invalid account type selection");
        };
        System.out.print("Opening balance: ");
        BigDecimal balance = new BigDecimal(scanner.nextLine());
        Card card = pickNewCard();
        banking.createAccount(customer, type, balance, card);
        System.out.println("Account created.");
    }

    private static void doDeposit(Customer customer, BankingService banking) {
        Account account = pickAccount(customer);
        System.out.print("Amount: ");
        BigDecimal amount = new BigDecimal(scanner.nextLine());
        Card card = pickCardFromAccount(account);
        banking.deposit(customer, account, amount, card);
        System.out.println("Deposit completed. Balance: " + account.getBalance());
    }

    private static void doWithdraw(Customer customer, BankingService banking) {
        Account account = pickAccount(customer);
        System.out.print("Amount: ");
        BigDecimal amount = new BigDecimal(scanner.nextLine());
        Card card = pickCardFromAccount(account);
        banking.withdraw(customer, account, amount, card);
        System.out.println("Withdraw completed. Balance: " + account.getBalance());
    }

    private static void doTransfer(Customer customer, BankingService banking) {
        Account source = pickAccount(customer);
        System.out.print("Transfer to your own account? (y/n): ");
        boolean own = scanner.nextLine().equalsIgnoreCase("y");
        Account destination;
        if (own) {
            destination = pickAccount(customer);
        } else {
            System.out.print("Recipient email: ");
            String email = scanner.nextLine();
            Optional<Customer> other = banking.findCustomerByEmail(email);
            if (other.isEmpty()) {
                throw new IllegalArgumentException("Recipient not found");
            }
            destination = pickAccount(other.get());
        }
        System.out.print("Amount: ");
        BigDecimal amount = new BigDecimal(scanner.nextLine());
        Card card = pickCardFromAccount(source);
        banking.transfer(customer, source, destination, amount, card);
        System.out.println("Transfer completed. Balance: " + source.getBalance());
    }

    private static void filterTransactions(Customer customer, TransactionService transactions) {
        System.out.println("""
                Filter:
                1) Today
                2) Yesterday
                3) Last 7 days
                4) Last 30 days""");
        String option = scanner.nextLine();
        LocalDate today = LocalDate.now();
        LocalDate from;
        LocalDate to;
        switch (option) {
            case "1" -> {
                from = today;
                to = today;
            }
            case "2" -> {
                from = today.minusDays(1);
                to = from;
            }
            case "3" -> {
                from = today.minusDays(6);
                to = today;
            }
            case "4" -> {
                from = today.minusDays(29);
                to = today;
            }
            default -> {
                from = today;
                to = today;
            }
        }
        var filtered = transactions.filterByDateRange(customer.getId(), from, to);
        if (filtered.isEmpty()) {
            System.out.println("No transactions in selected range.");
            return;
        }
        filtered.forEach(tx -> System.out.printf("%s - %s %s (Balance: %s)\n",
                tx.getCreatedAt(), tx.getType(), tx.getDescription(), tx.getPostBalance()));
    }

    private static Account pickAccount(Customer customer) {
        System.out.println("Choose Account:");
        if (customer.getAccounts().isEmpty()) {
            throw new IllegalStateException("No accounts available");
        }
        for (int i = 0; i < customer.getAccounts().size(); i++) {
            Account account = customer.getAccounts().get(i);
            System.out.printf("%d) %s (%s) Balance: %s\n", i + 1, account.getAccountNumber(), account.getTypeLabel(), account.getBalance());
        }
        int idx = Integer.parseInt(scanner.nextLine()) - 1;
        return customer.getAccounts().get(idx);
    }

    private static Card pickNewCard() {
        System.out.println("""
        Card type:
        1) MASTERCARD
        2) MASTERCARD_TITANIUM
        3) MASTERCARD_PLATINUM""");
        return switch (scanner.nextLine()) {
            case "1" -> new MastercardCard();
            case "2" -> new MastercardTitaniumCard();
            case "3" -> new MastercardPlatinumCard();
            default -> new MastercardCard();
        };
    }

    private static Card pickCardFromAccount(Account account) {
        System.out.println("Pick Card:");
        if (account.getCards().isEmpty()) {
            throw new IllegalStateException("No cards available for this account");
        }
        for (int i = 0; i < account.getCards().size(); i++) {
            Card c = account.getCards().get(i);
            System.out.printf("%d) %s (issued %s)\n", i + 1, c.getLabel(), c.getIssuedOn());
        }
        int idx = Integer.parseInt(scanner.nextLine()) - 1;
        return account.getCards().get(idx);
    }
}

