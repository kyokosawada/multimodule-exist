package com.exist.app;

import com.exist.service.TableService;
import com.exist.service.impl.TableServiceImpl;
import com.exist.service.FileService;
import com.exist.service.impl.FileServiceImpl;
import com.exist.utilities.ScanUtils;


import java.io.IOException;

public class MenuManager {

    private TableService tableService = new TableServiceImpl();
    private FileService fileService = new FileServiceImpl();
    private String fileName;

    public void startApplication(String fileName) {
        try {
            tableService.loadTableFromFile(fileName);
            tableService.printTable();
            this.fileName = fileName; // Capture for later saves
        } catch (Exception e) {
            System.out.println("Error loading file: " + e.getMessage());
            System.exit(1);
        }
    }

    public void displayMenu() {
        boolean exit = false;

        while (!exit) {
            System.out.println("\n=== MENU ===");
            System.out.println("[ search ] - Search");
            System.out.println("[ edit ] - Edit");
            System.out.println("[ print ] - Print");
            System.out.println("[ add_row ] - Add Row");
            System.out.println("[ sort ] - Sort");
            System.out.println("[ reset ] - Reset");
            System.out.println("[ x ] - Exit");
            String choice = ScanUtils.getUserInput("Choose an action: ");

            switch (choice.toLowerCase()) {
                case "search" -> handleSearch();
                case "edit" -> handleEdit();
                case "print" -> handlePrint();
                case "add_row" -> handleAddRow();
                case "sort" -> handleSort();
                case "reset" -> handleReset();
                case "x" -> exit = true;
                default -> System.out.println("Invalid action. Please try again.");
            }
        }
    }
    
    private void handleSearch() {
        String searchTerm = ScanUtils.getUserInput("Enter search term: ");

        if (searchTerm.trim().isEmpty()) {
            System.out.println("Search term cannot be empty. Please enter a valid search term.");
            return;
        }

        String result = tableService.searchValue(searchTerm);
        System.out.print(result);
    }

    private void handleEdit() {
        try {
            String position = ScanUtils.getUserInput("Enter cell position [row,column]: ");

            if (!position.matches("\\d+,\\d+")) {
                System.out.println("Invalid format.");
                return;
            }

            String[] parts = position.split(",");

            int rowIndex = Integer.parseInt(parts[0].trim());
            int columnIndex = Integer.parseInt(parts[1].trim());

            if (rowIndex < 0 || rowIndex >= tableService.getTable().size()) {
                System.out.println("Invalid row index");
                return;
            }

            if (columnIndex < 0 || columnIndex >= tableService.getTable().get(rowIndex).size()) {
                System.out.println("Invalid column index");
                return;
            }

            String editMode = ScanUtils.getUserInput("Edit key, value or both? [key/value/both]: ");

            String newKey = "";
            String newValue = "";

            switch (editMode.toLowerCase()) {
                case "key":
                    newKey = ScanUtils.getUserInput("Enter new key: ");
                    break;
                case "value":
                    newValue = ScanUtils.getUserInput("Enter new value: ");
                    break;
                case "both":
                    newKey = ScanUtils.getUserInput("Enter new key: ");
                    newValue = ScanUtils.getUserInput("Enter new value: ");
                    break;
                default:
                    System.out.println("Invalid edit mode. Please use 'key', 'value', or 'both'");
                    return;
            }

            tableService.editCell(rowIndex, columnIndex, newKey, newValue, editMode);
            // Save after edit
            fileService.saveFile(tableService.getTable(), fileName);

        } catch (NumberFormatException e) {
            System.out.println("Invalid number format. Please enter valid row and column numbers.");
        } catch (IOException e) {
            System.out.println("Error saving: " + e.getMessage());
        } 
    }

    private void handlePrint() {
        tableService.printTable();
    }

    private void handleAddRow() {
        try {
            String input = ScanUtils.getUserInput("Number of cells to add: ");
            int numberOfCells = Integer.parseInt(input);

            if (numberOfCells <= 0) {
                System.out.println("Number of cells must be positive. Please enter a number greater than 0.");
                return;
            }

            tableService.addRow(numberOfCells);
            fileService.saveFile(tableService.getTable(), fileName);

        } catch (NumberFormatException e) {
            System.out.println("Invalid number format. Please enter a valid number.");
        } catch (IOException e) {
            System.out.println("Error saving: " + e.getMessage());
        }
    }

    private void handleSort() {
        try {
            String input = ScanUtils.getUserInput("Enter row to sort: ");
            int rowIndex = Integer.parseInt(input);

            if (rowIndex < 0 || rowIndex >= tableService.getTable().size()) {
                System.out.println("Invalid row index.");
                return;
            }

            String order = ScanUtils.getUserInput("Sort order [asc/desc]: ");

            if (!order.equalsIgnoreCase("asc") && !order.equalsIgnoreCase("desc")) {
                System.out.println("Invalid order.");
                return;
            }

            tableService.sortRow(rowIndex, order);
            fileService.saveFile(tableService.getTable(), fileName);

        } catch (NumberFormatException e) {
            System.out.println("Invalid number format. Please enter a valid row number.");
        } catch (IOException e) {
            System.out.println("Error saving: " + e.getMessage());
        }
    }

    private void handleReset() {
        try {
            String dimensions = ScanUtils.getUserInput("Enter table dimensions [ROWSxCOLUMNS]: ");

            if (!dimensions.matches("\\d+x\\d+")) {
                System.out.println("Invalid format.");
                return;
            }

            String[] parts = dimensions.split("x");

            int rows = Integer.parseInt(parts[0].trim());
            int columns = Integer.parseInt(parts[1].trim());

            if (rows <= 0 || columns <= 0) {
                System.out.println("Dimensions must be greater than 0.");
                return;
            }

            tableService.resetTable(rows, columns);
            fileService.saveFile(tableService.getTable(), fileName);

        } catch (NumberFormatException e) {
            System.out.println("Invalid number format. Please enter valid numbers for rows and columns.");
        } catch (IOException e) {
            System.out.println("Error saving: " + e.getMessage());
        }
    }
    
}