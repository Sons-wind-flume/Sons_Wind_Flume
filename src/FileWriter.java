import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileWriter {
    public void Export(String text){
    Path path = Path.of("recording.csv");
        String content = text;

        try {
            // Creates the file (or overwrites it if it exists) and writes text
            Files.writeString(path, content);
            System.out.println("File exported successfully!");
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }

    

}
