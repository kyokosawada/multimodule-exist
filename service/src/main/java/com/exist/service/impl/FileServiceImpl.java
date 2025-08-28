package com.exist.service.impl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;
import java.util.ArrayList;

import com.exist.model.Table;
import com.exist.service.FileService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;


public class FileServiceImpl implements FileService {

    private static final Pattern CELL_PATTERN = Pattern.compile("\\([^,]*,[^)]*\\)");

    @Override
    public String getFileName(String[] args) throws Exception {

        if (args.length == 0) {
            throw new Exception("No filename provided.");
        }

        String fileName = args[0];

        if (StringUtils.isEmpty(fileName)) {
            throw new Exception("Filename cannot be empty.");
        }

        if (!fileExists(fileName)) {
            throw new Exception("File '" + fileName + "' not found or not readable.");
        }
        return fileName;
    }

    @Override
    public boolean fileExists(String fileName) {
        return FileUtils.getFile(fileName).canRead();
    }

    @Override
    public String loadFileContent(String fileName) throws IOException {
        if (DEFAULT_RESOURCE.equals(fileName)) {
            try (InputStream in = getClass().getClassLoader().getResourceAsStream(DEFAULT_RESOURCE)) {
                if (in == null)
                    throw new IOException("default.txt not found in resources");
                return new String(in.readAllBytes(), StandardCharsets.UTF_8);
            }
        }
        if (FileUtils.getFile(fileName).canRead()) {
            return FileUtils.readFileToString(FileUtils.getFile(fileName), StandardCharsets.UTF_8);
        }
        throw new IOException(fileName + " not found.");
    }

    @Override
    public Table parseFileToTable(String content) {
        Table table = new Table();

        if (content.isEmpty()) {
            System.out.println("File is empty, returning empty table.");
            return table;
        }

        String[] lines = content.split("\\r?\\n");

        for (int lineIndex = 0; lineIndex < lines.length; lineIndex++) {
            String line = lines[lineIndex];

            if (!parseLineToRow(line, lineIndex).isEmpty()) {
                table.add(parseLineToRow(line, lineIndex));
            }
        }

        return table;
    }

    @Override
    public List<String> parseLineToRow(String line, int lineIndex) {
        List<String> rowCells = new ArrayList<>();
        Matcher matcher = CELL_PATTERN.matcher(line);

        while (matcher.find()) {
            String fullMatch = matcher.group(0); 
            rowCells.add(fullMatch);
        }

        return rowCells;
    }

    @Override
    public String tableToString(Table table) {
        StringBuilder content = new StringBuilder();

        for (int i = 0; i < table.size(); i++) {
            for (int j = 0; j < table.get(i).size(); j++) {
                if (j > 0) {
                    content.append(" ");
                }
                content.append(table.get(i).get(j));
            }

            if (i < table.size() - 1) {
                content.append("\n");
            }
        }

        return content.toString();
    }

    @Override
    public void saveFile(Table table, String fileName) throws IOException {
        String content = tableToString(table);
        FileUtils.writeStringToFile(FileUtils.getFile(fileName), content, StandardCharsets.UTF_8);
    }

}