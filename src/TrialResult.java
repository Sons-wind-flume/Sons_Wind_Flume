public class TrialResult {

    private double sphereForce;
    private double testObjectForce;
    private double waterVelocity;
    private double testObjectDragCoefficient;

    public TrialResult(
            double sphereForce,
            double testObjectForce,
            double waterVelocity,
            double testObjectDragCoefficient) {

        this.sphereForce = sphereForce;
        this.testObjectForce = testObjectForce;
        this.waterVelocity = waterVelocity;
        this.testObjectDragCoefficient = testObjectDragCoefficient;
    }

    public double getSphereForce() {
        return sphereForce;
    }

    public double getTestObjectForce() {
        return testObjectForce;
    }

    public double getWaterVelocity() {
        return waterVelocity;
    }

    public double getTestObjectDragCoefficient() {
        return testObjectDragCoefficient;
    }
}
