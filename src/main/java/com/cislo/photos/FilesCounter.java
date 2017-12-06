package com.cislo.photos;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Created by jcislo on 12/7/17.
 */
public class FilesCounter extends SimpleFileVisitor<Path> {
    private int filesCount = 0;

    private FilesCounter() {}

    public static final FilesCounter INSTANCE = new FilesCounter();

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (attrs.isRegularFile()) {
            filesCount++;
        }
        return FileVisitResult.CONTINUE;
    }

    public synchronized int getFilesCount(Path rootDirectory) throws IOException {
        filesCount = 0;
        Files.walkFileTree(rootDirectory, this);
        return filesCount;
    }
}
