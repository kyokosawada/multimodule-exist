package com.exist.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import com.exist.service.impl.FileServiceImpl;

import java.nio.file.Files;
import java.nio.file.Path;

import com.exist.model.Table;

import java.util.Arrays;

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

        // Case 4: args[0] is a valid file with a real path backing it
        Path tempFile = Files.createTempFile("test", ".txt");
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
        Path tempFile = Files.createTempFile("exist", ".txt");
        try {
            assertTrue(fileService.fileExists(tempFile.toString()));
        } finally {
            Files.deleteIfExists(tempFile);
        }

        // Non-existent file
        assertFalse(fileService.fileExists("missing.txt"));

    }

    @Test
    void loadFileContent() throws Exception {
        FileService fileService = new FileServiceImpl();
        // Test loading a file with known content
        Path tempFile = Files.createTempFile("test", ".txt");
        String content = "(sl5,Y']) (d+8,jXO)";
        Files.writeString(tempFile, content);
        try {
            assertEquals(content, fileService.loadFileContent(tempFile.toString()));
        } finally {
            Files.deleteIfExists(tempFile);
        }

        // Test loading empty file
        Path emptyFile = Files.createTempFile("empty", ".txt");
        try {
            assertEquals("", fileService.loadFileContent(emptyFile.toString()));
        } finally {
            Files.deleteIfExists(emptyFile);
        }

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
        t.add(Arrays.asList("(x,y)"));
        assertEquals("(x,y)", fileService.tableToString(t));

        // Case 3: Two rows, two cells each
        t = new Table();
        t.add(Arrays.asList("(a,b)", "(c,d)"));
        t.add(Arrays.asList("(x,y)", "(z,w)"));
        assertEquals("(a,b) (c,d)\n(x,y) (z,w)", fileService.tableToString(t));

    }

    @Test
    void saveFile() throws Exception {
        FileService fileService = new FileServiceImpl();
        // Case 1: Save and read back single row
        Table t = new Table();
        t.add(Arrays.asList("(foo,bar)", "(x,y)"));
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
        t.add(Arrays.asList("(a,b)", "(c,d)"));
        t.add(Arrays.asList("(1,2)", "(3,4)"));
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
        t.add(Arrays.asList("(foo,bar)"));
        Path overwriteFile = Files.createTempFile("savefile_overwrite", ".txt");
        try {
            fileService.saveFile(t, overwriteFile.toString());
            t = new Table();
            t.add(Arrays.asList("(new,val)"));
            fileService.saveFile(t, overwriteFile.toString());
            String content = Files.readString(overwriteFile);
            assertEquals("(new,val)", content);
        } finally {
            Files.deleteIfExists(overwriteFile);
        }
    }
}