package org.windflume.calibration;

public class CalibrationCalculation {

    private final double waterDensity;

    public CalibrationCalculation(double waterDensity) {
        this.waterDensity = waterDensity;
    }

    public double calculateWaterVelocity(
            double sphereForce,
            double sphereDragCoefficient,
            double sphereArea) {

        if (sphereForce < 0) {
            return -1.0;
        }

        if (sphereDragCoefficient <= 0) {
            return -1.0;
        }

        if (sphereArea <= 0) {
            return -1.0;
        }

        if (waterDensity <= 0) {
            return -1.0;
        }

        if (sphereForce == 0) {
            return 0.0;
        }

        double numerator = 2.0 * sphereForce;

        double denominator =
                waterDensity
                * sphereDragCoefficient
                * sphereArea;

        double velocitySquared =
                numerator / denominator;

        return Math.sqrt(velocitySquared);
    }

    public double calculateDragCoefficient(
            double testObjectForce,
            double testObjectArea,
            double waterVelocity) {

        if (testObjectForce < 0) {
            return -1.0;
        }

        if (testObjectArea <= 0) {
            return -1.0;
        }

        if (waterVelocity <= 0) {
            return -1.0;
        }

        if (waterDensity <= 0) {
            return -1.0;
        }

        double numerator =
                2.0 * testObjectForce;

        double denominator =
                waterDensity
                * testObjectArea
                * waterVelocity
                * waterVelocity;

        return numerator / denominator;
    }

    public TrialResult calculateResult(
            double sphereForce,
            double testObjectForce,
            double sphereDragCoefficient,
            double sphereArea,
            double testObjectArea) {

        double waterVelocity =
                calculateWaterVelocity(
                        sphereForce,
                        sphereDragCoefficient,
                        sphereArea
                );

        double testObjectDragCoefficient;

        if (waterVelocity <= 0) {
            testObjectDragCoefficient = -1.0;
        } else {
            testObjectDragCoefficient =
                    calculateDragCoefficient(
                            testObjectForce,
                            testObjectArea,
                            waterVelocity
                    );
        }

        return new TrialResult(
                sphereForce,
                testObjectForce,
                waterVelocity,
                testObjectDragCoefficient
        );
    }

    public double convertSquareCentimetresToSquareMetres(
            double areaInSquareCentimetres) {

        return areaInSquareCentimetres / 10000.0;
    }
}