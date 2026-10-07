import com.phidget22.*;
import java.util.ArrayList;

public class phidgetGate {

    VoltageRatioInput voltageRatioInput;
    ArrayList<Double> values;
    int ChannelNum;
    boolean connected;

    public phidgetGate(int phidgetChannelNum) {

        ChannelNum = phidgetChannelNum;
        connected = false;
        values = new ArrayList<>();

        try {

            voltageRatioInput =
                    new VoltageRatioInput();

            voltageRatioInput.setChannel(
                    phidgetChannelNum
            );

            voltageRatioInput.open(1000);

            connected = true;

            voltageRatioInput
                    .addVoltageRatioChangeListener(
                            (VoltageRatioInputVoltageRatioChangeEvent e) -> {

                                values.add(
                                        e.getVoltageRatio()
                                );
                            }
                    );

        } catch (Exception e) {

            connected = false;
        }
    }

    public boolean isConnected() {
        return connected;
    }

    public void close() {

        if (voltageRatioInput == null) {
            return;
        }

        try {

            voltageRatioInput.close();

        } catch (Exception e) {
        }
    }

    public void sort() {

        values.sort(null);
    }

    public double getMedianVoltageValue() {

        if (values.isEmpty()) {
            return 0;
        }

        sort();

        int len = values.size();

        double median;

        if (len % 2 != 0) {

            median =
                    values.get(len / 2);

        } else {

            median =
                    (
                            values.get(
                                    (len / 2) - 1
                            )
                            + values.get(
                                    len / 2
                            )
                    ) / 2.0;
        }

        return median;
    }

    public void resetValues() {

        values = new ArrayList<>();
    }

    public int getChannel() {

        return ChannelNum;
    }
}