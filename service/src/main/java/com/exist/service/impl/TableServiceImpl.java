package com.exist.service.impl;

import com.exist.model.Table;
import com.exist.service.FileService;
import com.exist.service.TableService;
import com.exist.utilities.AsciiUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.IOException;

public class TableServiceImpl implements TableService {

    private Table table = new Table();
    private FileService fileService;

    private static final int ASCII_STRING_LENGTH = 3;

    public TableServiceImpl() {
        this.fileService = new FileServiceImpl();
    }

    public TableServiceImpl(FileService fileService) {
        this.fileService = fileService;
    }

    @Override
    public void loadTableFromFile(String fileName) throws IOException {
        String content = fileService.loadFileContent(fileName);
        this.table = fileService.parseFileToTable(content);
    }

    @Override
    public String searchValue(String searchTerm) {
        boolean found = false;
        StringBuilder result = new StringBuilder();

        for (int rowIndex = 0; rowIndex < table.size(); rowIndex++) {
            for (int colIndex = 0; colIndex < table.get(rowIndex).size(); colIndex++) {
                String cellValue = table.get(rowIndex).get(colIndex);
                String key = extractKey(cellValue);
                String value = extractValue(cellValue);

                int keyOccurrences = countOccurrences(key, searchTerm);
                int valueOccurrences = countOccurrences(value, searchTerm);

                if (keyOccurrences > 0 && valueOccurrences > 0) {
                    result.append(keyOccurrences)
                            .append(" <")
                            .append(searchTerm)
                            .append("> at key and ")
                            .append(valueOccurrences)
                            .append(" <")
                            .append(searchTerm)
                            .append("> at value of [")
                            .append(rowIndex)
                            .append(",")
                            .append(colIndex)
                            .append("]\n");
                    found = true;
                    continue;
                }

                if (keyOccurrences > 0) {
                    result.append(keyOccurrences)
                            .append(" <")
                            .append(searchTerm)
                            .append("> at key of [")
                            .append(rowIndex)
                            .append(",")
                            .append(colIndex)
                            .append("]\n");
                    found = true;
                    continue;
                }

                if (valueOccurrences > 0) {
                    result.append(valueOccurrences)
                            .append(" <")
                            .append(searchTerm)
                            .append("> at value of [")
                            .append(rowIndex)
                            .append(",")
                            .append(colIndex)
                            .append("]\n");
                    found = true;

                }
            }
        }

        if (!found) {
            result.append("No occurrences found in the table\n");
        }
        return result.toString();
    }

    private int countOccurrences(String text, String searchTerm) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(searchTerm, index)) != -1) {
            count++;
            index++;
        }
        return count;
    }

    @Override
    public void editCell(int rowIndex, int columnIndex, String newKey, String newValue, String editMode) {
        String oldKey = extractKey(table.get(rowIndex).get(columnIndex));
        String oldValue = extractValue(table.get(rowIndex).get(columnIndex));

        String finalKey = oldKey;
        String finalValue = oldValue;

        switch (editMode.toLowerCase()) {
            case "key":
                finalKey = newKey;
                break;
            case "value":
                finalValue = newValue;
                break;
            case "both":
                finalKey = newKey;
                finalValue = newValue;
                break;
            default:
                System.out.println("Invalid edit mode.");
                return;
        }

        String newCell = "(" + finalKey + "," + finalValue + ")";
        table.get(rowIndex).set(columnIndex, newCell);

        printTable();
    }

    @Override
    public void addRow(int numberOfCells) {
        List<String> newRow = new ArrayList<>();

        for (int i = 0; i < numberOfCells; i++) {
            String randomKey = AsciiUtils.generateRandomAscii(ASCII_STRING_LENGTH);
            String randomValue = AsciiUtils.generateRandomAscii(ASCII_STRING_LENGTH);
            newRow.add("(" + randomKey + "," + randomValue + ")");
        }

        table.add(newRow);
        printTable();
    }

    @Override
    public void sortRow(int rowIndex, String order) {
        switch (order.toLowerCase()) {
            case "asc":
                Collections.sort(table.get(rowIndex));
                break;
            case "desc":
                Collections.sort(table.get(rowIndex), Collections.reverseOrder());
                break;
        }

        printTable();
    }

    @Override
    public void resetTable(int rows, int columns) {
        table.clear();

        for (int i = 0; i < rows; i++) {
            List<String> row = new ArrayList<>();
            for (int j = 0; j < columns; j++) {
                String randomKey = AsciiUtils.generateRandomAscii(ASCII_STRING_LENGTH);
                String randomValue = AsciiUtils.generateRandomAscii(ASCII_STRING_LENGTH);
                row.add("(" + randomKey + "," + randomValue + ")");
            }
            table.add(row);
        }

        printTable();
    }

    @Override
    public void printTable() {
        System.out.println("\n--- Table Contents ---");
        for (int i = 0; i < table.size(); i++) {
            for (int j = 0; j < table.get(i).size(); j++) {
                if (j > 0) System.out.print(" ");
                System.out.print(table.get(i).get(j));
            }
            System.out.println();
        }
    }

    private String extractKey(String cellValue) {
        String content = cellValue.substring(1, cellValue.length() - 1);
        String[] parts = content.split(",", 2);
        return parts[0];
    }

    private String extractValue(String cellValue) {
        String content = cellValue.substring(1, cellValue.length() - 1);
        String[] parts = content.split(",", 2);
        return parts[1];
    }

    @Override
    public Table getTable() {
        return table;
    }


}