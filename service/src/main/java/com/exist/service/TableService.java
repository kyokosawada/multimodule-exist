package com.exist.service;

import com.exist.model.Table;

import java.io.IOException;

public interface TableService {

	void loadTableFromFile(String fileName) throws IOException;

	String searchValue(String searchTerm);


	void editCell(int rowIndex, int columnIndex, String newKey, String newValue, String editMode) throws IOException;

	void addRow(int numberOfCells) throws IOException;

	void sortRow(int rowIndex, String order) throws IOException;

	void resetTable(int rows, int columns) throws IOException;

	void printTable();

	Table getTable();
}