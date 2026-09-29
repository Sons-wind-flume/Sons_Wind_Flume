package org.windflume;


import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.windflume.calibration.VoltageForceCalibration;

public class VoltageForceCalibrationTest {

    @Test
    void testVoltageToForceCalibration() {

        VoltageForceCalibration calibration =
                new VoltageForceCalibration();

        double sphereForce =
                calibration.calculateSphereForce(0.0001);

        double modelForce =
                calibration.calculateModelForce(0.0001);

        System.out.println(
                "Sphere force: " + sphereForce + " N"
        );

        System.out.println(
                "Model force: " + modelForce + " N"
        );

        assertEquals(
                0.716,
                sphereForce,
                0.001
        );

        assertEquals(
                1.023,
                modelForce,
                0.001
        );
    }
}