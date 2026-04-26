package edu.metrostate;

import java.io.*;
import java.util.ArrayList;

/**
 * Directory class - Feature 5 - responsible for detecting new files.
 * Checks the "watched/" folder every 3 seconds for new order files.
 * Automatically sends any new .xml or .json files to OrderImporter.
 *
 */
public class Directory implements Serializable {
    private static final String watchFolder = "watched";
    private final OrderManager orderManager;
    private ArrayList<String> importedFiles;
    @Serial
    private static final long serialVersionUID = 1L;

    private Runnable onOrderImported;


    /**
     * Constructs a Directory watcher.
     * Loads the list of previously imported files from disk
     * Creates the watched/ folder if it does not already exist.
     *
     * @param orderManager the OrderManager to pass to OrderImporter when importing files
     */
    public Directory(OrderManager orderManager) {
        this.orderManager = orderManager;

        try (ObjectInputStream temp = new ObjectInputStream( new FileInputStream("importedFiles.dat"))) {
            importedFiles = (ArrayList<String>) temp.readObject();
        } catch (Exception e) {
            importedFiles = new ArrayList<>();
        }

        File folder = new File(watchFolder);
        if(!folder.exists()) {
            folder.mkdir();
            System.out.println("Created watched/ folder at: " + folder.getAbsolutePath());
        }
        System.out.println("Watching folder:" + folder.getAbsolutePath());
    }

    /**
     * Sets the callback to run after a new order is successfully imported.
     * Used by MainGUI to refresh the order list when a file is detected.
     *
     * @param callback the Runnable to call after a successful import
     */
    public void setOnOrderImported(Runnable callback) {
        this.onOrderImported = callback;
    }


    /**
     * Checks the watched/ folder for new files.
     * Called every 3 seconds from MainGUI
     * Skips empty files and files that have already been imported.
     * Supports .xml and .json files - Feature 3
     */
    public void checkFolder() {
        File folder = new File(watchFolder);
        File[] files = folder.listFiles();

        if (files == null || files.length == 0) return;

        for (File file : files) {
            String fileName = file.getName().toLowerCase();

            if (file.length() == 0) continue;
            if (importedFiles.contains(fileName)) continue;

            if (fileName.endsWith(".xml")) {
                System.out.println("\n--- Importing: " + fileName + " ---");
                OrderImporter.importXML(file, orderManager, onOrderImported);
                importedFiles.add(fileName);
                saveImportedFiles();
            } else if (fileName.endsWith(".json")) {
                System.out.println("\n--- Importing JSON: " + fileName + " ---");
                OrderImporter.importJSON(file, orderManager, onOrderImported);
                importedFiles.add(fileName);
                saveImportedFiles();
            } else {
                System.out.println("\n--- Unsupported file: " + fileName + " ---");
                importedFiles.add(fileName);
                saveImportedFiles();
            }

        }

    }

    /**
     * Saves the list of imported file names to disk.
     * Ensures already-imported files are not re-imported on the next session.
     * Part of Feature 2
     * Note: to reset, delete both allOrders.dat and importedFiles.dat.
     */
    public void saveImportedFiles() {
        try (ObjectOutputStream temp = new ObjectOutputStream( new FileOutputStream("importedFiles.dat"))) {
            temp.writeObject(importedFiles);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}



