package org.jfree.starter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class GaugeBlockPanel extends JPanel{

    private XYSeries series;
    private double x = 0;
    private boolean isRunning = false;
    JComboBox<String> typeDropdown = new JComboBox<>(new String[]{
        "Sphere", "Test Object"
    });




    Color white = new Color(207,207,207);
    static boolean recording= false;

    public GaugeBlockPanel(String title){
        setBackground(white);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.GRAY));

        XYDataset dataset = createDataset();

        JFreeChart chart = ChartFactory.createXYLineChart(  
                null,
                "Time",
                "Value",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false
       );

       JLabel chartLabel = new JLabel("Live Chart");

       

       JPanel configPanel = new JPanel(new GridLayout(2, 3));
       configPanel.add(new JLabel("Attachment area"));
       configPanel.add(new JLabel("Model"));
       configPanel.add(new JLabel("Projected area"));
       configPanel.add(new JLabel("Orientation"));
       configPanel.add(new JLabel("Experimenter"));
       configPanel.add(new JLabel("Drag coefficient"));

       JPanel leftSidePanel = new JPanel(new BorderLayout());
       leftSidePanel.add(typeDropdown, BorderLayout.NORTH);
       leftSidePanel.add(configPanel, BorderLayout.CENTER);
       this.add(leftSidePanel, BorderLayout.CENTER);

       ChartPanel chartPanel = new ChartPanel(chart);
       chartPanel.setPreferredSize(new Dimension(200, 100));

       JPanel chartGroup = new JPanel();
       chartGroup.setLayout(new BoxLayout(chartGroup, BoxLayout.Y_AXIS));
       chartGroup.add(chartLabel);
       chartGroup.add(chartPanel);

       JPanel rightSidePanel = new JPanel();
       rightSidePanel.setLayout(new GridBagLayout());
       rightSidePanel.add(chartGroup);
       this.add(rightSidePanel, BorderLayout.EAST);


        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    if (isRunning){
                    x += 1;
                    double randomY = Math.random() * 100;
                    series.add(x, randomY);
                    }
                }
            });
             timer.start();
    }

    private XYDataset createDataset(){
        series = new XYSeries("Live values");
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        return dataset;
    }

    public void setRunning(boolean running){
        isRunning = running;
    }

}
    

