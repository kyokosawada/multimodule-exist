package com.exist.utilities;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AsciiUtilsTest {
    @Test
    void testGenerateRandomAscii() {
        // Test several lengths
        for (int len : new int[]{1, 5, 10, 50, 100}) {
            String s = AsciiUtils.generateRandomAscii(len);
            assertEquals(len, s.length());
            for (char c : s.toCharArray()) {
                assertTrue(c >= 33 && c < 127);
                assertNotEquals(',', c);
                assertNotEquals('(', c);
                assertNotEquals(')', c);
            }
        }
    }
}