package com.exist.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import com.exist.service.impl.FileServiceImpl;

import java.nio.file.Files;
import java.nio.file.Path;

import com.exist.model.Table;

import java.util.Arrays;
import java.io.IOException;

@DisplayName("FileServiceImpl Tests")
class FileServiceImplTest {

    private final FileService fileService = new FileServiceImpl();

    @Nested
    @DisplayName("getFileName() Tests")
    class GetFileNameTests {

        @Test
        @DisplayName("should throw exception when no filename is provided")
        void getFileName_noArgs_throwsException() {
            Exception ex = assertThrows(Exception.class, () -> fileService.getFileName(new String[]{}));
            assertTrue(ex.getMessage().contains("No filename provided"));
        }

        @Test
        @DisplayName("should throw exception for empty filename")
        void getFileName_emptyArg_throwsException() {
            Exception ex = assertThrows(Exception.class, () -> fileService.getFileName(new String[]{""}));
            assertTrue(ex.getMessage().contains("Filename cannot be empty"));
        }

        @Test
        @DisplayName("should throw exception for non-existent file")
        void getFileName_missingFile_throwsException() {
            String missingFile = "definitely_missing_file.txt";
            Exception ex = assertThrows(Exception.class, () -> fileService.getFileName(new String[]{missingFile}));
            assertTrue(ex.getMessage().contains("not found"));
        }

        @Test
        @DisplayName("should return the filename for a valid file")
        void getFileName_validFile_returnsPath() throws Exception {
            Path tempFile = Files.createTempFile("test", ".txt");
            try {
                String result = fileService.getFileName(new String[]{tempFile.toString()});
                assertEquals(tempFile.toString(), result);
            } finally {
                Files.deleteIfExists(tempFile);
            }
        }
    }

    @Nested
    @DisplayName("fileExists() Tests")
    class FileExistsTests {

        @Test
        @DisplayName("should return true for an existing file")
        void fileExists_existingFile_returnsTrue() throws Exception {
            Path tempFile = Files.createTempFile("exist", ".txt");
            try {
                assertTrue(fileService.fileExists(tempFile.toString()));
            } finally {
                Files.deleteIfExists(tempFile);
            }
        }

        @Test
        @DisplayName("should return false for a non-existent file")
        void fileExists_missingFile_returnsFalse() {
            assertFalse(fileService.fileExists("missing.txt"));
        }
    }

    @Nested
    @DisplayName("loadFileContent() Tests")
    class LoadFileContentTests {

        @Test
        @DisplayName("should correctly load content from a file")
        void loadFileContent_withContent_returnsContent() throws Exception {
            Path tempFile = Files.createTempFile("test", ".txt");
            String content = "(sl5,Y']) (d+8,jXO)";
            Files.writeString(tempFile, content);
            try {
                assertEquals(content, fileService.loadFileContent(tempFile.toString()));
            } finally {
                Files.deleteIfExists(tempFile);
            }
        }

        @Test
        @DisplayName("should return empty string for an empty file")
        void loadFileContent_emptyFile_returnsEmptyString() throws Exception {
            Path emptyFile = Files.createTempFile("empty", ".txt");
            try {
                assertEquals("", fileService.loadFileContent(emptyFile.toString()));
            } finally {
                Files.deleteIfExists(emptyFile);
            }
        }

        @Test
        @DisplayName("should load default resource if it exists")
        void loadFileContent_defaultResource_loadsContent() {
            try {
                String defaultContent = fileService.loadFileContent(FileService.DEFAULT_RESOURCE);
                assertNotNull(defaultContent);
            } catch (IOException ex) {
                // This is okay if the resource isn't present during test
                assertTrue(ex.getMessage().contains("default.txt not found"));
            }
        }
    }

    @Nested
    @DisplayName("parseFileToTable() Tests")
    class ParseFileToTableTests {

        @Test
        @DisplayName("should return an empty table for empty content")
        void parseFileToTable_emptyContent_returnsEmptyTable() {
            Table t = fileService.parseFileToTable("");
            assertEquals(0, t.size());
        }

