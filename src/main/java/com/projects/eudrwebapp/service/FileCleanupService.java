package com.projects.eudrwebapp.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Date;

@Service
public class FileCleanupService {

    private static final String DIRECTORY_PATH = "src/main/resources/static/tmp/";  // Path to the directory where PDFs are saved
    private static final long EXPIRATION_TIME = 60 * 300;  //Rest for 1day: 24 * 60 *  * 1000

    @Scheduled(fixedRate = EXPIRATION_TIME)
    public void deleteOldFiles() {
        File directory = new File(DIRECTORY_PATH);
        if (!directory.exists()) {
            return;  // No need to proceed if the directory doesn't exist
        }

        // Get all files in the directory
        File[] files = directory.listFiles();
        if (files != null) {
            long currentTime = new Date().getTime();

            for (File file : files) {
                if (file.isFile()) {
                    if (currentTime - file.lastModified() > EXPIRATION_TIME) {
                        if (file.delete()) {
                            System.out.println("Deleted old file: " + file.getName());
                        } else {
                            System.out.println("Failed to delete file: " + file.getName());
                        }
                    }
                }
            }
        }
    }
}
