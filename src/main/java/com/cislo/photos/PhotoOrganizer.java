package com.cislo.photos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Created by jcislo on 3/16/17.
 */
public class PhotoOrganizer {

    private Logger logger = LoggerFactory.getLogger(PhotoOrganizer.class);

    private int movedPhotos = 0;
    private List<String> failedFileNames = new LinkedList<>();

    public void copyFilesToNewLocation(Path sourceDirectory, Path targetRootDirectory) throws IOException {
        createOutputDirectory(targetRootDirectory);
        logger.info("Number of files to move: {}", FilesCounter.getFilesCount(sourceDirectory));
        Files.walkFileTree(sourceDirectory, new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (attrs.isRegularFile()) {
                    copyFileToNewLocation(file, targetRootDirectory);
                }
                return FileVisitResult.CONTINUE;
            }
        });
        logger.info("Number of files moved: {}", movedPhotos);
        logger.info("Failed to move files: {}", failedFileNames);
    }

    public int getMovedPhotos() {
        return movedPhotos;
    }

    public List<String> getFailedFileNames() {
        return failedFileNames;
    }

    private void copyFileToNewLocation(Path file, Path targetRootDirectory) throws IOException {
        createOutputDirectory(targetRootDirectory);
        Optional<LocalDateTime> creationTimeOptional = new PhotoCreationDateExtractor().readCreationTime(file.toFile());
        if (creationTimeOptional.isPresent()) {
            Path yearDirectory = createYearDirectory(targetRootDirectory, creationTimeOptional);
            Path monthDirectory = Paths.get(TimeExtractor.readCreationMonth(creationTimeOptional.get()));
            Path outputDirectory = createOutputDirectory(targetRootDirectory, yearDirectory, monthDirectory);
            Path newFileLocation = outputDirectory.resolve(file.getFileName());
            Files.copy(file, newFileLocation, StandardCopyOption.REPLACE_EXISTING);
            movedPhotos++;
        } else {
            failedFileNames.add(file.getFileName().toString());
        }
    }

    private Path createOutputDirectory(Path targetDirectory, Path yearDirectory, Path monthDirectory) {
        Path outputLocation = targetDirectory.resolve(yearDirectory).resolve(monthDirectory);
        createOutputDirectory(outputLocation);
        return outputLocation;
    }

    private Path createYearDirectory(Path targetDirectory, Optional<LocalDateTime> creationTimeOptional) {
        Path yearDirectory = Paths.get(TimeExtractor.readCreationYear(creationTimeOptional.get()));
        createOutputDirectory(targetDirectory.resolve(yearDirectory));
        return yearDirectory;
    }

    private void createOutputDirectory(Path newLocation) {
        if (! newLocation.toFile().exists()) {
            newLocation.toFile().mkdir();
        }
    }
}
