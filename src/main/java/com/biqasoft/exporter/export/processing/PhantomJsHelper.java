/*
 * Copyright (c) 2016. com.biqasoft
 */

package com.biqasoft.exporter.export.processing;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import com.biqasoft.common.exceptions.InternalSeverErrorProcessingRequestException;

import java.io.File;
import java.io.IOException;

/**
 * Created by Nikita Bakaev, ya@nbakaev.ru on 5/11/2016.
 * All Rights Reserved
 */
@Service
public class PhantomJsHelper {

    private File jsScriptOnFileSystem;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public PhantomJsHelper() {
        try {
            jsScriptOnFileSystem = File.createTempFile("rasterize", ".js");
            final Resource uri = new ClassPathResource("javascript/rasterize.js");
            FileUtils.writeByteArrayToFile(jsScriptOnFileSystem, IOUtils.toByteArray(uri.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Convert HTML as string to PDF
     *
     * @param html html which should be converted to pdf
     * @return link to converted html file to pdf
     */
    public File processHTMLToPdf(byte[] html, String outputFormat) {

        String tempFileName = new ObjectId().toString();
        String outputFileName = tempFileName + "output." + outputFormat;

        File outputFile;
        File tempFile;

        try {
            outputFile = File.createTempFile(outputFileName, "."+outputFormat);
            tempFile = File.createTempFile(tempFileName, ".html");

            // write html to file - because export need file
            FileUtils.writeByteArrayToFile(tempFile, html);

        } catch (IOException e) {
            throw new InternalSeverErrorProcessingRequestException(e.getMessage());
        }

        ProcessBuilder pb;

        // export can not use absolute path with file://
        // may be bug
        // only read file from the same directory
        pb = new ProcessBuilder("phantomjs", jsScriptOnFileSystem.getAbsolutePath(), tempFile.getName(), outputFile.getName());

        // logs from process to system output
        pb.inheritIO();

        // cd to temp dir
        pb.directory(new File(System.getProperty("java.io.tmpdir")));
        logger.info("Start export");
        Process p = null;
        try {
            p = pb.start();
            p.waitFor();

            // export exit code check
            // it can be non 0 without exceptions
            if (p.exitValue() != 0) {
                logger.warn("export exit code is: {}", p.exitValue());
                throw new InternalSeverErrorProcessingRequestException("Error");
            }

            //delete temp file
            tempFile.delete();

            logger.info("End export");

            return outputFile;

        } catch (Exception e) {
            throw new InternalSeverErrorProcessingRequestException(e.getMessage());
        }
    }

}
