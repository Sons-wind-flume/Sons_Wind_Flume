public class TrialConfig {

    private String attachmentArea;
    private String modelId;
    private double projectedArea;
    private String orientation;
    private String experimenter;
    private double dragCoefficient;

    public TrialConfig(
            String attachmentArea,
            String modelId,
            double projectedArea,
            String orientation,
            String experimenter,
            double dragCoefficient) {

        this.attachmentArea = attachmentArea;
        this.modelId = modelId;
        this.projectedArea = projectedArea;
        this.orientation = orientation;
        this.experimenter = experimenter;
        this.dragCoefficient = dragCoefficient;
    }

    public static TrialConfig defaults() {
        return new TrialConfig(
                "N/A",
                "DEFAULT",
                0.0,
                "Default",
                "Unknown",
                0.0
        );
    }

    public String getAttachmentArea() {
        return attachmentArea;
    }

    public String getModelId() {
        return modelId;
    }

    public double getProjectedArea() {
        return projectedArea;
    }

    public String getOrientation() {
        return orientation;
    }

    public String getExperimenter() {
        return experimenter;
    }

    public double getDragCoefficient() {
        return dragCoefficient;
    }
}