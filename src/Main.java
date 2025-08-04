import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.nio.file.Files;
import java.nio.file.Path;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

public class Main {
    static final Scanner scanner = new Scanner(System.in);
    static List<String> userList = new ArrayList<>();
    static String userChoice;
    static boolean quit = false;
    static String fileName = ""; // Full path of the currently opened/saved list
    static boolean fileNeedsToBeSaved = false;

    public static void main(String[] args) {
        do {
            viewList();
            printMenu();
            getUserInput();
            processChoice();
        } while (!quit);

        System.out.println("Goodbye!");
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("Please choose from the following options:");
        System.out.println("A/a: Add an item to the list");
        System.out.println("C/c: Clear the list (removes all items)");
        System.out.println("D/d: Delete an item from the list");
        System.out.println("I/i: Insert an item into the list");
        System.out.println("M/m: Move an item in the list");
        System.out.println("O/o: Open a list file from disk");
        System.out.println("S/s: Save the current list to disk");
        System.out.println("V/v: View the list");
        System.out.println("Q/q: Quit program");
    }

    private static void getUserInput() {
        userChoice = SafeInput.getRegExString(scanner, "\nPlease select a menu option (A/C/D/I/M/O/S/V/Q): ", "(?i)^[acdimosvq]$").toUpperCase();
    }

    private static void viewList() {
        if (userList.isEmpty()) {
            System.out.println("\nThe list is empty.\n");
        } else {
            System.out.println("\nThe list contains the following items:\n");
            for (int i = 1; i <= userList.size(); i++) {
                System.out.println("Item " + i + ": " + userList.get(i - 1));
            }
            System.out.println("\n");
        }
    }

    private static void processChoice() {
        switch (userChoice) {
            case "A":
                addItem();
                break;
            case "C":
                clearList();
                break;
            case "D":
                if (listIsEmpty("delete an item")) break;
                deleteItem();
                break;
            case "I":
                if (userList.isEmpty()) {
                    System.out.println("The list is empty — let's add your first item.");
                    addItem();
                } else {
                    insertItem();
                }
                break;
            case "M":
                if (userList.isEmpty() || userList.size() == 1) {
                    System.out.println("We need more items in the list to move them.");
                    System.out.println("Let's add another item.");
                    addItem();
                } else {
                    moveItem();
                }
                break;
            case "O":
                if (checkForUnsavedWork()) {
                    openListFile();
                }
                break;
            case "S":
                saveListFile();
                break;
            case "V":
                viewList();
                break;
            case "Q":
                if (checkForUnsavedWork()) {
                    quit = SafeInput.getYNConfirm(scanner, "Are you sure you want to quit the list app?");
                }
                break;
        }
    }

    private static void addItem() {
        String newItem = SafeInput.getNonZeroLenString(scanner, "Please enter a new item");
        userList.add(newItem);
        fileNeedsToBeSaved = true;
    }

    private static void deleteItem() {
        int index = getValidatedIndex("Please enter the item number to delete");
        userList.remove(index - 1);
        fileNeedsToBeSaved = true;
    }

    private static void insertItem() {
        int index = getValidatedIndex("Please enter the index of the item to insert BEFORE");
        String newItem = SafeInput.getNonZeroLenString(scanner, "Please enter a new item");
        userList.add(index - 1, newItem);
        fileNeedsToBeSaved = true;
    }

    private static void clearList() {
        userList.clear();
        fileNeedsToBeSaved = true;
    }

    private static void moveItem() {
        int fromIndex = getValidatedIndex("Please enter the index of the item to move");
        int toIndex = getValidatedIndex("Please enter the index of the destination item");
        String itemToMove = userList.get(fromIndex - 1);
        userList.remove(fromIndex - 1);
        userList.add(toIndex - 1, itemToMove);
        fileNeedsToBeSaved = true;
    }

    private static void openListFile() {
        JFileChooser chooser = new JFileChooser(new File("src"));
        chooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));

        int result = chooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();

            if (!selectedFile.getName().toLowerCase().endsWith(".txt")) {
                System.out.println("Invalid file type. Only .txt files are allowed.");
                return;
            }

            clearList();
            Path filePath = selectedFile.toPath();

            try {
                loadFileIntoList(filePath);
                fileName = selectedFile.getAbsolutePath();
                fileNeedsToBeSaved = false;
                System.out.println("File loaded successfully: " + selectedFile.getName());
            } catch (IOException e) {
                System.err.println("Error reading file: " + e.getMessage());
            }
        } else {
            System.out.println("File selection cancelled.");
        }
    }

    private static void saveListFile() {
        JFileChooser chooser = new JFileChooser(new File("src"));
        chooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));

        if (!fileName.isEmpty()) {
            chooser.setSelectedFile(new File(fileName));
        }

        int result = chooser.showSaveDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = enforceTxtExtension(chooser.getSelectedFile());
            Path filePath = selectedFile.toPath();

            try {
                writeListToFile(filePath);
                fileNeedsToBeSaved = false;
                fileName = selectedFile.getAbsolutePath();
                System.out.println("File saved successfully as: " + selectedFile.getName());
            } catch (IOException e) {
                fileNeedsToBeSaved = true;
                System.err.println("Error writing to file: " + e.getMessage());
            }
        } else {
            fileNeedsToBeSaved = true;
            System.out.println("File save cancelled.");
        }
    }

    private static boolean checkForUnsavedWork() {
        if (fileNeedsToBeSaved) {
            boolean userWantsToProceed = SafeInput.getYNConfirm(scanner,
                    "You have unsaved work. Are you sure you want to continue and lose your changes?");
            if (!userWantsToProceed) {
                System.out.println("Operation cancelled. Returning to menu...");
                return false;
            }
        }
        return true;
    }

    // helper methods below here

    private static boolean listIsEmpty(String contextAction) {
        if (userList.isEmpty()) {
            System.out.println("\nYou can't " + contextAction + " when the list is empty.\n");
            return true;
        }
        return false;
    }

    private static int getValidatedIndex(String prompt) {
        return SafeInput.getRangedInt(scanner, prompt, 1, userList.size());
    }

    private static File enforceTxtExtension(File file) {
        String path = file.getPath();
        if (!path.toLowerCase().endsWith(".txt")) {
            return new File(path + ".txt");
        }
        return file;
    }

    private static void loadFileIntoList(Path path) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                userList.add(line.trim());
            }
        }
    }

    private static void writeListToFile(Path path) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (String item : userList) {
                writer.write(item);
                writer.newLine();
            }
        }
    }
}
