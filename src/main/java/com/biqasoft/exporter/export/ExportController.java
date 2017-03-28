/*
 * Copyright (c) 2016. com.biqasoft
 */

package com.biqasoft.exporter.export;

import com.biqasoft.common.exceptions.InternalSeverErrorProcessingRequestException;
import com.biqasoft.common.exceptions.InvalidRequestException;
import com.biqasoft.exporter.export.processing.PandocHelper;
import com.biqasoft.exporter.export.processing.PhantomJsHTMLRenderHelper;
import com.biqasoft.exporter.export.processing.PhantomJsHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Convert from one format to another
 * for example convert from html to pdf/docx
 * <p>
 * Created by ya_000 on 10/5/2015.
 */
@RestController
@RequestMapping("/v1//export")
public class ExportController {

    private PhantomJsHelper phantomJsHelper;
    private PandocHelper pandocHelper;
    private PhantomJsHTMLRenderHelper phantomJsHTMLRenderHelper;

    @Autowired
    public ExportController(PhantomJsHelper phantomJsHelper, PandocHelper pandocHelper, PhantomJsHTMLRenderHelper phantomJsHTMLRenderHelper) {
        this.phantomJsHelper = phantomJsHelper;
        this.pandocHelper = pandocHelper;
        this.phantomJsHTMLRenderHelper = phantomJsHTMLRenderHelper;
    }

    @RequestMapping(value = "from/{from}/to/{to}", method = RequestMethod.POST)
    ResponseEntity<byte[]> getPrintable(@RequestBody String requestedHTMLcode,
                                        @PathVariable("from") String from,
                                        @PathVariable("to") String to,

                                        @RequestParam(value = "mime_type", required = false) String mime_type,
                                        @RequestParam(value = "extension", required = false) String extension
    ) throws Exception {

        byte[] bytes;
        HttpHeaders headers;
        ResponseEntity responseEntity;

        // Convert HTML to pdf
        // with phantomjs
        if (from.equals("html") && (to.equals("pdf") || to.equals("png") || to.equals("jpg"))) {

            File file = phantomJsHelper.processHTMLToPdf(requestedHTMLcode.getBytes(), to);
            bytes = readBytesFromFile(file);
            headers = new HttpHeaders();

            switch (to) {
                case "pdf":
                    headers.setContentType(MediaType.parseMediaType("application/pdf"));
                    break;

                case "png":
                    headers.setContentType(MediaType.parseMediaType("image/png"));
                    break;

                case "jpg":
                    headers.setContentType(MediaType.parseMediaType("image/jpeg"));
                    break;
            }

            responseEntity = new ResponseEntity(bytes, headers, HttpStatus.ACCEPTED);
            return responseEntity;

            // Convert HTML to pandoc (cmd util) supported formats
            // for example to microsoft word (docx)
        } else if (from.equals("html") && to.equals("pandoc")) {

            File file = pandocHelper.processHTMLToPandocSupported(
                    from,
                    to, mime_type, extension,
                    // render string with javascript, such as jquery that in word we will have rendered html
                    readBytesFromFile(phantomJsHTMLRenderHelper.processHTMLToHTML(requestedHTMLcode.getBytes())));

            bytes = readBytesFromFile(file);

            headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(mime_type));

            responseEntity = new ResponseEntity(bytes, headers, HttpStatus.ACCEPTED);
            return responseEntity;
        }

        throw new InvalidRequestException("No such from: " + from + " and to: " + to);
    }

    private byte[] readBytesFromFile(File file) {
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(file.toPath());
            file.delete();
        } catch (IOException e) {
            e.printStackTrace();
            throw new InternalSeverErrorProcessingRequestException("Internal error. Please try later");
        }
        return bytes;
    }

}