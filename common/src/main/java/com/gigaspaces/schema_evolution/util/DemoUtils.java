package com.gigaspaces.schema_evolution.util;

import java.util.Random;

public class DemoUtils {
    public final static String PERSON_DOCUMENT = "PersonDocument";
    private final static Random random = new Random();

    public static String createRandomString(int length){
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        return random.ints(leftLimit, rightLimit + 1)
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
