package com.cislo.photos;

import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Created by jcislo on 3/16/17.
 */
public class PhotoOrganizerTest {

    private final PhotoOrganizer fileOrganizer = new PhotoOrganizer();
    private String firstFile = "src/test/resources/photos/photo1.JPG";
    private Path newLocation = Paths.get("src/test/resources/output").toAbsolutePath();

    @Test
    public void shouldCreateNewLocation() throws IOException {
        // given
        Path file = Paths.get(firstFile).toAbsolutePath();

        // when
        fileOrganizer.moveFileToNewLocation(file, newLocation);

        // then
        assertThat(newLocation.toFile().exists()).isTrue();
        assertThat(newLocation.toFile().isDirectory()).isTrue();
    }

    @Test
    public void shouldMoveFileToProperLocation() throws IOException {
        // given
        Path file = Paths.get(firstFile).toAbsolutePath();
        Path expectedDirectory = newLocation.resolve(Paths.get("2016/Sierpień/"));
        Path expectedLocation = expectedDirectory.resolve("photo1.JPG");

        // when
        fileOrganizer.moveFileToNewLocation(file, newLocation);

        // then
        assertThat(expectedDirectory.toFile().exists()).isTrue();
        assertThat(expectedLocation.toFile().exists()).isTrue();
        assertThat(expectedLocation.toFile().isFile()).isTrue();
    }

    @Test
    public void shouldMoveAllFilesInDirectoryToProperLocation() throws IOException {
        // given
        Path sourceDirectory = Paths.get("src/test/resources/photos/").toAbsolutePath();

        // when
        fileOrganizer.moveFilesToNewLocation(sourceDirectory, newLocation);

        // then
        assertThat(newLocation.toFile()).exists();
        assertThat(newLocation.resolve(Paths.get("2016/Sierpień/photo1.JPG")).toFile()).exists();
        assertThat(newLocation.resolve(Paths.get("2016/Grudzień/photo2.JPG")).toFile()).exists();

    }

    @After
    public void tearDown() throws Exception {
        Path rootPath = newLocation;
        Files.walk(rootPath)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }
}
