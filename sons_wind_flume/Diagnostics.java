import com.phidget22.Manager;
import com.phidget22.ManagerAttachEvent;
import com.phidget22.ManagerAttachListener;
import com.phidget22.Phidget;
import com.phidget22.PhidgetException;
import com.phidget22.VoltageRatioInput;

public class Diagnostics {

    public static void main(String[] args) {
        System.out.println("--------------------------------------------------");
        System.out.println("   Sons Wind Flume - Phidget Connection Check    ");
        System.out.println("--------------------------------------------------");

        // 1. Native Driver Check
        System.out.print("[1/3] Checking Phidget22 native library... ");
        try {
            String version = Phidget.getLibraryVersion();
            System.out.println("OK (" + version + ")");
        } catch (Throwable t) {
            System.out.println("FAILED");
            System.err.println("\nError: " + t.getMessage());
            System.err.println("Please install the Phidget22 64-bit Windows Driver (MSI) from phidgets.com.");
            return;
        }

        // 2. USB Bus Scan
        System.out.print("[2/3] Scanning USB bus for Phidgets (3s)... ");
        try {
            Manager manager = new Manager();
            manager.addAttachListener(new ManagerAttachListener() {
                @Override
                public void onAttach(ManagerAttachEvent mae) {
                    Phidget p = mae.getChannel();
                    try {
                        System.out.printf("\n      -> Found %s (Serial: %d, Channel: %d)",
                                p.getDeviceName(), p.getDeviceSerialNumber(), p.getChannel());
                    } catch (Exception ignored) {}
                }
            });
            manager.open();
            Thread.sleep(3000);
            manager.close();
            System.out.println("\n      Scan finished.");
        } catch (Exception ex) {
            System.out.println("Scan error: " + ex.getMessage());
        }

        // 3. Test Channel 0 Connection
        System.out.print("[3/3] Testing Channel 0 attach and read... ");
        try {
            VoltageRatioInput ch = new VoltageRatioInput();
            ch.setDeviceSerialNumber(Phidget.ANY_SERIAL_NUMBER);
            ch.setChannel(0);
            ch.open(2000);

            if (ch.getAttached()) {
                System.out.println("SUCCESS");
                System.out.printf("      Device: %s | Serial: %d\n", ch.getDeviceName(), ch.getDeviceSerialNumber());
                try {
                    ch.setBridgeEnabled(true);
                } catch (Exception ignored) {}
                Thread.sleep(200);
                System.out.printf("      Live Reading: %.8f V/V\n", ch.getVoltageRatio());
                System.out.println("\nPhidget setup is fully functional!");
            } else {
                System.out.println("TIMEOUT (No device attached)");
            }
            ch.close();
        } catch (PhidgetException ex) {
            System.out.println("FAILED (" + ex.getDescription() + ")");
        } catch (InterruptedException ignored) {}
    }
}
