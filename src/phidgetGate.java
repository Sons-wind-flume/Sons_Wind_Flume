import com.phidget22.PhidgetException;
import com.phidget22.VoltageRatioInput;
import com.phidget22.VoltageRatioInputVoltageRatioChangeEvent;
import com.phidget22.VoltageRatioInputVoltageRatioChangeListener;
import java.util.ArrayList;

public class phidgetGate {

    VoltageRatioInput voltageRatioInput;
    ArrayList<Double> values;
    int channelNumber;

    public phidgetGate(int phidgetChannelNumber) {
        channelNumber = phidgetChannelNumber;
        values = new ArrayList<Double>();

        try {
            voltageRatioInput = new VoltageRatioInput();
            voltageRatioInput.setChannel(phidgetChannelNumber);

            voltageRatioInput.addVoltageRatioChangeListener(
                new VoltageRatioInputVoltageRatioChangeListener() {
                    @Override
                    public void onVoltageRatioChange(VoltageRatioInputVoltageRatioChangeEvent event) {
                        values.add(event.getVoltageRatio());
                    }
                }
            );

            voltageRatioInput.open(1000);
        } catch (PhidgetException exception) {
            System.out.println(exception.getMessage());
        }
    }

    public void close() {
        try {
            if (voltageRatioInput != null) {
                voltageRatioInput.close();
            }
        } catch (PhidgetException exception) {
            System.out.println(exception.getMessage());
        }
    }

    public void sortValues() {
        int firstIndex;
        int secondIndex;
        double firstValue;
        double secondValue;

        for (firstIndex = 0; firstIndex < values.size(); firstIndex = firstIndex + 1) {
            for (secondIndex = firstIndex + 1; secondIndex < values.size(); secondIndex = secondIndex + 1) {
                firstValue = values.get(firstIndex);
                secondValue = values.get(secondIndex);

                if (firstValue > secondValue) {
                    values.set(firstIndex, secondValue);
                    values.set(secondIndex, firstValue);
                }
            }
        }
    }

    public double getMedianVoltageValue() {
        int length;
        double median;

        if (values == null || values.isEmpty()) {
            return 0.0;
        }

        sortValues();
        length = values.size();
        median = 0.0;

        if (length % 2 != 0) {
            median = values.get(length / 2);
        } else {
            median = values.get((length / 2) - 1) + values.get(length / 2);
            median = median / 2.0;
        }

        resetValues();
        return median;
    }

    public void resetValues() {
        values = new ArrayList<Double>();
    }

    public int getChannel() {
        return channelNumber;
    }
}
