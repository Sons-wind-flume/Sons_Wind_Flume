package org.windflume.ui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class GaugeBlockPanel extends JPanel {

    private final XYSeries series;
    private final ChartPanel chartPanel;
    private double x = 0;

    public GaugeBlockPanel(String title) {
        this(title, "Time (s)", "Voltage");
    }

    public GaugeBlockPanel(String title, String xLabel, String yLabel){

        setLayout(new BorderLayout());

       series = new XYSeries(title);

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);     

        JFreeChart chart = ChartFactory.createXYLineChart(
                        title,
                        xLabel,
                        yLabel,
                        dataset
                );
                chart.removeLegend();

                chartPanel = new ChartPanel(chart);
                chartPanel.setMouseZoomable(false);
                chartPanel.setPopupMenu(null);
                chartPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                add(chartPanel, BorderLayout.CENTER);
    }

    public void setOnClick(Runnable action){
        chartPanel.addMouseListener(new MouseAdapter() {
            @Override 
            public void mouseClicked(MouseEvent e){
                action.run();
            }
        });
    }

    public void addVoltage (double voltage){
            series.add(x, voltage);
            x++;

        if (series.getItemCount() > 100) {
            series.remove(0);
        }
    }

    public void addPoint(double xValue, double yValue){
        series.add(xValue, yValue);

        if (series.getItemCount() > 100){
            series.remove(0);
        }
    }

    

    public void clearGraph() {

        series.clear();
        x = 0;
    }
}