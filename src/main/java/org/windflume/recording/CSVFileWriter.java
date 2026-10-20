package org.windflume.recording;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CSVFileWriter {
    private final Path exportPath = Path.of("recording.csv").toAbsolutePath();

    public Path getExportPath(){
        return exportPath;
    }

    public boolean Export(String text) {

        try {
            Files.writeString(
                 exportPath,
                    text
            );

            System.out.println("Recording exported to " + exportPath);
            return true;

        } catch (IOException e) {
            System.out.println("Could not export recording.");
            e.printStackTrace();
            return false;
        }
    }
}

