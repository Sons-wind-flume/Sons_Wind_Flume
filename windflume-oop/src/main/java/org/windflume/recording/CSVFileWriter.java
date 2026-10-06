package org.windflume.recording;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CSVFileWriter {

    public boolean Export(String text) {

        try {
            Files.writeString(
                    Path.of("recording.csv"),
                    text
            );

            System.out.println("Recording exported to recording.csv");
            return true;

        } catch (IOException e) {
            System.out.println("Could not export recording.");
            e.printStackTrace();
            return false;
        }
    }
}

