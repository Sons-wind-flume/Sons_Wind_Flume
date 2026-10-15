package org.windflume.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.DoublePredicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ConfigLoader {

    public static final String PATH_PROPERTY = "windflume.config";

    private static final Pattern PAIR =
            Pattern.compile("\"([^\"]+)\"\\s*:\\s*(?:\"([^\"]*)\"|([^\\s,}]+))");

    public static final class Result {
        public final Configuration config;
        public final List<String> warnings;
        public final Path path;

        Result(Configuration config, List<String> warnings, Path path) {
            this.config = config;
            this.warnings = warnings;
            this.path = path;
        }
    }

    private ConfigLoader() {
    }

    public static Path defaultPath() {
        String override = System.getProperty(PATH_PROPERTY);
        if (override != null && !override.isBlank()) {
            return Path.of(override);
        }
        return Path.of(System.getProperty("user.home"), "SONS-Flume", "config.json");
    }

    public static Result load() {
        return load(defaultPath());
    }

    public static Result load(Path path) {
        List<String> warnings = new ArrayList<>();
        String json;
        try {
            if (Files.exists(path)) {
                json = Files.readString(path);
            } else {
                json = defaultJson();
                Files.createDirectories(path.toAbsolutePath().getParent());
                Files.writeString(path, json);
            }
        } catch (IOException e) {
            warnings.add("Could not read or create the config file. Built-in default values are being used.");
            return new Result(Configuration.defaults(), warnings, path);
        }
        return new Result(fromJson(json, warnings), warnings, path);
    }

    public static Configuration fromJson(String json, List<String> warnings) {
        Configuration c = Configuration.defaults();
        if (!json.trim().startsWith("{")) {
            warnings.add("The config file must start with '{'. Built-in default values are being used.");
            return c;
        }

        Map<String, String> m = new LinkedHashMap<>();
        Matcher match = PAIR.matcher(json);
        while (match.find()) {
            m.put(match.group(1), match.group(2) != null ? match.group(2) : match.group(3));
        }

        c.setWaterDensity(num(m, "waterDensity", c.getWaterDensity(), v -> v > 0, "greater than 0", warnings));

        for (int ch = 0; ch < 4; ch++) {
            c.setChannelCalibration(ch,
                    num(m, "slope" + ch, c.getSlope(ch), v -> v != 0, "anything except 0", warnings),
                    num(m, "offset" + ch, c.getOffset(ch), v -> true, "a number", warnings));
        }

        c.addTestObject(object(m, "sphere", Configuration.SPHERE, c, v -> v > 0, "greater than 0", warnings));
        // for the test object, a drag coefficient of 0 means "unknown"
        c.addTestObject(object(m, "testObject", Configuration.TEST_OBJECT, c, v -> v >= 0, "0 or more", warnings));

        for (String unknown : m.keySet()) {
            warnings.add("Unknown setting \"" + unknown + "\" was ignored.");
        }
        return c;
    }

    private static TestObject object(Map<String, String> m, String prefix, String name, Configuration c,
                                     DoublePredicate cdRule, String cdText, List<String> warnings) {
        TestObject old = c.getTestObject(name);
        TestObject obj = new TestObject(name,
                num(m, prefix + "ProjectedArea", old.getProjectedArea(), v -> v > 0, "greater than 0", warnings));
        obj.setAttachmentArea(
                num(m, prefix + "AttachmentArea", old.getAttachmentArea(), v -> v >= 0, "0 or more", warnings));
        obj.setKnownDragCoefficient(
                num(m, prefix + "DragCoefficient", old.getKnownDragCoefficient(), cdRule, cdText, warnings));
        obj.setModelId(m.getOrDefault(prefix + "ModelId", old.getModelId()).trim());
        obj.setOrientation(m.getOrDefault(prefix + "Orientation", old.getOrientation()).trim());
        m.remove(prefix + "ModelId");
        m.remove(prefix + "Orientation");
        return obj;
    }

    private static double num(Map<String, String> m, String key, double def,
                              DoublePredicate rule, String ruleText, List<String> warnings) {
        String text = m.remove(key);
        if (text == null) {
            return def;
        }
        try {
            double v = Double.parseDouble(text.trim());
            if (!Double.isNaN(v) && !Double.isInfinite(v) && rule.test(v)) {
                return v;
            }
            warnings.add("\"" + key + "\" must be " + ruleText + ". Using " + def + ".");
        } catch (NumberFormatException e) {
            warnings.add("\"" + key + "\" must be a number. Using " + def + ".");
        }
        return def;
    }

    /** The text written to a new config file. */
    public static String defaultJson() {
        return """
            {
              "waterDensity": 998.0,

              "slope0": 49160.0,
              "offset0": -4.2,
              "slope1": 47230.0,
              "offset1": -3.7,

              "sphereModelId": "SPHERE",
              "sphereOrientation": "",
              "sphereProjectedArea": 0.0022,
              "sphereAttachmentArea": 0.0,
              "sphereDragCoefficient": 0.5,

              "testObjectModelId": "",
              "testObjectOrientation": "",
              "testObjectProjectedArea": 0.003,
              "testObjectAttachmentArea": 0.0,
              "testObjectDragCoefficient": 0.0
            }
            """;
    }
}
