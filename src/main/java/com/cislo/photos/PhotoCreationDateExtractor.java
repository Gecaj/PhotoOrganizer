package com.cislo.photos;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;

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

    public Optional<LocalDateTime> readCreationTime(File file) {
        if (file.exists()) {
            Metadata metadata = null;
            try {
                metadata = ImageMetadataReader.readMetadata(file);
            } catch (ImageProcessingException | IOException e) {
                System.out.println("Failed to read metadata of file: " + file.getName());
                e.printStackTrace();
                return Optional.empty();
            }
            Date date = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class).getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
            LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
            return Optional.of(localDateTime);
        }
        return Optional.empty();
    }
}
