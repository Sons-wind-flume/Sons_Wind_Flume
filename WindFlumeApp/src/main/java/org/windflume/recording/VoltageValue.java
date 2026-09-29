package org.windflume.recording;

import java.text.DecimalFormat;

public class VoltageValue {

    private final DecimalFormat df =
            new DecimalFormat("0.00000");

    public double voltage;

    public VoltageValue(double vol) {

        voltage = vol;
    }

    @Override
    public String toString() {

        return df.format(voltage);
    }
}