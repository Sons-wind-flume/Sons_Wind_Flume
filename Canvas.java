package org.jfree.starter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Canvas extends JFrame {
    GaugeBlockPanel gauge1 = new GaugeBlockPanel("Live Dashboard 1");
    GaugeBlockPanel gauge2 = new GaugeBlockPanel("Live Dashboard 2");
     private boolean isRunning = false;

    Color white = new Color(207,207,207);
    static boolean recording= false;

    public Canvas(String title){
        setResizable(false);
        setBackground(white);
        setTitle("Sons wind flume interface");
        setUpWindowCloser();

       JButton startStopButton = new JButton("Start");

       JPanel gaugesContainer = new JPanel();
       gaugesContainer.setLayout(new GridLayout(2, 1));
       gaugesContainer.add(gauge1);
       gaugesContainer.add(gauge2);
       this.add(gaugesContainer, BorderLayout.CENTER);
       this.add(startStopButton, BorderLayout.SOUTH);

       this.setSize(800, 600);
       this.setLocationRelativeTo(null);
       this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        
        startStopButton.addActionListener(e -> {
            isRunning = !isRunning;
            startStopButton.setText(isRunning ? "Stop" : "Start");
            gauge1.setRunning(isRunning);
            gauge2.setRunning(isRunning);
        });

    }
    public void setUpWindowCloser(){
        addWindowListener(new WindowAdapter(){
            @Override 
            public void windowClosing(WindowEvent e){
                System.exit(0);
            }
        }  );
    }

    
    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {
            Canvas example = new Canvas("Live JFreeChart Demo");
            example.setVisible(true);
        });
    }

}
