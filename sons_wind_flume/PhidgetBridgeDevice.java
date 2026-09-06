import com.phidget22.AttachEvent;
import com.phidget22.AttachListener;
import com.phidget22.BridgeGain;
import com.phidget22.DetachEvent;
import com.phidget22.DetachListener;
import com.phidget22.ErrorEvent;
import com.phidget22.ErrorListener;
import com.phidget22.Phidget;
import com.phidget22.PhidgetException;
import com.phidget22.VoltageRatioInput;
import com.phidget22.VoltageRatioInputVoltageRatioChangeEvent;
import com.phidget22.VoltageRatioInputVoltageRatioChangeListener;

public class PhidgetBridgeDevice implements BridgeDevice {

    private static final int DEFAULT_CHANNELS = 4;
    private static final int ATTACH_TIMEOUT_MS = 3000;

    private final int serialNumber;
    private final int channelCount;
    private int dataIntervalMs = 1000;

    private VoltageRatioInput[] channels;
    private BridgeDataListener listener;
    private boolean connected = false;
    private String detectedDeviceName = "PhidgetBridge";

    public PhidgetBridgeDevice() {
        this(Phidget.ANY_SERIAL_NUMBER, DEFAULT_CHANNELS);
    }

    public PhidgetBridgeDevice(int serialNumber, int channelCount) {
        this.serialNumber = serialNumber;
        this.channelCount = Math.max(1, Math.min(channelCount, 4));
        this.channels = new VoltageRatioInput[this.channelCount];
    }

    @Override
    public synchronized void open() throws PhidgetException {
        if (connected) {
            return;
        }

        for (int i = 0; i < channelCount; i++) {
            final int ch = i;
            channels[ch] = new VoltageRatioInput();

            if (serialNumber != Phidget.ANY_SERIAL_NUMBER) {
                channels[ch].setDeviceSerialNumber(serialNumber);
            }
            channels[ch].setChannel(ch);

            // Configure channel hardware once attached
            channels[ch].addAttachListener(new AttachListener() {
                @Override
                public void onAttach(AttachEvent ae) {
                    try {
                        VoltageRatioInput src = (VoltageRatioInput) ae.getSource();
                        detectedDeviceName = src.getDeviceName();
                        connected = true;

                        // Set sampling interval within sensor capabilities
                        int interval = Math.max(src.getMinDataInterval(),
                                Math.min(src.getMaxDataInterval(), dataIntervalMs));
                        src.setDataInterval(interval);
                        src.setVoltageRatioChangeTrigger(0.0);

                        // Power the bridge excitation circuit
                        try {
                            src.setBridgeEnabled(true);
                        } catch (PhidgetException ignored) {}

                        // 128x is standard for strain gauges to amplify tiny mV signals
                        try {
                            src.setBridgeGain(BridgeGain.GAIN_128X);
                        } catch (PhidgetException ignored) {}

                        if (listener != null) {
                            listener.onDeviceAttached(ch, src.getDeviceName(), src.getDeviceSerialNumber());
                        }
                    } catch (PhidgetException ex) {
                        if (listener != null) {
                            listener.onError(ch, "Config error: " + ex.getDescription());
                        }
                    }
                }
            });

            channels[ch].addDetachListener(new DetachListener() {
                @Override
                public void onDetach(DetachEvent de) {
                    if (ch == 0) connected = false;
                    if (listener != null) {
                        listener.onDeviceDetached(ch);
                    }
                }
            });

            channels[ch].addErrorListener(new ErrorListener() {
                @Override
                public void onError(ErrorEvent ee) {
                    if (listener != null) {
                        listener.onError(ch, ee.getDescription());
                    }
                }
            });

            channels[ch].addVoltageRatioChangeListener(new VoltageRatioInputVoltageRatioChangeListener() {
                @Override
                public void onVoltageRatioChange(VoltageRatioInputVoltageRatioChangeEvent ev) {
                    if (listener != null) {
                        listener.onReading(new SensorReading(ch, ev.getVoltageRatio()));
                    }
                }
            });

            channels[ch].open(ATTACH_TIMEOUT_MS);
        }
    }

    @Override
    public synchronized void close() {
        for (int i = 0; i < channels.length; i++) {
            if (channels[i] != null) {
                try {
                    channels[i].close();
                } catch (PhidgetException ignored) {}
                channels[i] = null;
            }
        }
        connected = false;
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
        for (VoltageRatioInput ch : channels) {
            if (ch != null && connected) {
                try {
                    ch.setDataInterval(this.dataIntervalMs);
                } catch (PhidgetException ignored) {}
            }
        }
    }

    @Override
    public void setListener(BridgeDataListener listener) {
        this.listener = listener;
    }

    @Override
    public String getDeviceName() {
        return detectedDeviceName;
    }

    @Override
    public int getSerialNumber() {
        if (channels[0] != null) {
            try {
                return channels[0].getDeviceSerialNumber();
            } catch (PhidgetException ignored) {}
        }
        return serialNumber;
    }
}
