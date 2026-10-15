package org.windflume.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JPanel;

import org.windflume.config.Configuration;
import org.windflume.phidget.PhidgetGate;

public class InterfaceGrid extends JPanel {

    private final ArrayList<PhidgetInterface> interfaceList;
    private ArrayList<org.windflume.recording.RecordEntry> recordingList;
    private final Configuration config;

    public InterfaceGrid(int width, int height, ArrayList<PhidgetGate> list,
                         Configuration config) {

        this.config = config;

        setLayout(new GridLayout(0, 1, 0, 12));     // gauge cards stacked, as in the prototype
        setSize(width, height);
        setPreferredSize(new Dimension(width, height));
        setOpaque(false);

        interfaceList = new ArrayList<>();

        createInterfaces(list);

        recordingList = new ArrayList<>();
    }

    public void createInterfaces(ArrayList<PhidgetGate> list) {

        list.sort((u1, u2) -> Integer.compare(u1.getChannel(), u2.getChannel()));

        for (int i = 0; i < list.size(); i++) {
            createInterface(list.get(i),
                    (getHeight() / list.size()) - 1,
                    i * (getHeight() / list.size()));
        }
    }

    public void createInterface(PhidgetGate gate, int height, int y) {

        PhidgetInterface bridgeInterface =
                new PhidgetInterface(gate, height, getWidth(), config);

        bridgeInterface.setBackground(new Color(212, 203, 207));

        add(bridgeInterface);

        interfaceList.add(bridgeInterface);

        // When a student creates a "Custom object", add it to the config
        // and to every gauge's dropdown, and select it on this gauge
        bridgeInterface.setOnCustomCreated(obj -> {
            config.addTestObject(obj);
            for (PhidgetInterface e : interfaceList) {
                e.addObjectOption(obj);
            }
            bridgeInterface.selectObject(obj);
        });
    }

    // The gauge whose attached object is the reference (the Sphere)
    public PhidgetInterface getReferenceGauge() {
        for (PhidgetInterface e : interfaceList) {
            if (e.getSelectedObject().isReference()) return e;
        }
        return null;
    }

    public PhidgetInterface getOtherGauge(PhidgetInterface gauge) {
        for (PhidgetInterface e : interfaceList) {
            if (e != gauge) return e;
        }
        return null;
    }

    public boolean hasRecording() {
        return !recordingList.isEmpty();
    }

    public void resetRecord() {
        recordingList = new ArrayList<>();
    }

    public void updateinterfaces() {

        ArrayList<org.windflume.recording.VoltageValue> values = new ArrayList<>();

        for (PhidgetInterface e : interfaceList) {

            e.updateText();

            if (Canvas.recording) {
                values.add(new org.windflume.recording.VoltageValue(e.curVoltageValue,e.getChannelNum(),config,e.getSelectedObject()));
                //System.out.println(e.getSelectedObject().toString());
                //System.out.println(e.getSelectedObject().isReference());
            }
        }

        if (Canvas.recording) {

            recordingList.add(new org.windflume.recording.RecordEntry(
                    recordingList.size(), values,config));

            System.out.println("Recorded row: " + recordingList.size());
        }
    }

    public void clearCharts() {
        for (PhidgetInterface e : interfaceList) {
            e.clearChart();
        }
    }

    public void closeBridges() {
        for (PhidgetInterface e : interfaceList) {
            e.closeBridge();
        }
    }

    @Override
    public String toString() {

        String text = toStringHeader() + "\n";
        for (int i = 0; i < recordingList.size(); i++) {
            org.windflume.recording.RecordEntry entry = recordingList.get(i);
            text += entry.toString(i+1) + "\n";
        }

        return text;
    }

    public String toStringHeader() {

        String text = "Time Stamps ,";

        for (int i = 0; i < interfaceList.size(); i++) {
            PhidgetInterface curInterface = interfaceList.get(i);
            text += " Channel Voltage[" + curInterface.getChannelNum() + "]";
            if(curInterface.getSelectedObject().isReference()){
                text +="(sphere)";
            }
            else{
                text+="(test object)";
            }
            text += " ,";
        }
        for (int i = 0; i < interfaceList.size(); i++) {
            PhidgetInterface curInterface = interfaceList.get(i);
            text += " Channel force[" + curInterface.getChannelNum() + "]";
            if(curInterface.getSelectedObject().isReference()){
                text +="(sphere)";
            }
            else{
                text+="(test object)";
            }
            text += " ,";
        }
        text += " projected area (m2) , attachment area (m2) , centroid height (m), Model force / attachment area (N/m2),Water Velocity (m/s), Model drag coefficient , Model bending moment (N/m)";
        return text;
    }

    public double getVoltage(int index) {

        if (index < 0 || index >= interfaceList.size()) {
            return 0.0;
        }

        return interfaceList.get(index).curVoltageValue;
    }
}