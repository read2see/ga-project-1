package com.acme.utils;

import org.mindrot.jbcrypt.BCrypt;

public final class Hash {

    private static final int WORK_FACTOR = 12;

    private Hash() {
    }

    public static String make(String input) {
        return BCrypt.hashpw(input, BCrypt.gensalt(WORK_FACTOR));
    }

    public static boolean compare(String input, String hash) {
        return BCrypt.checkpw(input, hash);
    }
}

