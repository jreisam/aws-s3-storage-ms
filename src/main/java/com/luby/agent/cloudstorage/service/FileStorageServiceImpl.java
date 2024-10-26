package com.luby.agent.cloudstorage.service;

import io.micronaut.context.annotation.Bean;
import io.micronaut.http.*;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.http.server.types.files.StreamedFile;
import io.micronaut.http.server.util.HttpHostResolver;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.objectstorage.aws.AwsS3ObjectStorageEntry;
import io.micronaut.objectstorage.aws.AwsS3Operations;
import io.micronaut.objectstorage.request.UploadRequest;
import io.micronaut.objectstorage.response.UploadResponse;
import jakarta.inject.Singleton;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.net.URI;
import java.util.Optional;

@Bean
public class FileStorageServiceImpl implements FileStorageService {

    static final String S3URL = "https://inter-agents-jreis-temp-bucket.s3.amazonaws.com/";

    @Singleton
    private final AwsS3Operations objectStorage;
    @Singleton
    private final HttpHostResolver httpHostResolver;

    public FileStorageServiceImpl(AwsS3Operations objectStorage, HttpHostResolver httpHostResolver) {
        this.objectStorage = objectStorage;
        this.httpHostResolver = httpHostResolver;
    }

    @Override
    public HttpResponse<?> upload(CompletedFileUpload fileUpload, String filename, HttpRequest<?> request) {
        UploadRequest objectStorageUpload = UploadRequest.fromCompletedFileUpload(fileUpload, filename);
        UploadResponse<PutObjectResponse> response = objectStorage.upload(objectStorageUpload, builder -> {
            builder.acl(ObjectCannedACL.PUBLIC_READ);
        });
        String s3FileUrl = S3URL + filename;

        return HttpResponse
                .created(location(s3FileUrl))
                .header(HttpHeaders.ETAG, response.getETag());
    }

    private URI location(String s3FileUrl) {
        return UriBuilder.of(s3FileUrl)
                .build();
    }

    @Override
    public Optional<HttpResponse<StreamedFile>> download(String filename) {
         return objectStorage.retrieve(filename)
                .map(FileStorageServiceImpl::buildStreamedFile);
    }

    private static HttpResponse<StreamedFile> buildStreamedFile(AwsS3ObjectStorageEntry entry) {
        GetObjectResponse nativeEntry = entry.getNativeEntry();
        MediaType mediaType = MediaType.of(nativeEntry.contentType());
        StreamedFile file = new StreamedFile(entry.getInputStream(), mediaType).attach(entry.getKey());
        MutableHttpResponse<Object> httpResponse = HttpResponse.ok()
                .header(HttpHeaders.ETAG, nativeEntry.eTag());
        file.process(httpResponse);
        return httpResponse.body(file);
    }

    @Override
    public void delete(String filename) {
        objectStorage.delete(filename.trim());
    }
}
