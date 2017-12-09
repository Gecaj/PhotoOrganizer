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

    private Path newLocation = Paths.get("src/test/resources/output").toAbsolutePath();
    private Path sourceDirectory = Paths.get("src/test/resources/photos/").toAbsolutePath();
    private final PhotoOrganizer fileOrganizer = new PhotoOrganizer(sourceDirectory, newLocation);

    @Test
    public void shouldCopyAllFilesInDirectoryToProperLocation() throws IOException {
        // given
        // when
        fileOrganizer.copyFilesToNewLocation();

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
