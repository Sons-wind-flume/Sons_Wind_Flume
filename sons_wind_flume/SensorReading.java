import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SensorReading {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private final int channel;
    private final double voltageRatio;
    private final String timestamp;

    public SensorReading(int channel, double voltageRatio) {
        this.channel = channel;
        this.voltageRatio = voltageRatio;
        this.timestamp = LocalDateTime.now().format(FORMATTER);
    }

    public SensorReading(int channel, double voltageRatio, String timestamp) {
        this.channel = channel;
        this.voltageRatio = voltageRatio;
        this.timestamp = timestamp;
    }

    public int getChannel() {
        return channel;
    }

    public double getVoltageRatio() {
        return voltageRatio;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("[%s] Ch%d: %+11.8f V/V", timestamp, channel, voltageRatio);
    }
}
