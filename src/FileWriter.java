import java.io.PrintWriter;
import java.io.FileNotFoundException;

public class FileWriter {

    public void export(String text) {
        PrintWriter writer;

        try {
            writer = new PrintWriter("recording.csv");
            writer.print(text);
            writer.close();
            System.out.println("File exported successfully!");
        } catch (FileNotFoundException exception) {
            System.out.println("The CSV file could not be created.");
        }
    }
}
