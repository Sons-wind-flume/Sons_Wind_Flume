package org.windflume.phidget;

import java.util.ArrayList;
import java.util.Collections;

import com.phidget22.PhidgetException;
import com.phidget22.VoltageRatioInput;

public class PhidgetGate {

    private final VoltageRatioInput voltageRatioInput;
    private final ArrayList<Double> values;
    private final int channel;
    private final boolean connected;

    // Real hardware constructor
    public PhidgetGate(int channel) throws PhidgetException {

        this.channel = channel;
        this.values = new ArrayList<>();
        this.connected = true;

        this.voltageRatioInput = new VoltageRatioInput();

        voltageRatioInput.setChannel(channel);

        voltageRatioInput.addVoltageRatioChangeListener(event -> {

            synchronized (values) {
                values.add(event.getVoltageRatio());
            }

        });

        voltageRatioInput.open(1000);

        System.out.println(
                "Phidget channel " + channel + " connected."
        );
    }

    // Offline/display-only constructor
    public PhidgetGate(int channel, boolean offline) {

        this.channel = channel;
        this.values = new ArrayList<>();
        this.connected = false;
        this.voltageRatioInput = null;

        System.out.println(
                "Phidget channel " + channel +
                " running in offline mode."
        );
    }

    public double getMedianVoltageValue() {

        synchronized (values) {

            if (values.isEmpty()) {
                return 0.0;
            }

            ArrayList<Double> sortedValues =
                    new ArrayList<>(values);

            Collections.sort(sortedValues);

            int middle = sortedValues.size() / 2;

            if (sortedValues.size() % 2 == 0) {

                return (
                        sortedValues.get(middle - 1)
                        + sortedValues.get(middle)
                ) / 2.0;

            } else {

                return sortedValues.get(middle);
            }
        }
    }

    public void resetValues() {

        synchronized (values) {
            values.clear();
        }
    }

    public int getChannel() {
        return channel;
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

        } catch (PhidgetException e) {

            e.printStackTrace();
        }
    }
}