package com.example;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.EnumSet;
import java.util.Set;

public class TarGzipHelper {

    private TarGzipHelper() {
    }

    public static void extractTarGz(String tarGzFilePath, String outputDir) throws IOException {
        try (FileInputStream fis = new FileInputStream(tarGzFilePath);
             BufferedInputStream bis = new BufferedInputStream(fis);
             GzipCompressorInputStream gzis = new GzipCompressorInputStream(bis);
             TarArchiveInputStream tar = new TarArchiveInputStream(gzis)) {

            TarArchiveEntry entry;
            while ((entry = tar.getNextTarEntry()) != null) {

                String entryName = entry.getName().startsWith("/") ? entry.getName().substring(1) : entry.getName();
                Path extractTo = Paths.get(outputDir, entryName);

                if (entry.isDirectory()) {
                    Files.createDirectories(extractTo);
                } else if (entry.isSymbolicLink()) {
                    Path linkTarget = Paths.get(entry.getLinkName());
                    Files.createDirectories(extractTo.getParent());
                    Files.createSymbolicLink(extractTo, linkTarget);
                } else {
                    Files.createDirectories(extractTo.getParent());
                    try (OutputStream out = Files.newOutputStream(extractTo)) {
                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        while ((bytesRead = tar.read(buffer)) != -1) {
                            out.write(buffer, 0, bytesRead);
                        }
                    }
                    // Preserves file permissions
                    Set<PosixFilePermission> permissions = convertModeToPermissions(entry.getMode());
                    Files.setPosixFilePermissions(extractTo, permissions);
                }
            }
        }
    }

    /**
     * Converts the (int) mode to a POSIX permission set.
     */
    private static Set<PosixFilePermission> convertModeToPermissions(int mode) {
        Set<PosixFilePermission> permissions = EnumSet.noneOf(PosixFilePermission.class);

        if ((mode & 0400) != 0) permissions.add(PosixFilePermission.OWNER_READ);
        if ((mode & 0200) != 0) permissions.add(PosixFilePermission.OWNER_WRITE);
        if ((mode & 0100) != 0) permissions.add(PosixFilePermission.OWNER_EXECUTE);

        if ((mode & 0040) != 0) permissions.add(PosixFilePermission.GROUP_READ);
        if ((mode & 0020) != 0) permissions.add(PosixFilePermission.GROUP_WRITE);
        if ((mode & 0010) != 0) permissions.add(PosixFilePermission.GROUP_EXECUTE);

        if ((mode & 0004) != 0) permissions.add(PosixFilePermission.OTHERS_READ);
        if ((mode & 0002) != 0) permissions.add(PosixFilePermission.OTHERS_WRITE);
        if ((mode & 0001) != 0) permissions.add(PosixFilePermission.OTHERS_EXECUTE);

        return permissions;
    }

}
