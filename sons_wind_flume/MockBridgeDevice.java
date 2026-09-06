import java.util.Random;
import java.util.concurrent.*;

public class MockBridgeDevice implements BridgeDevice {

    private static final int DEFAULT_CHANNELS = 4;

    private final int channelCount;
    private final int serialNumber = 998877;
    private final String deviceName = "Mock PhidgetBridge 4-Input";

    private final double[] baseOffsets;
    private final Random random = new Random();

    private BridgeDataListener listener;
    private ScheduledExecutorService scheduler;
    private int dataIntervalMs = 1000;
    private boolean connected = false;

    public MockBridgeDevice() {
        this(DEFAULT_CHANNELS);
    }

    public MockBridgeDevice(int channelCount) {
        this.channelCount = Math.max(1, Math.min(channelCount, 4));
        this.baseOffsets = new double[this.channelCount];
        for (int i = 0; i < this.channelCount; i++) {
            this.baseOffsets[i] = 0.000120 * (i + 1) + (random.nextDouble() * 0.000030);
        }
    }

    @Override
    public synchronized void open() {
        if (connected) return;
        connected = true;

        if (listener != null) {
            for (int i = 0; i < channelCount; i++) {
                listener.onDeviceAttached(i, deviceName, serialNumber);
            }
        }

        startDataStream();
    }

    private void startDataStream() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
        }

        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            if (!connected || listener == null) return;

            long t = System.currentTimeMillis();
            for (int ch = 0; ch < channelCount; ch++) {
                double wave = Math.sin(t / 2500.0 + (ch * 1.2)) * 0.000045;
                double noise = random.nextGaussian() * 0.000003;
                double reading = baseOffsets[ch] + wave + noise;
                listener.onReading(new SensorReading(ch, reading));
            }
        }, dataIntervalMs, dataIntervalMs, TimeUnit.MILLISECONDS);
    }

    @Override
    public synchronized void close() {
        if (!connected) return;
        connected = false;

        if (scheduler != null) {
            scheduler.shutdownNow();
            scheduler = null;
        }

        if (listener != null) {
            for (int i = 0; i < channelCount; i++) {
                listener.onDeviceDetached(i);
            }
        }
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public int getChannelCount() {
        return channelCount;
    }

    @Override
    public void setDataInterval(int intervalMs) {
        this.dataIntervalMs = Math.max(10, intervalMs);
        if (connected) {
            startDataStream();
        }
    }

    @Override
    public void setListener(BridgeDataListener listener) {
        this.listener = listener;
    }

    @Override
    public String getDeviceName() {
        return deviceName;
    }

    @Override
    public int getSerialNumber() {
        return serialNumber;
    }
}
