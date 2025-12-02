package com.acme.utils;
import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

public class Hash {

    public static String make(String input){

        return Hashing.sha256().hashString(input, StandardCharsets.UTF_8).toString();
    }

    public static boolean compare(String input, String hash){

        String hashedInput = Hash.make(input);

        return  hash.equals(hashedInput);
    }
}
