package com.cislo.photos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
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

    public void moveFilesToNewLocation(Path sourceDirectory, Path newLocation) throws IOException {
        createOutputDirectory(newLocation);
        logger.info("Number of files to move: {}", FilesCounter.getFilesCount(sourceDirectory));
        Files.walkFileTree(sourceDirectory, new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (attrs.isRegularFile()) {
                    moveFileToNewLocation(file, newLocation);
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public void moveFileToNewLocation(Path file, Path newLocation) throws IOException {
        createOutputDirectory(newLocation);
        TimeExtractor timeExtractor = new TimeExtractor();
        Optional<LocalDateTime> creationTimeOptional = new PhotoCreationDateExtractor().readCreationTime(file.toFile());
        if (creationTimeOptional.isPresent()) {
            LocalDateTime creationTime = creationTimeOptional.get();
            Path yearDirectory = Paths.get(timeExtractor.readCreationYear(creationTime));
            createOutputDirectory(newLocation.resolve(yearDirectory));
            Path monthDirectory = Paths.get(timeExtractor.readCreationMonth(creationTime));
            Path outputLocation = newLocation.resolve(yearDirectory).resolve(monthDirectory);
            createOutputDirectory(outputLocation);
            Path newFileLocation = outputLocation.resolve(file.getFileName());
            newFileLocation.toFile().createNewFile();
            Files.copy(file, newFileLocation, StandardCopyOption.REPLACE_EXISTING);
            movedPhotos++;
        } else {
            failedFileNames.add(file.getFileName().toString());
        }
    }

    public int getMovedPhotos() {
        return movedPhotos;
    }

    public List<String> getFailedFileNames() {
        return failedFileNames;
    }

    private void createOutputDirectory(Path newLocation) {
        if (! newLocation.toFile().exists()) {
            newLocation.toFile().mkdir();
        }
    }
}
