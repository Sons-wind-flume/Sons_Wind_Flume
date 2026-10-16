package org.windflume;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.windflume.config.ConfigLoader;
import org.windflume.config.Configuration;

public class ConfigLoaderTest {

    @Test
    void defaultFileTextLoadsWithNoWarnings() {
        List<String> warnings = new ArrayList<>();
        Configuration c = ConfigLoader.fromJson(ConfigLoader.defaultJson(), warnings);

        assertTrue(warnings.isEmpty());
        assertEquals(998.0, c.getWaterDensity(), 1e-9);
        assertEquals(49160.0, c.getSlope(0), 1e-9);
        assertEquals(-3.7, c.getOffset(1), 1e-9);
        assertEquals(0.0022, c.getTestObject("Sphere").getProjectedArea(), 1e-9);
        assertEquals(0.5, c.getDragCoefficient(), 1e-9);
        assertEquals(0.003, c.getTestObject("Test Object").getProjectedArea(), 1e-9);
        assertTrue(c.getTestObject("Sphere").isReference());
    }

    @Test
    void customValuesAreApplied() {
        String json = "{ \"waterDensity\": 1000,\n"
                + " \"slope0\": 50000, \"offset0\": -1.5,\n"
                + " \"sphereProjectedArea\": 0.0025, \"sphereDragCoefficient\": 0.47, \"sphereModelId\": \"S1\",\n"
                + " \"testObjectProjectedArea\": 0.004, \"testObjectOrientation\": \"Face on\","
                + " \"testObjectDragCoefficient\": 1.05 }";
        List<String> warnings = new ArrayList<>();
        Configuration c = ConfigLoader.fromJson(json, warnings);

        assertTrue(warnings.isEmpty());
        assertEquals(1000.0, c.getWaterDensity(), 1e-9);
        assertEquals(50000.0, c.getSlope(0), 1e-9);
        assertEquals(-1.5, c.getOffset(0), 1e-9);
        assertEquals(47230.0, c.getSlope(1), 1e-9); // not in the file, so default
        assertEquals(0.0025, c.getTestObject("Sphere").getProjectedArea(), 1e-9);
        assertEquals(0.47, c.getDragCoefficient(), 1e-9);
        assertEquals("S1", c.getTestObject("Sphere").getModelId());
        assertTrue(c.getTestObject("Sphere").isReference());
        assertEquals(0.004, c.getTestObject("Test Object").getProjectedArea(), 1e-9);
        assertEquals("Face on", c.getTestObject("Test Object").getOrientation());
        assertEquals(1.05, c.getTestObject("Test Object").getKnownDragCoefficient(), 1e-9);
    }

    @Test
    void invalidValuesFallBackToDefaultsWithWarnings() {
        String json = "{ \"waterDensity\": -5, \"slope0\": 0, \"sphereProjectedArea\": \"big\","
                + " \"sphereDragCoefficient\": -1, \"testObjectProjectedArea\": 0 }";
        List<String> warnings = new ArrayList<>();
        Configuration c = ConfigLoader.fromJson(json, warnings);

        assertEquals(5, warnings.size());
        assertEquals(998.0, c.getWaterDensity(), 1e-9);
        assertEquals(49160.0, c.getSlope(0), 1e-9);
        assertEquals(0.0022, c.getTestObject("Sphere").getProjectedArea(), 1e-9);
        assertEquals(0.5, c.getDragCoefficient(), 1e-9);
        assertEquals(0.003, c.getTestObject("Test Object").getProjectedArea(), 1e-9);
    }

    @Test
    void unknownSettingGivesAWarning() {
        List<String> warnings = new ArrayList<>();
        ConfigLoader.fromJson("{ \"sphereArea\": 0.01 }", warnings);

        assertEquals(1, warnings.size());
        assertTrue(warnings.get(0).contains("sphereArea"));
    }

    @Test
    void textThatIsNotJsonUsesDefaultsWithOneWarning() {
        List<String> warnings = new ArrayList<>();
        Configuration c = ConfigLoader.fromJson("hello", warnings);

        assertEquals(1, warnings.size());
        assertEquals(998.0, c.getWaterDensity(), 1e-9);
    }

    @Test
    void missingFileIsCreatedWithDefaults() throws IOException {
        Path file = Files.createTempDirectory("windflume-test").resolve("sub").resolve("config.json");

        ConfigLoader.Result first = ConfigLoader.load(file);
        assertTrue(first.warnings.isEmpty());
        assertTrue(Files.exists(file));

        ConfigLoader.Result second = ConfigLoader.load(file); // now reads the file it created
        assertTrue(second.warnings.isEmpty());
        assertEquals(0.0022, second.config.getTestObject("Sphere").getProjectedArea(), 1e-9);
    }
}