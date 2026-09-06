import java.io.*;
import java.nio.file.*;

public class CsvDataLogger implements Closeable {

    private final PrintWriter writer;
    private final String filePath;
    private long recordCount = 0;

    public CsvDataLogger(String filePath) throws IOException {
        this.filePath = filePath;
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        boolean exists = file.exists() && file.length() > 0;
        this.writer = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));

        if (!exists) {
            writer.println("Timestamp,Channel,Label,VoltageRatio_V_per_V");
            writer.flush();
        }
    }

    public synchronized void writeReading(SensorReading reading, String label) {
        if (writer == null) return;
        writer.printf("%s,%d,%s,%.8f\n",
                reading.getTimestamp(),
                reading.getChannel(),
                label == null ? "" : label,
                reading.getVoltageRatio());
        writer.flush();
        recordCount++;
    }

    public long getRecordCount() {
        return recordCount;
    }

    public String getFilePath() {
        return filePath;
    }

    public static String getNextAvailableFileName(String baseDir, String prefix) {
        int i = 0;
        File f;
        do {
            f = new File(baseDir, prefix + "_" + i + ".csv");
            i++;
        } while (f.exists());
        return f.getAbsolutePath();
    }

    @Override
    public synchronized void close() {
        if (writer != null) {
            writer.flush();
            writer.close();
        }
    }
}
