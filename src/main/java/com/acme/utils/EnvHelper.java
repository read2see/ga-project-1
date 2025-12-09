package com.acme.utils;

import io.github.cdimascio.dotenv.Dotenv;

public final class EnvHelper {

    private static final Dotenv DOTENV = Dotenv.configure()
            .ignoreIfMissing()
            .load();

    private EnvHelper() {}

    public static String get(String key) {
        String fromDotEnv = DOTENV.get(key);
        if (fromDotEnv != null) {
            return fromDotEnv;
        }
        return System.getenv(key) != null ? System.getenv(key) : System.getProperty(key);
    }
}

