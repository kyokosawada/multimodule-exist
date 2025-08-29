package com.exist.service;

import com.exist.service.impl.TableServiceImpl;
import com.exist.model.Table;

import java.util.Arrays;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.regex.Pattern;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TableServiceImplTest {

    private static final Pattern CELL_PATTERN = Pattern.compile("\\([^,]+,[^)]+\\)");

    TableService tableService;

    @BeforeEach
    void setUp(){
        tableService = new TableServiceImpl();
        tableService.getTable().add(Arrays.asList("(abc,xyz)", "(foo,bar)", "(search,term)"));
        tableService.getTable().add(Arrays.asList("(abc,term)", "(xyz,abc)", "(something,else)"));
    }

    @Test
    void loadTableFromFile() throws Exception {
        FileService mockFileService = mock(FileService.class);
        String fileContent = "(abc,xyz) (foo,bar)\n(abc,term) (xyz,abc)";
        when(mockFileService.loadFileContent("dummy.txt")).thenReturn(fileContent);

        // Build expected table to return for parseFileToTable
        Table expectedTable = new Table();
        expectedTable.add(Arrays.asList("(abc,xyz)", "(foo,bar)"));
        expectedTable.add(Arrays.asList("(abc,term)", "(xyz,abc)"));
        when(mockFileService.parseFileToTable(fileContent)).thenReturn(expectedTable);

        TableService ts = new TableServiceImpl(mockFileService);
        ts.loadTableFromFile("dummy.txt");
        Table t = ts.getTable();
        assertEquals(2, t.size());
        assertEquals(2, t.get(0).size());
        assertEquals("(abc,xyz)", t.get(0).get(0));
        assertEquals("(foo,bar)", t.get(0).get(1));
        assertEquals(2, t.get(1).size());
        assertEquals("(abc,term)", t.get(1).get(0));
        assertEquals("(xyz,abc)", t.get(1).get(1));
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
        // Edit key only
        tableService.editCell(0, 0, "editedKey", "", "key");
        assertEquals("(editedKey,xyz)", tableService.getTable().get(0).get(0));
        // Edit value only
        tableService.editCell(0, 1, "", "editedValue", "value");
        assertEquals("(foo,editedValue)", tableService.getTable().get(0).get(1));
        // Edit both key and value
        tableService.editCell(1, 2, "bothKey", "bothValue", "both");
        assertEquals("(bothKey,bothValue)", tableService.getTable().get(1).get(2));
    }

    @Test
    void addRow() {
        int initialRows = tableService.getTable().size();
        tableService.addRow(2);
        assertEquals(initialRows + 1, tableService.getTable().size());
        assertEquals(2, tableService.getTable().get(initialRows).size());
        for (String cell : tableService.getTable().get(initialRows)) {
            assertTrue(CELL_PATTERN.matcher(cell).matches());
        }
    }

    @Test
    void sortRow() {
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
        tableService.resetTable(3, 4);
        assertEquals(3, tableService.getTable().size());
        for (int i = 0; i < 3; i++) {
            assertEquals(4, tableService.getTable().get(i).size());
            for (String cell : tableService.getTable().get(i)) {
                assertTrue(CELL_PATTERN.matcher(cell).matches());
            }
        }
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