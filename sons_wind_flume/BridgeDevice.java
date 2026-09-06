public interface BridgeDevice {

    void open() throws Exception;

    void close();

    boolean isConnected();

    int getChannelCount();

    void setDataInterval(int intervalMs);

    void setListener(BridgeDataListener listener);

    String getDeviceName();

    int getSerialNumber();
}