        @Test
        @DisplayName("should correctly parse a single row with two cells")
        void parseFileToTable_singleRow_parsesCorrectly() {
            String row = "(a,b) (c,d)";
            Table t = fileService.parseFileToTable(row);
            assertAll("Single Row Parsing",
                    () -> assertEquals(1, t.size()),
                    () -> assertEquals(2, t.get(0).size()),
                    () -> assertEquals("(a,b)", t.get(0).get(0)),
                    () -> assertEquals("(c,d)", t.get(0).get(1))
            );
        }

        @Test
        @DisplayName("should correctly parse multiple rows")
        void parseFileToTable_multipleRows_parsesCorrectly() {
            String multi = "(x,1) (y,2)\n(m,n) (p,q)";
            Table t = fileService.parseFileToTable(multi);
            assertAll("Multi-row Parsing",
                    () -> assertEquals(2, t.size()),
                    () -> assertEquals("(x,1)", t.get(0).get(0)),
                    () -> assertEquals("(y,2)", t.get(0).get(1)),
                    () -> assertEquals("(m,n)", t.get(1).get(0)),
                    () -> assertEquals("(p,q)", t.get(1).get(1))
            );
        }
    }

    @Nested
    @DisplayName("tableToString() Tests")
    class TableToStringTests {

        @Test
        @DisplayName("should return an empty string for an empty table")
        void tableToString_emptyTable_returnsEmptyString() {
            assertEquals("", fileService.tableToString(new Table()));
        }

        @Test
        @DisplayName("should correctly format a single row, single cell table")
        void tableToString_singleCell_formatsCorrectly() {
            Table t = new Table();
            t.add(Arrays.asList("(x,y)"));
            assertEquals("(x,y)", fileService.tableToString(t));
        }

        @Test
        @DisplayName("should correctly format a multi-row, multi-cell table")
        void tableToString_multiCell_formatsCorrectly() {
            Table t = new Table();
            t.add(Arrays.asList("(a,b)", "(c,d)"));
            t.add(Arrays.asList("(x,y)", "(z,w)"));
            assertEquals("(a,b) (c,d)\n(x,y) (z,w)", fileService.tableToString(t));
        }
    }

    @Nested
    @DisplayName("saveFile() Tests")
    class SaveFileTests {

        @Test
        @DisplayName("should correctly save a single row table")
        void saveFile_singleRow_savesCorrectly() throws Exception {
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
        }

        @Test
        @DisplayName("should correctly save a multi-row table")
        void saveFile_multiRow_savesCorrectly() throws Exception {
            Table t = new Table();
            t.add(Arrays.asList("(a,b)", "(c,d)"));
            t.add(Arrays.asList("(1,2)", "(3,4)"));
            Path file = Files.createTempFile("savefile_multi", ".txt");
            try {
                fileService.saveFile(t, file.toString());
                String content = Files.readString(file);
                assertEquals("(a,b) (c,d)\n(1,2) (3,4)", content);
            } finally {
                Files.deleteIfExists(file);
            }
        }

        @Test
        @DisplayName("should save an empty string for an empty table")
        void saveFile_emptyTable_savesEmptyFile() throws Exception {
            Path file = Files.createTempFile("savefile_empty", ".txt");
            try {
                fileService.saveFile(new Table(), file.toString());
                String content = Files.readString(file);
                assertEquals("", content);
            } finally {
                Files.deleteIfExists(file);
            }
        }

        @Test
        @DisplayName("should overwrite an existing file")
        void saveFile_overwritesFile_overwritesCorrectly() throws Exception {
            Path overwriteFile = Files.createTempFile("savefile_overwrite", ".txt");
            try {
                Table t1 = new Table();
                t1.add(Arrays.asList("(foo,bar)"));
                fileService.saveFile(t1, overwriteFile.toString());

                Table t2 = new Table();
                t2.add(Arrays.asList("(new,val)"));
                fileService.saveFile(t2, overwriteFile.toString());

                String content = Files.readString(overwriteFile);
                assertEquals("(new,val)", content);
            } finally {
                Files.deleteIfExists(overwriteFile);
            }
        }
    }
}