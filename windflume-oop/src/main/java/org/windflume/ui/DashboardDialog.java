package org.windflume.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class DashboardDialog extends JDialog {
    private final JLabel currentVelocity = new JLabel("-- m/s");
    private final JLabel maxVelocity = new JLabel("-- m/s");
    private final JLabel minVelocity = new JLabel("-- m/s");
    private final JLabel duration = new JLabel("-- s");
    private final JLabel medianCd = new JLabel("--");
    private final JLabel maxForce = new JLabel("-- N");

    private final GaugeBlockPanel velocityChart = new GaugeBlockPanel("Water velocity", "Time (s)", "Velocity (m/s)");

    private long lastPlottedSecond = -1;

    public DashboardDialog(Window owner, String title){
        super(owner, title, ModalityType.MODELESS);

        JPanel stats = new JPanel(new GridLayout(2, 3, 10, 10));
        stats.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        stats.setPreferredSize(new Dimension(600, 160));

        stats.add(statBox("Current Velocity", currentVelocity));
        stats.add(statBox("Max velocity", maxVelocity));
        stats.add(statBox("Min velocity", minVelocity));
        stats.add(statBox("Duration", duration));
        stats.add(statBox("Median Cd", medianCd));
        stats.add(statBox("Max Force", maxForce));

        velocityChart.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));


        add(stats, BorderLayout.NORTH);
        add(velocityChart, BorderLayout.CENTER);

        setSize(700, 520);
        setLocationRelativeTo(owner);     
    }
    
    private JPanel statBox(String name, JLabel value){
        JPanel box = new JPanel(new GridLayout(2, 1));
        box.setBorder(BorderFactory.createTitledBorder(name));
        value.setHorizontalAlignment(SwingConstants.CENTER);
        box.add(value);
        return box;
    }

    public void update(double velocityNow, double velocityMax, double velocityMin, long durationSeconds, double cdMedian, double forceMax){
        currentVelocity.setText(fmt(velocityNow, "%.3f m/s"));
        maxVelocity.setText(fmt(velocityMax, "%.3f m/s"));
        minVelocity.setText(fmt(velocityMin, "%.3f m/s"));
        duration.setText(durationSeconds + " s");
        medianCd.setText(fmt(cdMedian, "%.3f m/s"));

        maxForce.setText(Double.isNaN(forceMax) ? "--" : String.format("%.2f N", forceMax));

        boolean validVelocity = !Double.isNaN(velocityNow) && velocityNow >= 0;
        if (validVelocity && durationSeconds != lastPlottedSecond){
            velocityChart.addPoint(durationSeconds, velocityNow);
            lastPlottedSecond = durationSeconds;
        }
    }

    public void clearChart(){
        velocityChart.clearGraph();;
        lastPlottedSecond = -1;
    }

    private String fmt(double value, String pattern){
        return (Double.isNaN(value) || value < 0) ? "--" : String.format(pattern, value);
    }   
}
