package org.windflume.calibration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrialStats {
    private final List<Double> velocities = new ArrayList<>();
    private final List<Double> dragCoeffiecient = new ArrayList<>();
    private final List<Double> sphereForces = new ArrayList<>();
    private final List<Double> testObjectForces = new ArrayList<>();

    private double lastVelocity = Double.NaN;
    private int seconds = 0;

    public void reset(){
        velocities.clear();
        dragCoeffiecient.clear();
        sphereForces.clear();
        testObjectForces.clear();
        lastVelocity = Double.NaN;
        seconds = 0;
    }

    public void add(TrialResult result){
        seconds++;
        lastVelocity = result.getWaterVelocity();

        if (result.getWaterVelocity() >= 0){
            velocities.add(result.getWaterVelocity());
        }
        if (result.getTestObjectDragCoefficient() >= 0){
            dragCoeffiecient.add(result.getTestObjectDragCoefficient());
        } 
        sphereForces.add(result.getSphereForce());
        testObjectForces.add(result.getTestObjectForce());
    }

    public double getCurrentVelocity()   { return lastVelocity; }
    public double getMaxVelocity()          { return max(velocities); }
    public double getMinVelocity()       { return min(velocities); }
    public double getMedianCd()          { return median(dragCoeffiecient); }
    public double getMaxSphereForce()     { return max(sphereForces); }
    public double getMaxTestObjectForce() { return max(testObjectForces); }
    public int getDurationSeconds()      {return seconds; }

    private static double max(List<Double> list){
        return list.stream().mapToDouble(Double::doubleValue).max().orElse(Double.NaN);
    }

    private static double min(List<Double> list){
        return list.stream().mapToDouble(Double::doubleValue).min().orElse(Double.NaN);
    }

    private static double median(List<Double> list){
        if (list.isEmpty()) return Double.NaN;
        List<Double> sorted = new ArrayList<>();
        Collections.sort(sorted);
        int n = sorted.size();
        return (n % 2 == 1)
            ? sorted.get(n/2)
            : (sorted.get(n/2 - 1) + sorted.get(n/2)) / 2.0;
        
    }
    
}
