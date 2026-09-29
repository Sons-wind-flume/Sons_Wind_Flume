package org.windflume.calibration;

public class CalibrationController {
    private final VoltageForceCalibration voltageCalibration;
    private final CalibrationCalculation calculation;

    private final double sphereDragCoefficient;
    private final double sphereArea;
    private final double testObjectArea;

    public CalibrationController(double waterDensity, double sphereDragCoefficient, double sphereArea, double testObjectArea) {
        voltageCalibration = new VoltageForceCalibration();
        
        calculation = new CalibrationCalculation(waterDensity);

        this.sphereDragCoefficient = sphereDragCoefficient;

        this.sphereArea = sphereArea;

        this.testObjectArea = testObjectArea;

        }

    public TrialResult calculate(
        double sphereVoltageRatio,
        double testObjectVoltageRatio
) {

    double sphereForce =
            voltageCalibration.calculateSphereForce(
                    sphereVoltageRatio
            );

    double testObjectForce =
            voltageCalibration.calculateModelForce(
                    testObjectVoltageRatio
            );

    return calculation.calculateResult(
            sphereForce,
            testObjectForce,
            sphereDragCoefficient,
            sphereArea,
            testObjectArea
    );
}

}
