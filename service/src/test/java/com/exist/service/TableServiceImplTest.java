package com.exist.service;

import com.exist.service.impl.TableServiceImpl;

import java.io.IOException;
import java.util.Arrays;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TableServiceImplTest {

    TableService tableService;

    @BeforeEach
    void setUp() throws IOException, NoSuchFieldException, IllegalAccessException {
        tableService = new TableServiceImpl();
        tableService.getTable().add(Arrays.asList("(abc,xyz)", "(foo,bar)", "(search,term)"));
        tableService.getTable().add(Arrays.asList("(abc,term)", "(xyz,abc)", "(something,else)"));
        // Set fileName using reflection to avoid IO errors for saveFile
        java.lang.reflect.Field fileNameField = TableServiceImpl.class.getDeclaredField("fileName");
        fileNameField.setAccessible(true);
        fileNameField.set(tableService, "dummy.txt");
    }

    @Test
    void loadTableFromFile() {
    }

    @Test
    void searchValue() {
        // Case 1: Term in key
        String result1 = tableService.searchValue("abc");
        assertTrue(result1.contains("<abc> at key"));
        // Case 2: Term in value
        String result2 = tableService.searchValue("bar");
        assertTrue(result2.contains("<bar> at value"));
        // Case 3: Term in both key and value
        String result3 = tableService.searchValue("term");
        assertTrue(result3.contains("<term> at value") || result3.contains("<term> at key"));
        // Case 4: Term not present
        String result4 = tableService.searchValue("none");
        assertTrue(result4.contains("No occurrences found"));
    }

    @Test
    void editCell() {
    }

    @Test
    void addRow() {
    }

    @Test
    void sortRow() throws IOException {
        // Ascending first row
        tableService.sortRow(0, "asc");
        assertEquals("(abc,xyz)", tableService.getTable().get(0).get(0));
        assertEquals("(foo,bar)", tableService.getTable().get(0).get(1));
        assertEquals("(search,term)", tableService.getTable().get(0).get(2));
        // Descending first row
        tableService.sortRow(0, "desc");
        assertEquals("(search,term)", tableService.getTable().get(0).get(0));
        assertEquals("(foo,bar)", tableService.getTable().get(0).get(1));
        assertEquals("(abc,xyz)", tableService.getTable().get(0).get(2));
        // Ascending second row
        tableService.sortRow(1, "asc");
        assertEquals("(abc,term)", tableService.getTable().get(1).get(0));
        assertEquals("(something,else)", tableService.getTable().get(1).get(1));
        assertEquals("(xyz,abc)", tableService.getTable().get(1).get(2));
        // Descending second row
        tableService.sortRow(1, "desc");
        assertEquals("(xyz,abc)", tableService.getTable().get(1).get(0));
        assertEquals("(something,else)", tableService.getTable().get(1).get(1));
        assertEquals("(abc,term)", tableService.getTable().get(1).get(2));
    }

    @Test
    void resetTable() {
    }

    @Test
    void printTable() {
        PrintStream standardOut = System.out;
        ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

        System.setOut(new PrintStream(outputStreamCaptor));

        // Call the method
        tableService.printTable();

        // Restore System.out
        System.setOut(standardOut);

        // Expected output
        String expected = "\n--- Table Contents ---\n"
                + "(abc,xyz) (foo,bar) (search,term)\n"
                + "(abc,term) (xyz,abc) (something,else)\n";
        String actual = outputStreamCaptor.toString();
        // Normalize line endings and trim
        String normExpected = expected.replace("\r\n", "\n").trim();
        String normActual = actual.replace("\r\n", "\n").trim();
        assertEquals(normExpected, normActual);
    }

    @Test
    void getTable() {
        assertEquals(2, tableService.getTable().size());
        assertEquals(3, tableService.getTable().get(0).size());
        assertEquals("(abc,xyz)", tableService.getTable().get(0).get(0));
        assertEquals("(foo,bar)", tableService.getTable().get(0).get(1));
        assertEquals("(search,term)", tableService.getTable().get(0).get(2));
        assertEquals(3, tableService.getTable().get(1).size());
        assertEquals("(abc,term)", tableService.getTable().get(1).get(0));
        assertEquals("(xyz,abc)", tableService.getTable().get(1).get(1));
        assertEquals("(something,else)", tableService.getTable().get(1).get(2));
    }
}