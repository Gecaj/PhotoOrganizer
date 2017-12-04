package com.cislo.photos;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

/**
 * Created by jcislo on 3/17/17.
 */
public class PhotoCreationDateExtractor {

    private Logger logger = LoggerFactory.getLogger(PhotoCreationDateExtractor.class);

    public Optional<LocalDateTime> readCreationTime(File file) {
        if (file.exists()) {
            return tryReadMetadata(file).map(this::metadataToDate).map(this::dateToLocalDateTime);
        }
        return Optional.empty();
    }

    private Optional<Metadata> tryReadMetadata(File file) {
        try {
            return Optional.of(ImageMetadataReader.readMetadata(file));
        } catch (ImageProcessingException | IOException e) {
            logger.info("Failed to read metadata of file: {}", file.getName());
            logger.error(e.getMessage());
            return Optional.empty();
        }
    }

    private LocalDateTime dateToLocalDateTime(Date d) {
        return LocalDateTime.ofInstant(d.toInstant(), ZoneId.systemDefault());
    }

    private Date metadataToDate(Metadata m) {
        return m.getFirstDirectoryOfType(ExifSubIFDDirectory.class).getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
    }
}
