package com.exist.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import com.exist.service.impl.FileServiceImpl;

import java.nio.file.Files;
import java.nio.file.Path;

import com.exist.model.Table;

import java.util.ArrayList;

class FileServiceImplTest {

    @Test
    void getFileName() throws Exception {
        FileService fileService = new FileServiceImpl();

        // Case 1: args.length == 0
        Exception ex1 = assertThrows(Exception.class, () -> fileService.getFileName(new String[]{}));
        assertTrue(ex1.getMessage().contains("No filename provided"));

        // Case 2: args[0] == "" (empty string)
        Exception ex2 = assertThrows(Exception.class, () -> fileService.getFileName(new String[]{""}));
        assertTrue(ex2.getMessage().contains("Filename cannot be empty"));

        // Case 3: args[0] is a file that doesn't exist
        String missingFile = "definitely_missing_file.txt";
        Exception ex3 = assertThrows(Exception.class, () -> fileService.getFileName(new String[]{missingFile}));
        assertTrue(ex3.getMessage().contains("not found"));

        // Case 4: args[0] is a valid temp file
        Path tempFile = Files.createTempFile("filesvc_test", ".tmp");
        try {
            String result = fileService.getFileName(new String[]{tempFile.toString()});
            assertEquals(tempFile.toString(), result);
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    @Test
    void fileExists() throws Exception {
        FileService fileService = new FileServiceImpl();
        // Valid file
        Path tempFile = Files.createTempFile("fexists_test", ".tmp");
        try {
            assertTrue(fileService.fileExists(tempFile.toString()));
        } finally {
            Files.deleteIfExists(tempFile);
        }

        // Non-existent file
        assertFalse(fileService.fileExists("no_such_file.txt"));

        // Directory case
        Path tempDir = Files.createTempDirectory("fexists_dir_test");
        try {
            assertFalse(fileService.fileExists(tempDir.toString()));
        } finally {
            Files.deleteIfExists(tempDir);
        }
    }

    @Test
    void loadFileContent() throws Exception {
        FileService fileService = new FileServiceImpl();
        // Test loading a file with known content
        Path tempFile = Files.createTempFile("loadcontent_test", ".txt");
        String content = "Hello, world!\nSecond line.";
        Files.writeString(tempFile, content);
        try {
            assertEquals(content, fileService.loadFileContent(tempFile.toString()));
        } finally {
            Files.deleteIfExists(tempFile);
        }

        // Test loading empty file
        Path emptyFile = Files.createTempFile("loadcontent_empty", ".txt");
        try {
            assertEquals("", fileService.loadFileContent(emptyFile.toString()));
        } finally {
            Files.deleteIfExists(emptyFile);
        }

        // Test non-existent file
        String missingFile = "no_such_loadcontent.txt";
        assertThrows(java.io.IOException.class, () -> fileService.loadFileContent(missingFile));

        // Test resource load if file exists
        try {
            String defaultContent = fileService.loadFileContent(FileService.DEFAULT_RESOURCE);
            assertNotNull(defaultContent); // If exists, should not be null
        } catch (java.io.IOException ex) {
            // Acceptable if resource is missing
            assertTrue(ex.getMessage().contains("default.txt not found"));
        }
    }

    @Test
    void parseFileToTable() {
        FileService fileService = new FileServiceImpl();

        // Case 1: Empty content
        Table t1 = fileService.parseFileToTable("");
        assertEquals(0, t1.size());

        // Case 2: One row, two cells
        String row = "(a,b) (c,d)";
        Table t2 = fileService.parseFileToTable(row);
        assertEquals(1, t2.size());
        assertEquals(2, t2.get(0).size());
        assertEquals("(a,b)", t2.get(0).get(0));
        assertEquals("(c,d)", t2.get(0).get(1));

        // Case 3: Two rows, each with two cells
        String multi = "(x,1) (y,2)\n(m,n) (p,q)";
        Table t3 = fileService.parseFileToTable(multi);
        assertEquals(2, t3.size());
        assertEquals("(x,1)", t3.get(0).get(0));
        assertEquals("(y,2)", t3.get(0).get(1));
        assertEquals("(m,n)", t3.get(1).get(0));
        assertEquals("(p,q)", t3.get(1).get(1));

        // Case 4: Malformed line
        String mal = "random text\n(a,b)";
        Table t4 = fileService.parseFileToTable(mal);
        assertEquals(1, t4.size()); // Only one row, from valid line
        assertEquals("(a,b)", t4.get(0).get(0));

        // Case 5: Line with no valid cell
        String noval = "just garbage";
        Table t5 = fileService.parseFileToTable(noval);
        assertEquals(0, t5.size());
    }

    @Test
    void tableToString() {
        FileService fileService = new FileServiceImpl();
        Table t;

        // Case 1: Empty table
        t = new Table();
        assertEquals("", fileService.tableToString(t));

        // Case 2: Single row, single cell
        t = new Table();
        ArrayList<String> row1 = new ArrayList<>();
        row1.add("(x,y)");
        t.add(row1);
        assertEquals("(x,y)", fileService.tableToString(t));

        // Case 3: Two rows, two cells each
        t = new Table();
        ArrayList<String> rowA = new ArrayList<>();
        rowA.add("(a,b)");
        rowA.add("(c,d)");
        ArrayList<String> rowB = new ArrayList<>();
        rowB.add("(x,y)");
        rowB.add("(z,w)");
        t.add(rowA);
        t.add(rowB);
        assertEquals("(a,b) (c,d)\n(x,y) (z,w)", fileService.tableToString(t));

        // Case 4: Table with empty row
        t = new Table();
        ArrayList<String> emptyRow = new ArrayList<>();
        t.add(emptyRow);
        assertEquals("", fileService.tableToString(t));

        // Case 5: Row with empty cell
        t = new Table();
        ArrayList<String> rowE = new ArrayList<>();
        rowE.add("");
        rowE.add("(q,r)");
        t.add(rowE);
        assertEquals(" (q,r)", fileService.tableToString(t));
    }

    @Test
    void saveFile() throws Exception {
        FileService fileService = new FileServiceImpl();
        // Case 1: Save and read back single row
        Table t = new Table();
        ArrayList<String> row = new ArrayList<>();
        row.add("(foo,bar)");
        row.add("(x,y)");
        t.add(row);
        Path file = Files.createTempFile("savefile_test", ".txt");
        try {
            fileService.saveFile(t, file.toString());
            String content = Files.readString(file);
            assertEquals("(foo,bar) (x,y)", content);
        } finally {
            Files.deleteIfExists(file);
        }

        // Case 2: Save and read back multiple rows
        t = new Table();
        ArrayList<String> r1 = new ArrayList<>();
        r1.add("(a,b)");
        r1.add("(c,d)");
        ArrayList<String> r2 = new ArrayList<>();
        r2.add("(1,2)");
        r2.add("(3,4)");
        t.add(r1);
        t.add(r2);
        file = Files.createTempFile("savefile_multi", ".txt");
        try {
            fileService.saveFile(t, file.toString());
            String content = Files.readString(file);
            assertEquals("(a,b) (c,d)\n(1,2) (3,4)", content);
        } finally {
            Files.deleteIfExists(file);
        }

        // Case 3: Empty Table
        t = new Table();
        file = Files.createTempFile("savefile_empty", ".txt");
        try {
            fileService.saveFile(t, file.toString());
            String content = Files.readString(file);
            assertEquals("", content);
        } finally {
            Files.deleteIfExists(file);
        }

        // Case 4: Overwrite
        t = new Table();
        ArrayList<String> r3 = new ArrayList<>();
        r3.add("(foo,bar)");
        t.add(r3);
        Path overwriteFile = Files.createTempFile("savefile_overwrite", ".txt");
        try {
            fileService.saveFile(t, overwriteFile.toString());
            t = new Table();
            ArrayList<String> r4 = new ArrayList<>();
            r4.add("(new,val)");
            t.add(r4);
            fileService.saveFile(t, overwriteFile.toString());
            String content = Files.readString(overwriteFile);
            assertEquals("(new,val)", content);
        } finally {
            Files.deleteIfExists(overwriteFile);
        }

        // Case 5: Attempt to save to directory
        Path tempDir = Files.createTempDirectory("savefile_dir");
        try {
            Table dummy = new Table();
            assertThrows(java.io.IOException.class, () -> fileService.saveFile(dummy, tempDir.toString()));
        } finally {
            Files.deleteIfExists(tempDir);
        }
    }
}