package com.exist.utilities;

public final class AsciiUtils {

    private static final int ASCII_MIN = 33;      
    private static final int ASCII_RANGE = 94;  

    private AsciiUtils() {}

	public static String generateRandomAscii(int length) {
        String result = "";

        for (int i = 0; i < length; i++) {
            int randomAscii = (int) (Math.random() * ASCII_RANGE) + ASCII_MIN;
            char randomChar = (char) randomAscii;
            result += randomChar;
        }

        return result;
    }

}