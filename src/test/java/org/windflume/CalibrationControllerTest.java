package org.windflume;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.windflume.calibration.CalibrationController;
import org.windflume.calibration.TrialResult;

public class CalibrationControllerTest {

    @Test
    void testCompleteCalibrationPipeline() {

        CalibrationController controller =
                new CalibrationController(
                        998.0,
                        0.5,
                        0.0022,
                        0.0003
                );

        double sphereVoltageRatio = 0.0001;
        double testObjectVoltageRatio = 0.0001;

        TrialResult result =
                controller.calculate(
                        sphereVoltageRatio,
                        testObjectVoltageRatio
                );

        System.out.println(
                "Sphere force: "
                        + result.getSphereForce()
                        + " N"
        );

        System.out.println(
                "Test object force: "
                        + result.getTestObjectForce()
                        + " N"
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

        assertEquals(
                0.716,
                result.getSphereForce(),
                0.001
        );

        assertEquals(
                1.023,
                result.getTestObjectForce(),
                0.001
        );

        assertEquals(
                1.142,
                result.getWaterVelocity(),
                0.01
        );

        assertEquals(
                5.23,
                result.getTestObjectDragCoefficient(),
                0.1
        );
    }
}
