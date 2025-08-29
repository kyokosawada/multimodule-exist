package com.exist.utilities;

public final class AsciiUtils {

    private static final int ASCII_MIN = 33;      
    private static final int ASCII_RANGE = 94;  

    private AsciiUtils() {}

	public static String generateRandomAscii(int length) {
        String result = "";
        for (int i = 0; i < length; i++) {
            char randomChar;
            do {
                int randomAscii = (int) (Math.random() * ASCII_RANGE) + ASCII_MIN;
                randomChar = (char) randomAscii;
            } while (randomChar == ',' || randomChar == ')' || randomChar == '(');
            result += randomChar;
        }
        return result;
    }

}