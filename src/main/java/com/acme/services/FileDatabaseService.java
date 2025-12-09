package com.acme.services;

import com.acme.models.Banker;
import com.acme.models.Customer;
import com.acme.models.Person;
import com.acme.models.Transaction;
import com.acme.utils.EncryptionService;
import com.acme.utils.EnvHelper;
import com.acme.utils.FileWorker;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.DeserializationFeature;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class FileDatabaseService {

    private final Path baseDir;
    private final ObjectMapper mapper;
    private final EncryptionService encryptionService;

    public FileDatabaseService() {
        this(Path.of("data"));
    }

    public FileDatabaseService(Path baseDir) {
        this.baseDir = baseDir;
        String key = Optional.ofNullable(EnvHelper.get("DATA_ENCRYPTION_KEY"))
                .orElseThrow(() -> new IllegalStateException("DATA_ENCRYPTION_KEY missing in .env"));
        this.encryptionService = new EncryptionService(key);
        this.mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public void saveCustomer(Customer customer) {
        writeEncrypted(userPath("Customer", customer.getFullName(), customer.getId()), customer);
    }

    public void saveBanker(Banker banker) {
        writeEncrypted(userPath("Banker", banker.getFullName(), banker.getId()), banker);
    }

    public List<Customer> loadCustomers() {
        return readUsers("Customer-", new TypeReference<Customer>() {});
    }

    public List<Banker> loadBankers() {
        return readUsers("Banker-", new TypeReference<Banker>() {});
    }

    public Optional<Person> findByEmail(String email) {
        String normalized = email.toLowerCase();
        return Stream.concat(loadCustomers().stream(), loadBankers().stream())
                .filter(p -> p.getEmail().equals(normalized))
                .findFirst();
    }

    public Optional<Customer> findCustomerById(UUID id) {
        return loadCustomers().stream()
                .filter(c -> c.getId().equals(id))
                .findFirst();
    }

    public List<Transaction> loadTransactions(UUID customerId) {
        Path path = transactionsPath(customerId);
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }
        String decrypted = encryptionService.decrypt(FileWorker.readString(path));
        try {
            return mapper.readValue(decrypted, new TypeReference<>() {});
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read transactions", e);
        }
    }

    public void appendTransaction(UUID customerId, Transaction tx) {
        List<Transaction> existing = loadTransactions(customerId);
        existing.add(tx);
        writeEncrypted(transactionsPath(customerId), existing);
    }

    private <T> List<T> readUsers(String prefix, TypeReference<T> typeRef) {
        Path usersDir = baseDir.resolve("users");
        if (!Files.exists(usersDir)) {
            return new ArrayList<>();
        }
        try (Stream<Path> paths = Files.list(usersDir)) {
            return paths.filter(p -> p.getFileName().toString().startsWith(prefix))
                    .flatMap(path -> {
                        try {
                            String decrypted = encryptionService.decrypt(FileWorker.readString(path));
                            T person = mapper.readValue(decrypted, typeRef);
                            return Stream.of(person);
                        } catch (Exception ex) {
                            System.err.println("Failed to load user file: " + path.getFileName() + " reason: " + ex.getMessage());
                        }
                        return Stream.empty();
                    })
                    .toList();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    private Path userPath(String prefix, String name, UUID id) {
        return baseDir.resolve("users").resolve("%s-%s-%s.json".formatted(prefix, name, id));
    }

    private Path transactionsPath(UUID customerId) {
        return baseDir.resolve("transactions").resolve("Customer-%s-transactions.json".formatted(customerId));
    }

    private void writeEncrypted(Path path, Object value) {
        try {
            String json = mapper.writeValueAsString(value);
            String cipher = encryptionService.encrypt(json);
            FileWorker.writeString(path, cipher);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to persist data", e);
        }
    }
}

