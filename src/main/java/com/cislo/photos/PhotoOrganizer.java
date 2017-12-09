package com.cislo.photos;

import com.cislo.photos.utils.FilesCounter;
import com.cislo.photos.utils.PhotoCreationDateExtractor;
import com.cislo.photos.utils.TimeExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
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
public class PhotoOrganizer extends SwingWorker<Void, Long>{

    private final Path sourcePath;
    private final Path targetPath;
    private Logger logger = LoggerFactory.getLogger(PhotoOrganizer.class);

    private int movedPhotos = 0;
    private List<String> failedFileNames = new LinkedList<>();
    private boolean finished;
    private int filesCount;

    public PhotoOrganizer(Path sourcePath, Path targetPath) {
        this.sourcePath = sourcePath;
        this.targetPath = targetPath;
    }

    public void copyFilesToNewLocation() throws IOException {
        createOutputDirectory(targetPath);
        filesCount = FilesCounter.getFilesCount(sourcePath);
        logger.info("Number of files to move: {}", filesCount);
        Files.walkFileTree(sourcePath, new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (attrs.isRegularFile()) {
                    copyFileToNewLocation(file, targetPath);
                }
                return FileVisitResult.CONTINUE;
            }
        });
        finished = true;
        logger.info("Number of files moved: {}", movedPhotos);
        logger.info("Failed to move files: {}", failedFileNames);
    }

    public int getMovedPhotos() {
        return movedPhotos;
    }

    public boolean hasFinished() {
        return finished;
    }

    public List<String> getFailedFileNames() {
        return failedFileNames;
    }

    private void copyFileToNewLocation(Path file, Path targetRootDirectory) throws IOException {
        createOutputDirectory(targetRootDirectory);
        Optional<LocalDateTime> creationTimeOptional = PhotoCreationDateExtractor.readCreationTime(file.toFile());
        if (creationTimeOptional.isPresent()) {
            Path yearDirectory = createYearDirectory(targetRootDirectory, creationTimeOptional);
            Path monthDirectory = Paths.get(TimeExtractor.readCreationMonth(creationTimeOptional.get()));
            Path outputDirectory = createOutputDirectory(targetRootDirectory, yearDirectory, monthDirectory);
            Path newFileLocation = outputDirectory.resolve(file.getFileName());
            Files.copy(file, newFileLocation, StandardCopyOption.REPLACE_EXISTING);
            setProgress((int)((++movedPhotos/(double)filesCount)*100));
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

    @Override
    protected Void doInBackground() throws Exception {
        try {
            copyFilesToNewLocation();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
