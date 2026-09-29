package org.windflume.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.windflume.calibration.CalibrationController;
import org.windflume.calibration.TrialResult;
import org.windflume.phidget.PhidgetGate;
import org.windflume.recording.CSVFileWriter;

import com.phidget22.PhidgetException;

public class Canvas extends JFrame {

    Color white = new Color(207, 207, 207);

    static boolean recording = false;

    private ArrayList<PhidgetGate> bridgeList;
    private InterfaceGrid grid;
    private GaugeBlockPanel graph;
    private CalibrationController calibration;
    private JLabel velocityLabel;
    private JLabel dragCoefficientLabel;

    public Canvas() {

        setTitle("Sons wind flume interface");
        setResizable(false);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        setUpWindowCloser();

        // Main content panel

        JPanel mainPanel = new JPanel(null);
        mainPanel.setPreferredSize(new Dimension(800, 800));
        mainPanel.setBackground(white);

        // Main interface area

        bridgeList = new ArrayList<>();

        try {
            bridgeList.add(new PhidgetGate(0));
        } catch (PhidgetException e) {
             System.out.println("Bridge 0 hardware not connected.");
             bridgeList.add(new PhidgetGate(0, true));
            }
            
        try {
            bridgeList.add(new PhidgetGate(1));
        } catch (PhidgetException e) {
            System.out.println("Bridge 1 hardware not connected.");
            bridgeList.add(new PhidgetGate(1, true));
        }

        grid = new InterfaceGrid(
                800,
                600,
                bridgeList
        );

        grid.setLocation(0, 0);

        mainPanel.add(grid);
        
       graph = new GaugeBlockPanel();
       graph.setLocation(250, 600);
       graph.setSize(300, 100);
       mainPanel.add(graph);

       velocityLabel = new JLabel("Water velocity: -- m/s");
       velocityLabel.setLocation(250, 705);
       velocityLabel.setSize(300, 25);
       mainPanel.add(velocityLabel);

       dragCoefficientLabel = new JLabel("Test object Cd: --");
       dragCoefficientLabel.setLocation(250, 730);
       dragCoefficientLabel.setSize(300, 25);
       mainPanel.add(dragCoefficientLabel);

       calibration = new CalibrationController(
         998.0,
        0.5,
        0.0022,
        0.0003
       );

        // Update interface every second

     Timer timer = new Timer(1000, e -> {
        grid.updateinterfaces();

        double channel0Voltage = grid.getVoltage(0);
        double channel1Voltage = grid.getVoltage(1);
        
        graph.addVoltage( channel0Voltage, channel1Voltage);
        
        if (bridgeList.size() >= 2) {

        TrialResult result = calibration.calculate( channel0Voltage, channel1Voltage );

        double waterVelocity = result.getWaterVelocity();

        double dragCoefficient = result.getTestObjectDragCoefficient();

        // Display water velocity
        if (waterVelocity >= 0) {
            velocityLabel.setText(
                "Water velocity: "
                + String.format(
                    "%.3f",
                    waterVelocity
                )
                + " m/s"
            );
        } else {
            velocityLabel.setText(
                "Water velocity: -- m/s"
            );
        }

        // Display test object Cd
        if (dragCoefficient >= 0) {
            dragCoefficientLabel.setText(
                "Test object Cd: "
                + String.format(
                    "%.3f",
                    dragCoefficient
                )
            );
        } else {
            dragCoefficientLabel.setText(
                "Test object Cd: --"
            );
        }
    }
    });

       timer.start();

        // Export button

        JButton export = new JButton("Export");

        export.setLocation(100, 650);
        export.setSize(120, 70);

        export.addActionListener(e -> export());

        mainPanel.add(export);

        // Record button

        JButton record = new JButton("Record");

        record.setLocation(580, 650);
        record.setSize(120, 70);

        record.addActionListener(e -> startStopRecording());

        mainPanel.add(record);

        // Add content panel

        setContentPane(mainPanel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

        // Export

    public void export() {
       String data = grid.toString();

       CSVFileWriter writer = new CSVFileWriter();
       writer.Export(data);
}

    // Start / stop recording

    public void startStopRecording() {

        if (recording) {
            recording = false;
            System.out.println("Recording stopped");
        } else {
            recording = true;
            System.out.println("Recording started");
            grid.resetRecord();
        }
    }

    // Window closing

    public void setUpWindowCloser() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {

                if (bridgeList != null) {

                    for (PhidgetGate gate : bridgeList) {
                        gate.close();
                    }
                }

                System.exit(0);
            }
        });
    }
}
