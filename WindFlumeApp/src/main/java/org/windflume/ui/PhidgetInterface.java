package org.windflume.ui;

import java.awt.Color;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.windflume.phidget.PhidgetGate;

public class PhidgetInterface extends JPanel {

    public double curVoltageValue = 0;

    private final DecimalFormat df =
            new DecimalFormat("0.0000");

    public JLabel curVoltageText;

    public PhidgetGate connectedBridge;

    public PhidgetInterface(
            PhidgetGate gate,
            int height,
            int width) {

        connectedBridge = gate;

        System.out.println(height);

        setSize(width, height);

        Border lineBorder =
                BorderFactory.createLineBorder(Color.BLACK);

        setBorder(
                BorderFactory.createTitledBorder(
                        lineBorder,
                        "Bridge " + gate.getChannel() + " Interface"
                )
        );

        curVoltageText = new JLabel();

        add(curVoltageText);
    }

    public int getChannelNum() {

        return connectedBridge.getChannel();
    }

    public void updateText() {

        updateVoltage();
    }

    public double getMedianVoltageValue() {

        return connectedBridge.getMedianVoltageValue();
    }

    public void updateVoltage() {

        curVoltageValue =
                connectedBridge.getMedianVoltageValue();

        connectedBridge.resetValues();

        curVoltageText.setText(
                "current voltage:" +
                df.format(curVoltageValue)
        );
    }

    public void closeBridge() {

        connectedBridge.close();
    }
}