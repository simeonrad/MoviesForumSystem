package com.telerikacademy.web.forumsystem.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImageStorageServiceImpl implements ImageStorageService {

    private final Path rootLocation = Paths.get("uploaded-images");
    //creates a place for storing the images and sets up a folder named uploaded-image,
    //if it does not exist.
    public ImageStorageServiceImpl() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage", e);
        }
    }
    //makes sure that a directory is created, if not throws a checked IOException.

    @Override
    public String saveImage(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String filename = UUID.randomUUID().toString() + "." + getExtension(file.getOriginalFilename());
        //generating unique name, so it prevents overwriting existing files.
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file " + filename);
                //checks if the file is empty or has a problematic filename
            }
            if (originalFilename.contains("..")) {
                //checks if it is navigating up in the directory. Checks for Directory Traversal.
                throw new RuntimeException("Cannot store file with relative path outside current directory " + filename);
            }
            Files.copy(file.getInputStream(), this.rootLocation.resolve(filename));
            //Saves the file to the uploaded-images directory. If something is wrong it throws an error.
            //The copy method copies bytes from a source to a target. Here it copies the uploaded picture into the new file.
            //The getInputStream method gets the content of the uploaded file. It provides a stream to read the content of the file.
            //The resolve method determines where to save the file.
            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file " + filename, e);
        }
    }

    private String getExtension(String filename) {
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
    //Gets the file extension (like .jpg or .png) from the original filename.
}

