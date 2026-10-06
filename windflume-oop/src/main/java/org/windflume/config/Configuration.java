package org.windflume.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Configuration {

    private double waterDensity = 998.0;
    private double dragCoefficient = 0.5;

    private final List<TestObject> testObjects = new ArrayList<>();
    private final Map<Integer, Double> slopes = new HashMap<>();
    private final Map<Integer, Double> offsets = new HashMap<>();

    private boolean usingDefaults = false;

    public static Configuration defaults() {
        Configuration c = new Configuration();
        c.addTestObject(new TestObject("Sphere", 0.0022));
        c.addTestObject(new TestObject("Test Object", 0.0003));
        c.setChannelCalibration(0, 49160.0, -4.2);
        c.setChannelCalibration(1, 47230.0, -3.7);
        c.usingDefaults = true;
        return c;
    }

    public void addTestObject(TestObject obj) {
        testObjects.add(obj);
    }

    public TestObject getTestObject(String name) {
        for (TestObject t : testObjects) {
            if (t.getName().equalsIgnoreCase(name)) return t;
        }
        return null;
    }

    public List<TestObject> getTestObjects() {
        return testObjects;
    }

    public void setChannelCalibration(int channel, double slope, double offset) {
        slopes.put(channel, slope);
        offsets.put(channel, offset);
    }

    public double getSlope(int channel) {
        return slopes.getOrDefault(channel, 0.0);
    }

    public double getOffset(int channel) {
        return offsets.getOrDefault(channel, 0.0);
    }

    public double getWaterDensity() {
        return waterDensity;
    }

    public void setWaterDensity(double value) {
        waterDensity = value;
    }

    public double getDragCoefficient() {
        return dragCoefficient;
    }

    public void setDragCoefficient(double value) {
        dragCoefficient = value;
    }

    public boolean isUsingDefaults() {
        return usingDefaults;
    }
}
