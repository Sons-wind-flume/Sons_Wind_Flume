package org.windflume.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.windflume.phidget.PhidgetGate;

public class PhidgetInterface extends JPanel {

    public double curVoltageValue = 0;

    private final DecimalFormat df = new DecimalFormat("0.0000");

    public JLabel curVoltageText;


    public PhidgetGate connectedBridge;
    public final JComboBox<GaugeRole> roleBox;
    private final GaugeBlockPanel chart;
    private final JLabel statusLabel = new JLabel();
    private DashboardDialog dashboard;

    public PhidgetInterface( PhidgetGate gate, int height, int width) {

        connectedBridge = gate;

        setLayout(new BorderLayout(0 , 8));
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK), 
        BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JLabel title = new JLabel("Bridge " + gate.getChannel());
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));

        roleBox = new JComboBox<>(GaugeRole.values());
        roleBox.setSelectedItem(gate.getChannel() == 0 ? GaugeRole.SPHERE : GaugeRole.TEST_OBJECT);

        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        rolePanel.setOpaque(false);
        rolePanel.add(new JLabel("Assigned as:"));
        rolePanel.add(roleBox);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        
        updateStatus();

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setOpaque(false);
        titlePanel.add(title);
        titlePanel.add(statusLabel);

        top.add(titlePanel, BorderLayout.WEST);

        top.add(rolePanel, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        //middle: live chart
        chart = new GaugeBlockPanel("Bridge " + gate.getChannel());
        add(chart, BorderLayout.CENTER);

        //bottom row: current reading + dashboard button
        curVoltageText = new JLabel();
        
        chart.setOnClick(this::openDashboard);

        JLabel hint = new JLabel("Click the chart for more details");
        hint.setFont(hint.getFont().deriveFont(Font.ITALIC, 11f));
        hint.setForeground(Color.DARK_GRAY);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(curVoltageText, BorderLayout.WEST);
        bottom.add(hint, BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);
}

    public GaugeRole getRole() {
        return (GaugeRole) roleBox.getSelectedItem();
    }

    public int getChannelNum() {
        return connectedBridge.getChannel();
    }

    public DashboardDialog getDashboard(){
        return dashboard;
    }

    public void updateText() {
        updateVoltage();
    }

    public double getMedianVoltageValue() {
        return connectedBridge.getMedianVoltageValue();
    }

    public void updateVoltage() {
        curVoltageValue = connectedBridge.getMedianVoltageValue();
        chart.addVoltage(curVoltageValue);

        connectedBridge.resetValues();

        curVoltageText.setText("current voltage: " + df.format(curVoltageValue));
    }

    public void updateStatus(){
        if (connectedBridge.isConnected()){
            statusLabel.setText("Sensor connected");
            statusLabel.setForeground(new Color(40, 140, 60));
        } else {
            statusLabel.setText("Offline: check cable");
            statusLabel.setForeground(new Color(200, 100, 0));
        }
    }

    private void openDashboard(){
        if (dashboard == null){
            dashboard = new DashboardDialog(SwingUtilities.getWindowAncestor(this),
        "Bridge "+ getChannelNum() + " Dashboard");
        }
        dashboard.setVisible(true);
        dashboard.toFront();
    }
    
    public void clearChart(){
        chart.clearGraph();
        if (dashboard != null){
            dashboard.clearChart();
        }
    }

    public void closeBridge() {
        connectedBridge.close();
    }
}