package org.windflume.calibration;

import org.windflume.config.Configuration;

public class VoltageForceCalibration {
     private final Configuration config;

    public VoltageForceCalibration() {
        this(Configuration.defaults());   // same numbers as before
    }

    public VoltageForceCalibration(Configuration config) {
        this.config = config;
    }

    public double calculateForce(int channel, double ratio) {
        return ratio * config.getSlope(channel) + config.getOffset(channel);
    }

    public double calculateSphereForce(double ratio) {
        return calculateForce(0, ratio);
    }

    public double calculateModelForce(double ratio) {
        return calculateForce(1, ratio);
    }
}
  

