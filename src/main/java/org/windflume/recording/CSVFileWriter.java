package org.windflume.recording;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CSVFileWriter {

    private Path lastFile;

    public static Path exportFolder() {
        return Path.of(System.getProperty("user.home"), "SONS-Flume", "exports");
    }

    public boolean Export(String text) {
        try {
            Path folder = exportFolder();
            Files.createDirectories(folder);

            String stamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            lastFile = folder.resolve("recording_" + stamp + ".csv");

            Files.writeString(lastFile, text);
            System.out.println("Recording exported to " + lastFile);
            return true;

        } catch (IOException e) {
            System.out.println("Could not export recording.");
            e.printStackTrace();
            return false;
        }
    }

    public Path getLastFile() {
        return lastFile;
    }
}

