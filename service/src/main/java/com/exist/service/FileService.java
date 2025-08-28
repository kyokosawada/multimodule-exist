package com.exist.service;

import java.util.List;
import java.io.IOException;

import com.exist.model.Table;

public interface FileService {

    public static final String DEFAULT_RESOURCE = "default.txt";

    String getFileName(String[] args) throws Exception;

    boolean fileExists(String fileName);

    String loadFileContent(String fileName) throws IOException;

    Table parseFileToTable(String content);

    List<String> parseLineToRow(String line, int lineIndex);

    String tableToString(Table table);

    void saveFile(Table table, String fileName) throws IOException;

}