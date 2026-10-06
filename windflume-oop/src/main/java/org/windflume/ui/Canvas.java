package org.windflume.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import org.windflume.calibration.CalibrationController;
import org.windflume.calibration.TrialResult;
import org.windflume.calibration.TrialStats;
import org.windflume.phidget.PhidgetGate;
import org.windflume.recording.CSVFileWriter;

import com.phidget22.PhidgetException;

public class Canvas extends JFrame {

    Color white = new Color(207, 207, 207);

    static boolean recording = false;

    private ArrayList<PhidgetGate> bridgeList;
    private InterfaceGrid grid;
    private JButton startStopButton;
    private JButton exportButton;
    private CalibrationController calibration;
    private JLabel velocityLabel;
    private JLabel dragCoefficientLabel;
    private final TrialStats stats = new TrialStats();

    public Canvas() {

        setTitle("Sons wind flume interface");
        setResizable(false);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        setUpWindowCloser();

        // ---- Main panel ----
        JPanel mainPanel = new JPanel(new BorderLayout(0, 12));
        mainPanel.setPreferredSize(new Dimension(1000, 650));
        mainPanel.setBackground(white);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // ---- Bridges ----
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

        // ---- Centre: the two gauge cards ----
        grid = new InterfaceGrid(970, 480, bridgeList);
        mainPanel.add(grid, BorderLayout.CENTER);

        // ---- Results labels ----
        velocityLabel = new JLabel("Water velocity: -- m/s", SwingConstants.CENTER);
        velocityLabel.setFont(velocityLabel.getFont().deriveFont(Font.BOLD, 16f));

        dragCoefficientLabel = new JLabel("Test object Cd: --", SwingConstants.CENTER);
        dragCoefficientLabel.setFont(dragCoefficientLabel.getFont().deriveFont(Font.BOLD, 16f));

        // ---- Buttons ----
        exportButton = new JButton("Export");
        exportButton.setPreferredSize(new Dimension(140, 60));
        exportButton.setVisible(false);
        exportButton.setBackground(new Color(255, 215, 0));
        exportButton.setOpaque(true);
        exportButton.setBorderPainted(false);
        exportButton.addActionListener(e -> export());

        startStopButton = new JButton("START");
        startStopButton.setPreferredSize(new Dimension(140, 60));
        startStopButton.setFont(startStopButton.getFont().deriveFont(Font.BOLD, 16f));
        startStopButton.addActionListener(e -> toggleTrial());

        // ---- Bottom bar: [Export]  [results]  [START/STOP] ----
        JPanel leftBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftBox.setOpaque(false);
        leftBox.add(exportButton);

        JPanel resultsBox = new JPanel(new GridLayout(2, 1));
        resultsBox.setOpaque(false);
        resultsBox.add(velocityLabel);
        resultsBox.add(dragCoefficientLabel);

        JPanel rightBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightBox.setOpaque(false);
        rightBox.add(startStopButton);

        JPanel bottomBar = new JPanel(new GridLayout(1, 3, 15, 0));
        bottomBar.setOpaque(false);
        bottomBar.add(leftBox);
        bottomBar.add(resultsBox);
        bottomBar.add(rightBox);

        mainPanel.add(bottomBar, BorderLayout.SOUTH);

        // ---- Calibration ----
        calibration = new CalibrationController(
                998.0,
                0.5,
                0.0022,
                0.0003
        );

        // ---- Update interface every second ----
        Timer timer = new Timer(1000, e -> {
            grid.updateinterfaces();

            if (bridgeList.size() >= 2) {

                PhidgetInterface sphere = grid.getInterfaceByRole(GaugeRole.SPHERE);
                PhidgetInterface testObj = grid.getInterfaceByRole(GaugeRole.TEST_OBJECT);

                TrialResult result = calibration.calculate(
                        sphere.getChannelNum(), sphere.curVoltageValue,
                        testObj.getChannelNum(), testObj.curVoltageValue);
                
                if (recording) {
                    stats.add(result);
                }

                updateDashboard(sphere, stats.getMaxSphereForce(), Double.NaN);
                updateDashboard(testObj, stats.getMaxTestObjectForce(), stats.getMedianCd());

                double waterVelocity = result.getWaterVelocity();
                double dragCoefficient = result.getTestObjectDragCoefficient();

                if (waterVelocity >= 0) {
                    velocityLabel.setText("Water velocity: "
                            + String.format("%.3f", waterVelocity) + " m/s");
                } else {
                    velocityLabel.setText("Water velocity: -- m/s");
                }

                if (dragCoefficient >= 0) {
                    dragCoefficientLabel.setText("Test object Cd: "
                            + String.format("%.3f", dragCoefficient));
                } else {
                    dragCoefficientLabel.setText("Test object Cd: --");
                }
            }
        });

        timer.start();

        setContentPane(mainPanel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Export

    public void export() {
        if (!grid.hasRecording()){
            return;
        }

        String data = grid.toString();

        CSVFileWriter writer = new CSVFileWriter();
        boolean saved = writer.Export(data);

        if (saved){
            JOptionPane.showMessageDialog(this, "Your data was saved to recording.csv", "Export complete", JOptionPane.INFORMATION_MESSAGE);
        } else {
             JOptionPane.showMessageDialog(this,
                "Could not save recording.csv.\nIf the file is open in another program, close it and try again.", "Export failed", 
                JOptionPane.ERROR_MESSAGE);
        }
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

    private void toggleTrial() {
        if (!recording) {
            // START
            grid.clearCharts();
            exportButton.setVisible(false);
            startStopRecording();
            startStopButton.setText("STOP");
            stats.reset();
        } else {
            // STOP
            startStopRecording();
            startStopButton.setText("START");
            exportButton.setVisible(grid.hasRecording());
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

    private void updateDashboard(PhidgetInterface gauge, double maxForce, double cd) {
        DashboardDialog d = gauge.getDashboard();
        if (d != null && d.isVisible()) {
            d.update(
                    stats.getCurrentVelocity(),
                    stats.getMaxVelocity(),
                    stats.getMinVelocity(),
                    stats.getDurationSeconds(),
                    cd,
                    maxForce
            );
        }
    }
}