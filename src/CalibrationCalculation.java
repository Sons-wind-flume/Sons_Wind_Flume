public class CalibrationCalculation {

    private double waterDensity;

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

        double numerator;
        double denominator;
        double velocitySquared;
        double waterVelocity;

        numerator = 2.0 * sphereForce;

        denominator =
                waterDensity
                * sphereDragCoefficient
                * sphereArea;

        velocitySquared = numerator / denominator;
        waterVelocity = Math.sqrt(velocitySquared);

        return waterVelocity;
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

        double numerator;
        double denominator;
        double dragCoefficient;

        numerator = 2.0 * testObjectForce;

        denominator =
                waterDensity
                * testObjectArea
                * waterVelocity
                * waterVelocity;

        dragCoefficient = numerator / denominator;

        return dragCoefficient;
    }

    public TrialResult calculateResult(
            double sphereForce,
            double testObjectForce,
            double sphereDragCoefficient,
            double sphereArea,
            double testObjectArea) {

        double waterVelocity;
        double testObjectDragCoefficient;
        TrialResult result;

        waterVelocity = calculateWaterVelocity(
                sphereForce,
                sphereDragCoefficient,
                sphereArea
        );

        if (waterVelocity <= 0) {
            testObjectDragCoefficient = -1.0;
        } else {
            testObjectDragCoefficient = calculateDragCoefficient(
                    testObjectForce,
                    testObjectArea,
                    waterVelocity
            );
        }

        result = new TrialResult(
                sphereForce,
                testObjectForce,
                waterVelocity,
                testObjectDragCoefficient
        );

        return result;
    }

    public double convertSquareCentimetresToSquareMetres(
            double areaInSquareCentimetres) {

        double areaInSquareMetres;
        areaInSquareMetres = areaInSquareCentimetres / 10000.0;

        return areaInSquareMetres;
    }
}
