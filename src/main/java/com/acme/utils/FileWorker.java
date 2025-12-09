package com.acme.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FileWorker {

    private FileWorker() {}

    public static void ensureParent(Path path) {
        try {
            Files.createDirectories(path.getParent());
        } catch (IOException e) {
            throw new IllegalStateException("Unable to create parent directories for " + path, e);
        }
    }

    public static void writeString(Path path, String content) {
        ensureParent(path);
        try {
            Files.writeString(path, content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to write file " + path, e);
        }
    }

    public static String readString(Path path) {
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read file " + path, e);
        }
    }
}

