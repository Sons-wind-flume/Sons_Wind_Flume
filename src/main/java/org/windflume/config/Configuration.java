package org.windflume.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.windflume.phidget.PhidgetGate;

public class Configuration {
    public static final String SPHERE = "Sphere";
    public static  String TEST_OBJECT = "Test Object";

    private double waterDensity = 998.0;

    private final List<TestObject> testObjects = new ArrayList<>();
     //private final List<PhidgetGate> activeGates = new ArrayList<>();
    private final Map<Integer, PhidgetGate> activeGates = new HashMap<>();
    private final Map<Integer, Double> slopes = new HashMap<>();
    private final Map<Integer, Double> offsets = new HashMap<>();

    private boolean usingDefaults = false;

    public Configuration(){
        TestObject sphere = new TestObject(SPHERE, 0.0022);
        sphere.setReference(true);
        sphere.setKnownDragCoefficient(0.5);  
        testObjects.add(sphere);
        testObjects.add(new TestObject(TEST_OBJECT, 0.003));
    }

    public static Configuration defaults() {
        Configuration c = new Configuration();
        c.setChannelCalibration(0, 49160.0, -4.2);
        c.setChannelCalibration(1, 47230.0, -3.7);
        c.usingDefaults = true;
        return c;
    }

    public void addGate(PhidgetGate gate){
        activeGates.put(gate.getChannel(),gate);
        setChannelCalibration(gate.getChannel(), 0, 0);
    }

    public boolean isActiveGate(int channel){
        return activeGates.get(channel) !=null;
    }
    public int activeGateCount(){
        return activeGates.size();
    }

    public ArrayList<PhidgetGate> getActiveGates() {
        return new ArrayList<PhidgetGate>(activeGates.values());
    }

    public PhidgetGate getActiveGate(int channel) {
        return activeGates.get(channel);
    }


    public void addTestObject(TestObject obj) {
        for ( int i=0; i < testObjects.size(); i++){
            if (testObjects.get(i).getName().equalsIgnoreCase(obj.getName())){
                obj.setReference(testObjects.get(i).isReference());
                testObjects.set(i, obj);
                return;
            }
        }
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
        return getTestObject(SPHERE).getKnownDragCoefficient();
    }

    public void setDragCoefficient(double value) {
        getTestObject(SPHERE).setKnownDragCoefficient(value);
    }

    public boolean isUsingDefaults() {
        return usingDefaults;
    }
}
