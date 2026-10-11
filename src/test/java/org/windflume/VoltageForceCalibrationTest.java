package org.windflume;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.windflume.calibration.VoltageForceCalibration;
import org.windflume.config.Configuration;

public class VoltageForceCalibrationTest {

    // Original test: the default numbers must not change
    @Test
    void testVoltageToForceCalibration() {

        VoltageForceCalibration calibration =
                new VoltageForceCalibration();

        double sphereForce =
                calibration.calculateSphereForce(0.0001);

        double modelForce =
                calibration.calculateModelForce(0.0001);

        System.out.println("Sphere force: " + sphereForce + " N");
        System.out.println("Model force: " + modelForce + " N");

        assertEquals(0.716, sphereForce, 0.001);
        assertEquals(1.023, modelForce, 0.001);
    }

    // calculateForce(channel, ratio) must match the old sphere/model methods
    @Test
    void testCalculateForceMatchesChannelMethods() {

        VoltageForceCalibration calibration =
                new VoltageForceCalibration();

        assertEquals(
                calibration.calculateSphereForce(0.0001),
                calibration.calculateForce(0, 0.0001),
                1e-9);

        assertEquals(
                calibration.calculateModelForce(0.0001),
                calibration.calculateForce(1, 0.0001),
                1e-9);
    }

    // The calibration must read its numbers from the Configuration
    @Test
    void testCalibrationUsesConfiguration() {

        Configuration config = new Configuration();
        config.setChannelCalibration(0, 100.0, 1.0);

        VoltageForceCalibration calibration =
                new VoltageForceCalibration(config);

        // 0.5 * 100 + 1 = 51
        assertEquals(51.0, calibration.calculateForce(0, 0.5), 1e-9);
    }
}