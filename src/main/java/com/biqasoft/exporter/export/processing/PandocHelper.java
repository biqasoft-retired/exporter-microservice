/*
 * Copyright (c) 2016. com.biqasoft
 */

package com.biqasoft.exporter.export.processing;

import org.apache.commons.io.FileUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.biqasoft.common.exceptions.InternalSeverErrorProcessingRequestException;

import java.io.File;
import java.io.IOException;

/**
 * Created by Nikita Bakaev, ya@nbakaev.ru on 5/11/2016.
 * All Rights Reserved
 */
@Service
public class PandocHelper {

    private static final Logger logger = LoggerFactory.getLogger(PandocHelper.class);

    /**
     * Convert HTML as string to PDF
     * @param html html which should be converted to pdf
     * @return link to converted html file to pdf
     */
    public File processHTMLToPandocSupported(String from, String to, String mimeType, String extension, byte[] html) {

        String tempFileName = new ObjectId().toString();
        String outputFileName = tempFileName + "output"+extension;

        File outputFile;
        File tempFile;

        try {
            outputFile = File.createTempFile(outputFileName, extension);
            tempFile = File.createTempFile(tempFileName, "."+from);

            // write html to file - because pandoc need file
            FileUtils.writeByteArrayToFile(tempFile, html);

        } catch (IOException e) {
            throw new InternalSeverErrorProcessingRequestException(e.getMessage());
        }

        ProcessBuilder pb;

        try {
            // export can not use absolute path with file://
            // may be bug
            // only read file from the same directory
            pb = new ProcessBuilder("pandoc", "-o", outputFile.getName(), tempFile.getName());
        } catch (Exception e) {
            throw new InternalSeverErrorProcessingRequestException(e.getMessage());
        }

        // logs from process to system output
        pb.inheritIO();

        // cd to temp dir
        pb.directory(new File(System.getProperty("java.io.tmpdir")));
        logger.info("Start pandoc");
        Process p = null;
        try {
            p = pb.start();
            p.waitFor();

            if (p.exitValue() != 0) {
                logger.warn("pandoc exit code is: {}", p.exitValue());
                throw new InternalSeverErrorProcessingRequestException("Error");
            }

            //delete temp file
            tempFile.delete();

            logger.info("End pandoc");

            return outputFile;

        } catch (Exception e) {
            throw new InternalSeverErrorProcessingRequestException(e.getMessage());
        }
    }

}
