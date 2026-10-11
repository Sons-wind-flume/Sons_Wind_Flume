package org.windflume;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.windflume.calibration.CalibrationCalculation;
import org.windflume.calibration.TrialResult;

public class CalculationTest {

    @Test
    void testCalibrationCalculation() {

        double waterDensity = 998.0;
        double sphereForce = 0.72;
        double testObjectForce = 1.02;
        double sphereDragCoefficient = 0.5;
        double sphereArea = 0.0022;
        double testObjectArea = 0.0003;

        CalibrationCalculation calculation =
                new CalibrationCalculation(waterDensity);

        TrialResult result =
                calculation.calculateResult(
                        sphereForce,
                        testObjectForce,
                        sphereDragCoefficient,
                        sphereArea,
                        testObjectArea
                );

        System.out.println(
                "Water velocity: "
                        + result.getWaterVelocity()
                        + " m/s"
        );

        System.out.println(
                "Test object Cd: "
                        + result.getTestObjectDragCoefficient()
        );

        assertTrue(result.getWaterVelocity() > 0);
        assertTrue(result.getTestObjectDragCoefficient() > 0);

        assertEquals(
                1.146,
                result.getWaterVelocity(),
                0.01
        );

        assertEquals(
                5.194,
                result.getTestObjectDragCoefficient(),
                0.01
        );
    }
}