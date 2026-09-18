package org.jfree.starter;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;

public class XYSeriesExample extends JFrame {

    // Class fields — declared here so BOTH the constructor and the Timer can see them
    private XYSeries series;
    private double x = 0;

    public XYSeriesExample(String title) {
        super(title);

        XYDataset dataset = createDataset();

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Live Random Chart",
                "Time",
                "Value",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        ChartPanel chartPanel = new ChartPanel(chart);
        this.add(chartPanel, BorderLayout.CENTER);

        this.setSize(800, 600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Timer created AFTER dataset/series exist
        Timer timer = new Timer(1000, e -> {
            x = x + 1;
            double randomY = Math.random() * 100;
            series.add(x, randomY);
        });
        timer.start();
    }

    private XYDataset createDataset() {
        series = new XYSeries("Live values"); // assigns the FIELD, not a local variable
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        return dataset;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            XYSeriesExample example = new XYSeriesExample("Live JFreeChart Demo");
            example.setVisible(true);
        });
    }
}