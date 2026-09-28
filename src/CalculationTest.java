public class CalculationTest {

    public static void main(String[] args) {
        double waterDensity;
        double sphereForce;
        double testObjectForce;
        double sphereDragCoefficient;
        double sphereArea;
        double testObjectArea;

        waterDensity = 998.0;
        sphereForce = 0.72;
        testObjectForce = 1.02;
        sphereDragCoefficient = 0.5;
        sphereArea = 0.0022;
        testObjectArea = 0.0003;

        CalibrationCalculation calculation;
        calculation = new CalibrationCalculation(waterDensity);

        TrialResult result;
        result = calculation.calculateResult(
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
    }
}
