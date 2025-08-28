package com.exist.utilities;

import java.util.Scanner;

public final class ScanUtils {
	private static final Scanner scanner = new Scanner(System.in);

	private ScanUtils() {}

	public static String getUserInput(String input) {
        System.out.print(input);
        return scanner.nextLine().trim();
    }

}