import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigLoader {

    public static TrialConfig loadConfig(String path)
            throws IOException {

        String json = Files.readString(
                Path.of(path)
        );

        TrialConfig config = new TrialConfig(
                readString(json, "attachmentArea"),
                readString(json, "modelId"),
                readDouble(json, "projectedArea"),
                readString(json, "orientation"),
                readString(json, "experimenter"),
                readDouble(json, "dragCoefficient")
        );

        if (!validateConfig(config)) {
            throw new IllegalArgumentException(
                    "Configuration is invalid."
            );
        }

        return config;
    }

    public static boolean validateConfig(
            TrialConfig config) {

        if (config == null) {
            return false;
        }

        if (config.getAttachmentArea() == null
                || config.getAttachmentArea().isBlank()) {
            return false;
        }

        if (config.getModelId() == null
                || config.getModelId().isBlank()) {
            return false;
        }

        if (config.getProjectedArea() <= 0) {
            return false;
        }

        if (config.getOrientation() == null
                || config.getOrientation().isBlank()) {
            return false;
        }

        if (config.getExperimenter() == null
                || config.getExperimenter().isBlank()) {
            return false;
        }

        if (config.getDragCoefficient() <= 0) {
            return false;
        }

        return true;
    }

    private static String readString(
            String json,
            String key) {

        Pattern pattern = Pattern.compile(
                "\""
                + Pattern.quote(key)
                + "\"\\s*:\\s*\"([^\"]*)\""
        );

        Matcher matcher = pattern.matcher(json);

        if (!matcher.find()) {
            throw new IllegalArgumentException(
                    "Missing value: " + key
            );
        }

        return matcher.group(1);
    }

    private static double readDouble(
            String json,
            String key) {

        Pattern pattern = Pattern.compile(
                "\""
                + Pattern.quote(key)
                + "\"\\s*:\\s*"
                + "(-?\\d+(?:\\.\\d+)?"
                + "(?:[eE][+-]?\\d+)?)"
        );

        Matcher matcher = pattern.matcher(json);

        if (!matcher.find()) {
            throw new IllegalArgumentException(
                    "Missing value: " + key
            );
        }

        return Double.parseDouble(
                matcher.group(1)
        );
    }
}