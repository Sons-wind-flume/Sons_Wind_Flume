package org.windflume.calibration;

public class VoltageForceCalibration {
    private final double sphereSlope;
    private final double sphereIntercept;

    private final double modelSlope;
    private final double modelIntercept;

    public VoltageForceCalibration() {
        sphereSlope = 49160.0;
        sphereIntercept = -4.2;

        modelSlope = 47230.0;
        modelIntercept = -3.7;


    }

    public double calculateSphereForce(double voltageRatio) {
        return voltageRatio * sphereSlope + sphereIntercept;
    }

    public double calculateModelForce(double voltageRatio) {
        return voltageRatio * modelSlope + modelIntercept ;
    }
    
}
