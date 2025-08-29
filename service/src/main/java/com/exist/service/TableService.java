package com.exist.service;

import com.exist.model.Table;

import java.io.IOException;

public interface TableService {

	void loadTableFromFile(String fileName) throws IOException;

	String searchValue(String searchTerm);

    void editCell(int rowIndex, int columnIndex, String newKey, String newValue, String editMode);

    void addRow(int numberOfCells);

    void sortRow(int rowIndex, String order);

    void resetTable(int rows, int columns);

	void printTable();

	Table getTable();
}