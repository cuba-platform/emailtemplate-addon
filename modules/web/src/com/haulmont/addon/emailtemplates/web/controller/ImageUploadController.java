package com.haulmont.addon.emailtemplates.web.controller;

import com.google.common.base.Strings;
import com.haulmont.cuba.core.app.DataService;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.security.auth.AuthenticationService;
import com.haulmont.cuba.security.auth.LoginPasswordCredentials;
import com.haulmont.restapi.data.FileInfo;
import com.haulmont.restapi.exception.RestAPIException;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;

@RestController("emailtemplates_FileUploadController")
@RequestMapping(path = "/files")
public class ImageUploadController {

    private static final Logger log = LoggerFactory.getLogger(ImageUploadController.class);

    @Inject
    protected Metadata metadata;

    @Inject
    protected TimeSource timeSource;

    @Inject
    protected DataService dataService;

    @Inject
    protected FileLoader fileLoader;

    @Inject
    protected AuthenticationService authentication;

    @PostMapping(consumes = "image/*")
    public ResponseEntity<FileInfo> uploadFile(
            @RequestBody byte[] postPayload,
            @RequestParam String name,
            HttpServletRequest request) {
        try {
            authentication.login(new LoginPasswordCredentials("admin", "admin"));

            long size = postPayload.length;
            FileDescriptor fd = createFileDescriptor(name, size);

            InputStream is = new ByteArrayInputStream(postPayload);
            uploadToMiddleware(is, fd);
            saveFileDescriptor(fd);

            return createFileInfoResponseEntity(request, fd);
        } catch (Exception e) {
            log.error("File upload failed", e);
            throw new RestAPIException("File upload failed", "File upload failed", HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            authentication.logout();
        }
    }

    protected ResponseEntity<FileInfo> createFileInfoResponseEntity(HttpServletRequest request, FileDescriptor fd) {
        FileInfo fileInfo = new FileInfo(fd.getId(), fd.getName(), fd.getSize());

        UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString())
                .path("/{id}")
                .buildAndExpand(fd.getId().toString());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(uriComponents.toUri());
        return new ResponseEntity<>(fileInfo, httpHeaders, HttpStatus.CREATED);
    }

    protected void saveFileDescriptor(FileDescriptor fd) {
        CommitContext commitContext = new CommitContext(Collections.singleton(fd));
        dataService.commit(commitContext);
    }

    protected FileDescriptor createFileDescriptor(@Nullable String fileName, long size) {
        FileDescriptor fd = metadata.create(FileDescriptor.class);
        if (Strings.isNullOrEmpty(fileName)) {
            fileName = fd.getId().toString();
        }
        fd.setName(fileName);
        fd.setExtension(FilenameUtils.getExtension(fileName));
        fd.setSize(size);
        fd.setCreateDate(timeSource.currentTimestamp());
        return fd;
    }

    protected void uploadToMiddleware(InputStream is, FileDescriptor fd) {
        try {
            fileLoader.saveStream(fd, new FileLoader.SingleInputStreamSupplier(is));
        } catch (FileStorageException e) {
            throw new RestAPIException("Unable to upload file to FileStorage",
                    "Unable to upload file to FileStorage: " + fd.getId(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
