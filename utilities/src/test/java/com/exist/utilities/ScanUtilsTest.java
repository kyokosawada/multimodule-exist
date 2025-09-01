package com.exist.utilities;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScanUtilsTest {

    @Test
    void getUserInput() {
        String testInput = "hello\n";
        InputStream originalIn = System.in;
        try {
            System.setIn(new ByteArrayInputStream(testInput.getBytes()));
            String userInput = ScanUtils.getUserInput("");
            assertEquals("hello", userInput);
        } finally {
            System.setIn(originalIn);
        }
    }
}