package com.exist.service;

import com.exist.model.Table;
import com.exist.service.impl.TableServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Table Service Implementation Tests")
class TableServiceImplTest {

    private static final Pattern CELL_PATTERN = Pattern.compile("\\([^,]+,[^)]+\\)");

    private TableService tableService;

    @BeforeEach
    void setUp() {
        tableService = new TableServiceImpl();
        tableService.getTable().add(Arrays.asList("(abc,xyz)", "(foo,bar)", "(term,term)"));
        tableService.getTable().add(Arrays.asList("(abc,term)", "(xyz,abc)", "(something,else)"));
    }

    @Nested
    @DisplayName("when loading from file")
    @ExtendWith(MockitoExtension.class)
    class LoadFromFileTests {

        @Mock
        private FileService mockFileService;

        @InjectMocks
        private TableServiceImpl tableService;

        @Test
        @DisplayName("should correctly parse content from a mocked file service")
        void loadTableFromFile() throws Exception {
            String fileContent = "(abc,xyz) (foo,bar)\n(abc,term) (xyz,abc)";
            when(mockFileService.loadFileContent("dummy.txt")).thenReturn(fileContent);

            Table expectedTable = new Table();
            expectedTable.add(Arrays.asList("(abc,xyz)", "(foo,bar)"));
            expectedTable.add(Arrays.asList("(abc,term)", "(xyz,abc)"));
            when(mockFileService.parseFileToTable(fileContent)).thenReturn(expectedTable);

            tableService.loadTableFromFile("dummy.txt");
            Table t = tableService.getTable();

            assertAll("Verify table dimensions and content after loading",
                    () -> assertEquals(2, t.size()),
                    () -> assertEquals(2, t.get(0).size()),
                    () -> assertEquals("(abc,xyz)", t.get(0).get(0)),
                    () -> assertEquals("(foo,bar)", t.get(0).get(1)),
                    () -> assertEquals(2, t.get(1).size()),
                    () -> assertEquals("(abc,term)", t.get(1).get(0)),
                    () -> assertEquals("(xyz,abc)", t.get(1).get(1))
            );
        }
    }

    @Nested
    @DisplayName("when searching a value")
    class SearchValueTests {
        @Test
        @DisplayName("should find occurrences in the key")
        void searchValue_whenTermInKey_shouldSucceed() {
            String result = tableService.searchValue("abc");
            assertTrue(result.contains("<abc> at key"));
        }

        @Test
        @DisplayName("should find occurrences in the value")
        void searchValue_whenTermInValue_shouldSucceed() {
            String result = tableService.searchValue("bar");
            assertTrue(result.contains("<bar> at value"));
        }

        @Test
        @DisplayName("should find occurrences in both key and value")
        void searchValue_whenTermInBoth_shouldSucceed() {
            String result = tableService.searchValue("term");
            assertTrue(result.contains("<term> at key and") && result.contains("<term> at value"));
        }

        @Test
        @DisplayName("should return 'not found' message for a non-existent term")
        void searchValue_whenTermNotPresent_shouldSucceed() {
            String result = tableService.searchValue("none");
            assertTrue(result.contains("No occurrences found"));
        }
    }

    @Nested
    @DisplayName("when editing a cell")
    class EditCellTests {
        @Test
        @DisplayName("should update the key only")
        void editCell_whenEditingKey_shouldUpdateKeyOnly() {
            tableService.editCell(0, 0, "editedKey", "", "key");
            assertEquals("(editedKey,xyz)", tableService.getTable().get(0).get(0));
        }

        @Test
        @DisplayName("should update the value only")
        void editCell_whenEditingValue_shouldUpdateValueOnly() {
            tableService.editCell(0, 1, "", "editedValue", "value");
            assertEquals("(foo,editedValue)", tableService.getTable().get(0).get(1));
        }

        @Test
        @DisplayName("should update both the key and value")
        void editCell_whenEditingBoth_shouldUpdateBoth() {
            tableService.editCell(1, 2, "bothKey", "bothValue", "both");
            assertEquals("(bothKey,bothValue)", tableService.getTable().get(1).get(2));
        }

        @Test
        @DisplayName("should show an error message for invalid editing mode")
        void editCell_whenInvalidEditingMode_shouldReturnInvalidModeMessage() {
            PrintStream standardOut = System.out;
            ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

            System.setOut(new PrintStream(outputStreamCaptor));

            tableService.editCell(1, 2, "invalid", "invalid", "invalid");

            System.setOut(standardOut);

            String expected = "Invalid edit mode.\n";
            String actual = outputStreamCaptor.toString();

            String normExpected = expected.replace("\r\n", "\n").trim();
            String normActual = actual.replace("\r\n", "\n").trim();
            assertEquals(normExpected, normActual);

            assertEquals("(something,else)", tableService.getTable().get(1).get(2));
        }
    }

