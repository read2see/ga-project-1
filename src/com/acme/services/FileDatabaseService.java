package com.acme.services;

import com.acme.models.Banker;
import com.acme.models.Customer;
import com.acme.utils.FileWorker;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileDatabaseService implements IAuthenticationService {

    private static final String BASE_DIRECTORY = "data";

    public FileDatabaseService() {
        createBaseDirectory();
    }

    public void createBaseDirectory() {
        File directory = new File(BASE_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdir();
        }
    }

    public void saveCustomer(Customer customer) {
        String fileName = getCustomerFileName(customer);
        saveToFile(fileName, customer.toJson());
    }

    public void saveToFile(String fileName, String content){
        FileWorker.appendTofile(fileName, content);
    }

    public Optional<Customer> loadCustomer(UUID id) {
        File customerFile = findFileByPrefix("Customer-", id.toString());
        if (customerFile == null) return Optional.empty();

        try {
            List<String> lines = Files.readAllLines(customerFile.toPath());
            return Optional.of(deserializeCustomer(lines));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<Banker> loadBanker(UUID id) {
        File bankerFile = findFileByPrefix("Banker-", id.toString());
        if (bankerFile == null) return Optional.empty();

        try {
            List<String> lines = Files.readAllLines(bankerFile.toPath());
            return Optional.of(deserializeBanker(lines));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Customer deserializeCustomer(List<String> lines){

        return //Customer
    }

    public Banker deserializeBanker(List<String> lines){

        return //Banker
    }


    private String getCustomerFileName(Customer c) {
        return "Customer-" + c.getFullName().replace(" ", "_") + "-" + c.getId() + ".json";
    }

    private String getBankerFileName(Banker b) {
        return "Banker-" + b.getFullName().replace(" ", "_") + "-" + b.getId() + ".txt";
    }

    private File findFileByPrefix(String prefix, String idString) {
        File folder = new File(BASE_DIRECTORY);

        File[] files = folder.listFiles((dir, name) ->
                name.startsWith(prefix) && name.contains(idString)
        );

        return (files != null && files.length > 0) ? files[0] : null;
    }



}
