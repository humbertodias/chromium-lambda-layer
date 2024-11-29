package com.example;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LibraryPath {
    private static final String LD_LIBRARY_PATH = "LD_LIBRARY_PATH";
    private LibraryPath() {
    }

    public static void listFiles(String path) throws IOException {
        Path dir = Paths.get(path);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path entry : stream) {
                System.out.println(entry.getFileName());
            }
        }
    }

    public static void updateLdLibraryPath(String... newPaths) {
        for (String newPath : newPaths) {
            updateLdLibraryPath(newPath);
        }
    }

    private static void updateLdLibraryPath(String newPath) {
        if (newPath == null || newPath.isEmpty()) {
            System.out.println("Invalid path provided.");
            return;
        }

        String existingLdLibraryPath = System.getenv(LD_LIBRARY_PATH);

        // Checks if the given path already exists in the LD_LIBRARY_PATH variable
        if (existingLdLibraryPath != null && existingLdLibraryPath.contains(newPath)) {
            System.out.println("Path already exists in LD_LIBRARY_PATH.");
            return;
        }

        // If not, add the new path
        String updatedLdLibraryPath = newPath + (existingLdLibraryPath != null ? ":" + existingLdLibraryPath : "");
        System.setProperty(LD_LIBRARY_PATH, updatedLdLibraryPath);
        System.out.println("Updated LD_LIBRARY_PATH: " + System.getProperty(LD_LIBRARY_PATH));
    }

}