    @Nested
    @DisplayName("when adding a row")
    class AddRowTests {
        @Test
        @DisplayName("should append a new row with the correct number of columns")
        void addRow() {
            int initialRows = tableService.getTable().size();
            tableService.addRow(2);
            assertEquals(initialRows + 1, tableService.getTable().size());
            assertEquals(2, tableService.getTable().get(initialRows).size());
            for (String cell : tableService.getTable().get(initialRows)) {
                assertTrue(CELL_PATTERN.matcher(cell).matches());
            }
        }
    }

    @Nested
    @DisplayName("when sorting a row")
    class SortRowTests {
        @Test
        @DisplayName("should sort the first row in ascending order")
        void sortRow_firstRowAscending_shouldSortCorrectly() {
            tableService.sortRow(0, "asc");
            Table table = tableService.getTable();
            assertAll("Verify ascending sort on first row",
                    () -> assertEquals("(abc,xyz)", table.get(0).get(0)),
                    () -> assertEquals("(foo,bar)", table.get(0).get(1)),
                    () -> assertEquals("(term,term)", table.get(0).get(2))
            );
        }

        @Test
        @DisplayName("should sort the first row in descending order")
        void sortRow_firstRowDescending_shouldSortCorrectly() {
            tableService.sortRow(0, "desc");
            Table table = tableService.getTable();
            assertAll("Verify descending sort on first row",
                    () -> assertEquals("(term,term)", table.get(0).get(0)),
                    () -> assertEquals("(foo,bar)", table.get(0).get(1)),
                    () -> assertEquals("(abc,xyz)", table.get(0).get(2))
            );
        }

        @Test
        @DisplayName("should sort the second row in ascending order")
        void sortRow_secondRowAscending_shouldSortCorrectly() {
            tableService.sortRow(1, "asc");
            Table table = tableService.getTable();
            assertAll("Verify ascending sort on second row",
                    () -> assertEquals("(abc,term)", table.get(1).get(0)),
                    () -> assertEquals("(something,else)", table.get(1).get(1)),
                    () -> assertEquals("(xyz,abc)", table.get(1).get(2))
            );
        }

        @Test
        @DisplayName("should sort the second row in descending order")
        void sortRow_secondRowDescending_shouldSortCorrectly() {
            tableService.sortRow(1, "desc");
            Table table = tableService.getTable();
            assertAll("Verify descending sort on second row",
                    () -> assertEquals("(xyz,abc)", table.get(1).get(0)),
                    () -> assertEquals("(something,else)", table.get(1).get(1)),
                    () -> assertEquals("(abc,term)", table.get(1).get(2))
            );
        }
    }

    @Nested
    @DisplayName("when resetting the table")
    class ResetTableTests {
        @Test
        @DisplayName("should reset the table with given rows and columns")
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
    }

    @Nested
    @DisplayName("when printing the table")
    class PrintTableTests {
        @Test
        @DisplayName("should print the table contents to standard out")
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
                    + "(abc,xyz) (foo,bar) (term,term)\n"
                    + "(abc,term) (xyz,abc) (something,else)\n";
            String actual = outputStreamCaptor.toString();
            // Normalize line endings and trim
            String normExpected = expected.replace("\r\n", "\n").trim();
            String normActual = actual.replace("\r\n", "\n").trim();
            assertEquals(normExpected, normActual);
        }
    }

    @Nested
    @DisplayName("when retrieving the table")
    class GetTableTests {
        @Test
        @DisplayName("should return the current table structure")
        void getTable() {
            Table table = tableService.getTable();
            assertAll("Verify the initial state of the table",
                    () -> assertEquals(2, table.size()),
                    () -> assertEquals(3, table.get(0).size()),
                    () -> assertEquals("(abc,xyz)", table.get(0).get(0)),
                    () -> assertEquals("(foo,bar)", table.get(0).get(1)),
                    () -> assertEquals("(term,term)", table.get(0).get(2)),
                    () -> assertEquals(3, table.get(1).size()),
                    () -> assertEquals("(abc,term)", table.get(1).get(0)),
                    () -> assertEquals("(xyz,abc)", table.get(1).get(1)),
                    () -> assertEquals("(something,else)", table.get(1).get(2))
            );
        }
    }
}