package org.windflume.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.windflume.phidget.PhidgetGate;

import com.phidget22.PhidgetException;


public final class ConfigLoader {

    public static final String PATH_PROPERTY = "windflume.config";

    private static final Pattern SETTING =
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

    // Finding and reading the file
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
                json = defaultJson(); // first run: create the file with default values
                Files.createDirectories(path.toAbsolutePath().getParent());
                Files.writeString(path, json);
            }
        } catch (IOException e) {
            warnings.add("Could not read or create the config file. Built-in default values are being used.");
            return new Result(defaultsWithGates(), warnings, path);
        }

        return new Result(fromJson(json, warnings), warnings, path);
    }

    // Turning the file text into a Configuration

    public static Configuration fromJson(String json, List<String> warnings) {

        if (!json.trim().startsWith("{")){
            warnings.add("The config file must start with '{'. Built-in default values are being used.");
            return defaultsWithGates();
        }
        
        Configuration config = Configuration.defaults();
        Configuration defaults = Configuration.defaults();
        Map<String, String> settings = findSettings(json);

        for (int channel : activeGates(settings, warnings)){
            config.addGate(newGate(channel));
        }

        config.setWaterDensity(
                number(settings, "waterDensity", defaults.getWaterDensity(), Rule.POSITIVE, warnings));

        for (int channel = 0; channel < 4; channel++) {
            double slope = number(settings, "slope" + channel, defaults.getSlope(channel), Rule.NOT_ZERO, warnings);
            double offset = number(settings, "offset" + channel, defaults.getOffset(channel), Rule.ANY, warnings);
            config.setChannelCalibration(channel, defaults.getSlope(channel), defaults.getOffset(channel));
        }

        // The sphere's drag coefficient must be known
        config.addTestObject(readObject(settings, "sphere", Configuration.SPHERE,
                config, Rule.POSITIVE, warnings));
        config.addTestObject(readObject(settings, "testObject", Configuration.TEST_OBJECT,
                config, Rule.ZERO_OR_MORE, warnings));

        // Anything still in the map was not recognised
        for (String name : settings.keySet()) {
            warnings.add("Unknown setting \"" + name + "\" was ignored.");
        }
        return config;
    }


    //fallback config that still has both gates
    private static Configuration defaultsWithGates(){
        Configuration config = Configuration.defaults();
        Configuration defaults = Configuration.defaults();
        for (int channel : new int[] {0, 1}){
            config.addGate(newGate(channel));
            config.setChannelCalibration(channel, defaults.getSlope(channel), defaults.getOffset(channel));
        }
        return config;
    }

  
    private static Map<String, String> findSettings(String json) {
        Map<String, String> settings = new LinkedHashMap<>();
        Matcher matcher = SETTING.matcher(json);
        while (matcher.find()) {
            String quotedText = matcher.group(2);
            String bareValue = matcher.group(3);
            settings.put(matcher.group(1), quotedText != null ? quotedText : bareValue);
        }
        return settings;
    }

    //hardware fallback
    private static PhidgetGate newGate(int channel){
        try {
            return new PhidgetGate(channel);
        } catch (PhidgetException | LinkageError e) {
            return new PhidgetGate(channel, true);
        }
    }

    //"activeGates": "0,1"
    private static List<Integer> activeGates(Map<String, String> settings, List<String> warnings){
        List<Integer> channels = new ArrayList<>();
        String text = settings.remove("activeGates");
        if (text == null){
            text = "0,1";
        }
        for (String part : text.split(",")){
            try {
                int channel = Integer.parseInt(part.trim());
                if (channel < 0 || channel > 3){
                    throw new NumberFormatException();
                }
                if (!channels.contains(channel)){
                    channels.add(channel);
                }
                
            } catch (NumberFormatException e) {
                warnings.add("\"activeGates\" must list channel numbers 0 to 3, like \"0,1\". Ignored \"" + part.trim() + "\".");
            }
        }
        if (channels.isEmpty()){
            channels.add(0);
            channels.add(1);
        }
        return channels;
    }


    private static TestObject readObject(Map<String, String> settings, String prefix, String name,
                                         Configuration config, Rule dragRule, List<String> warnings) {
        TestObject current = config.getTestObject(name); 

        TestObject object = new TestObject(name,
                number(settings, prefix + "ProjectedArea", current.getProjectedArea(), Rule.POSITIVE, warnings));
        object.setAttachmentArea(
                number(settings, prefix + "AttachmentArea", current.getAttachmentArea(), Rule.ZERO_OR_MORE, warnings));
        object.setCentroidHeight(number(settings, prefix + "CentroidHeight", current.getCentroidHeight(), Rule.ZERO_OR_MORE, warnings));
        object.setKnownDragCoefficient(
                number(settings, prefix + "DragCoefficient", current.getKnownDragCoefficient(), dragRule, warnings));
        object.setModelId(text(settings, prefix + "ModelId", current.getModelId()));
        object.setOrientation(text(settings, prefix + "Orientation", current.getOrientation()));
        return object;
    }

    // Checking values

    private enum Rule {
        ANY("a number"),
        POSITIVE("greater than 0"),
        ZERO_OR_MORE("0 or more"),
        NOT_ZERO("anything except 0");

        final String description;

        Rule(String description) {
            this.description = description;
        }

        boolean allows(double value) {
            return switch (this) {
                case ANY -> true;
                case POSITIVE -> value > 0;
                case ZERO_OR_MORE -> value >= 0;
                case NOT_ZERO -> value != 0;
            };
        }
    }

    // Reads a number. If it is missing, the default is used. If it is invalid, the default is used and a warning added. 
    private static double number(Map<String, String> settings, String name, double defaultValue,
                                 Rule rule, List<String> warnings) {
        String text = settings.remove(name);
        if (text == null) {
            return defaultValue;
        }

        try {
            double value = Double.parseDouble(text.trim());
            if (!Double.isNaN(value) && !Double.isInfinite(value) && rule.allows(value)) {
                return value;
            }
            warnings.add("\"" + name + "\" must be " + rule.description + ". Using " + defaultValue + ".");
        } catch (NumberFormatException e) {
            warnings.add("\"" + name + "\" must be a number. Using " + defaultValue + ".");
        }
        return defaultValue;
    }

    // Reads text. If it is missing, the default is used. 
    private static String text(Map<String, String> settings, String name, String defaultValue) {
        String text = settings.remove(name);
        return (text == null) ? defaultValue : text.trim();
    }

    // The default file

    public static String defaultJson() {
        return """
            {
              "waterDensity": 998.0,

              "activeGates": "0,1",
              "sphereCentroidHeight": 0.0,
              "testObjectCentroidHeight": 0.0,

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