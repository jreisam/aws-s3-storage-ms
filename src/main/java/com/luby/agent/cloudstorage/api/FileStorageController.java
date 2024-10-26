/*
 * Copyright 2017-2024 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.luby.agent.cloudstorage.api;

import com.luby.agent.cloudstorage.service.FileStorageServiceImpl;
import io.micronaut.http.*;
import io.micronaut.http.annotation.*;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.http.server.types.files.StreamedFile;
import jakarta.inject.Inject;

import java.util.Optional;


@Controller(FileStorageController.PREFIX)
public class FileStorageController {

    static final String PREFIX = "/file";
    private final FileStorageServiceImpl fileStorageServiceImpl;

    @Inject
    public FileStorageController(FileStorageServiceImpl fileStorageServiceImpl) {
        this.fileStorageServiceImpl = fileStorageServiceImpl;
    }

    @Post(uri = "/{filename}", consumes = MediaType.MULTIPART_FORM_DATA)
    public HttpResponse<?> upload(CompletedFileUpload fileUpload, String filename, HttpRequest<?> request) {
        return fileStorageServiceImpl.upload(fileUpload, filename, request);
    }

    @Get("/{filename}")
    public Optional<HttpResponse<StreamedFile>> download(String filename) {
        return fileStorageServiceImpl.download(filename);
    }

    @Status(HttpStatus.NO_CONTENT)
    @Delete("/{filename}")
    public void delete(String filename) {
        fileStorageServiceImpl.delete(filename);
    }
}

