package com.cislo.photos.utils;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class FilesCounter {

    public static int getFilesCount(Path rootDirectory) throws IOException {
        FilesCounterVisitor visitor = new FilesCounterVisitor();
        Files.walkFileTree(rootDirectory, visitor);
        return visitor.getFilesCount();
    }

    private static class FilesCounterVisitor extends SimpleFileVisitor<Path> {
        private int filesCount = 0;

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            if (attrs.isRegularFile()) {
                filesCount++;
            }
            return FileVisitResult.CONTINUE;
        }

        int getFilesCount() {
            return filesCount;
        }
    }
}
