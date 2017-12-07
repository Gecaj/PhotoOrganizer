package com.cislo.photos;

import com.cislo.photos.utils.PhotoCreationDateExtractor;
import com.drew.imaging.ImageProcessingException;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Created by jcislo on 3/17/17.
 */
public class PhotoCreationDateExtractorTest {

    @Test
    public void shouldReadPhotoCreationDateCorrectly() throws IOException, ImageProcessingException {
        // given
        Path path = Paths.get("src/test/resources/photos/photo1.JPG");

        // when
        Optional<LocalDateTime> localDateTime = PhotoCreationDateExtractor.readCreationTime(path.toFile());

        // then
        assertThat(localDateTime).isPresent();
        assertThat(localDateTime.get().get(ChronoField.MONTH_OF_YEAR)).isEqualTo(8);
        assertThat(localDateTime.get().get(ChronoField.DAY_OF_MONTH)).isEqualTo(23);
        assertThat(localDateTime.get().get(ChronoField.YEAR)).isEqualTo(2016);

    }

}