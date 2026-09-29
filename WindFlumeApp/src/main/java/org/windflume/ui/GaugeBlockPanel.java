package org.windflume.ui;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class GaugeBlockPanel extends JPanel {

    private final XYSeries channel0Series;
    private final XYSeries channel1Series;

    private double x = 0;

    public GaugeBlockPanel() {

        setLayout(new BorderLayout());

        channel0Series = new XYSeries("Channel 0");
        channel1Series = new XYSeries("Channel 1");

        XYSeriesCollection dataset =
                new XYSeriesCollection();

        dataset.addSeries(channel0Series);
        dataset.addSeries(channel1Series);

        JFreeChart chart =
                ChartFactory.createXYLineChart(
                        "Live Voltage",
                        "Time",
                        "Voltage",
                        dataset
                );

        ChartPanel chartPanel =
                new ChartPanel(chart);

        add(chartPanel, BorderLayout.CENTER);
    }

    public void addVoltage(
            double channel0Voltage,
            double channel1Voltage) {

        channel0Series.add(x, channel0Voltage);
        channel1Series.add(x, channel1Voltage);

        x++;

        if (channel0Series.getItemCount() > 100) {
            channel0Series.remove(0);
        }

        if (channel1Series.getItemCount() > 100) {
            channel1Series.remove(0);
        }
    }

    public void clearGraph() {

        channel0Series.clear();
        channel1Series.clear();

        x = 0;
    }
}