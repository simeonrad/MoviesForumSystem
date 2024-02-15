package com.telerikacademy.web.forumsystem.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

public class ImageStorageServiceTests {
    @InjectMocks
    private ImageStorageServiceImpl service;

    @Mock
    private Path rootLocation;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        rootLocation = Paths.get("uploaded-images");
        service = new ImageStorageServiceImpl();
    }

    @Test
    public void saveImage_Success() throws IOException {
        MultipartFile multipartFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image content".getBytes());
        String expectedFilename = service.saveImage(multipartFile); // This isn't mocked but generated in service

        Path expectedPath = rootLocation.resolve(expectedFilename);
        boolean fileExists = Files.exists(expectedPath);

        assertTrue(fileExists);
    }

    @Test
    public void saveImage_ThrowsExceptionIfFileIsEmpty() {
        MultipartFile emptyFile = new MockMultipartFile("file", "", "image/jpeg", new byte[0]);

        Exception exception = assertThrows(RuntimeException.class, () -> service.saveImage(emptyFile));

        String expectedMessageStart = "Failed to store empty file";
        assertTrue(exception.getMessage().startsWith(expectedMessageStart));
    }


    @Test
    public void saveImage_ThrowsExceptionIfFilenameContainsDirectoryTraversalCharacters() {
        MultipartFile maliciousFile = new MockMultipartFile("file", "../test.jpg", "image/jpeg", "content".getBytes());

        Exception exception = assertThrows(RuntimeException.class, () -> service.saveImage(maliciousFile));

        assertTrue(exception.getMessage().contains("Cannot store file with relative path outside current directory"));
    }

    @Test
    public void saveImage_ThrowsRuntimeExceptionWhenIOExceptionOccurs() throws IOException {
        MultipartFile validFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", "content".getBytes());

        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.copy(any(InputStream.class), any(Path.class), any(CopyOption[].class)))
                    .thenThrow(IOException.class);

            Exception exception = assertThrows(RuntimeException.class, () -> service.saveImage(validFile));

            assertTrue(exception.getMessage().contains("Failed to store file"));
        }
    }
}
