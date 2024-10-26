package com.luby.agent.cloudstorage.service;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.http.server.types.files.StreamedFile;

import java.util.Optional;

public interface FileStorageService {

    HttpResponse upload(CompletedFileUpload fileUpload, String filename, HttpRequest<?> request);

    Optional<HttpResponse<StreamedFile>> download(String filename);

    void delete(String filename);
}
