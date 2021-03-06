package com.cislo.photos.utils;

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

public class PhotoCreationDateExtractor {

    private static Logger logger = LoggerFactory.getLogger(PhotoCreationDateExtractor.class);

    public static Optional<LocalDateTime> readCreationTime(File file) {
        if (file.exists()) {
            return tryReadMetadata(file).
                    map(PhotoCreationDateExtractor::metadataToDate).
                    map(PhotoCreationDateExtractor::dateToLocalDateTime);
        }
        return Optional.empty();
    }

    private static Optional<Metadata> tryReadMetadata(File file) {
        try {
            return Optional.of(ImageMetadataReader.readMetadata(file));
        } catch (ImageProcessingException | IOException e) {
            logger.info("Failed to read metadata of file: {}", file.getName());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private static LocalDateTime dateToLocalDateTime(Date d) {
        return LocalDateTime.ofInstant(d.toInstant(), ZoneId.systemDefault());
    }

    private static Date metadataToDate(Metadata m) {
        ExifSubIFDDirectory exifSubIFDDirectory = m.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
        if (exifSubIFDDirectory == null) {
            return null;
        }
        return exifSubIFDDirectory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
    }
}
