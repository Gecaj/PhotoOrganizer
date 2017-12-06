package com.cislo.photos;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by jcislo on 12/7/17.
 */
public class FilesCounterTest {

    @Test
    public void shouldCountNumberOfFiles() throws IOException {
        // given
        Path directory = Paths.get("src/test/resources/photos");
        // when
        int filesCount = FilesCounter.getFilesCount(directory);
        // then
        assertThat(filesCount).isEqualTo(3);
    }

}