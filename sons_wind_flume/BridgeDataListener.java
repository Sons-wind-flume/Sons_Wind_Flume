public interface BridgeDataListener {
    void onReading(SensorReading reading);
    default void onDeviceAttached(int channel, String deviceName, int serialNumber) {}
    default void onDeviceDetached(int channel) {}
    default void onError(int channel, String message) {}
}
