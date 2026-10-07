import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class Canvas extends JFrame {

    Color white = new Color(207, 207, 207);

    static boolean recording = false;

    public interfaceGrid grid;

    private final TrialConfig config;

    public Canvas(
            TrialConfig config,
            boolean configWarning) {

        this.config = config;

        setLayout(null);
        setResizable(false);
        setBackground(white);
        setTitle("Sons wind flume interface");
        setSize(800, 800);

        setUpWindowCloser();

        String warningText = "";

        if (configWarning) {
            warningText =
                    "Warning: Configuration is missing or invalid. "
                    + "Default values are in use.";
        }

        ArrayList<phidgetGate> bridgeList;
        bridgeList = new ArrayList<>();

        phidgetGate gate0 = new phidgetGate(0);
        bridgeList.add(gate0);

        if (!gate0.isConnected()) {

            warningText =
                    warningText
                    + " Phidget channel 0 is not connected.";
        }

        phidgetGate gate1 = new phidgetGate(1);
        bridgeList.add(gate1);

        if (!gate1.isConnected()) {

            warningText =
                    warningText
                    + " Phidget channel 1 is not connected.";
        }

        grid = new interfaceGrid(
                getWidth(),
                600,
                bridgeList
        );

        grid.setLocation(0, 0);
        grid.setBackground(Color.gray);

        add(grid);

        JButton record = new JButton("Record");

        record.setLocation(600, 600);
        record.setSize(100, 100);

        record.addActionListener(
                e -> startStopRecording()
        );

        JButton export = new JButton("Export");

        export.setLocation(0, 600);
        export.setSize(100, 100);

        export.addActionListener(
                e -> export()
        );

        add(record);
        add(export);

        if (!warningText.isBlank()) {

            JLabel warningBanner =
                    new JLabel(warningText);

            warningBanner.setLocation(
                    0,
                    710
            );

            warningBanner.setSize(
                    800,
                    35
            );

            warningBanner.setOpaque(true);
            warningBanner.setBackground(
                    Color.YELLOW
            );
            warningBanner.setForeground(
                    Color.BLACK
            );

            add(warningBanner);
        }

        setVisible(true);

        Timer timer =
                new Timer(
                        1000,
                        new ActionListener() {

                            @Override
                            public void actionPerformed(
                                    ActionEvent e) {

                                grid.updateinterfaces();
                            }
                        }
                );

        timer.start();
    }

    public void export() {

        System.out.println(
                grid.toString()
        );

        FileWriter writer =
                new FileWriter();

        writer.Export(
                grid.toString()
        );
    }

    public void startStopRecording() {

        if (recording) {

            recording = false;

        } else {

            recording = true;
            grid.resetRecord();
        }
    }

    public void setUpWindowCloser() {

        addWindowListener(
                new WindowAdapter() {

                    @Override
                    public void windowClosing(
                            WindowEvent e) {

                        closePhidgetBridges();
                        System.exit(0);
                    }
                }
        );
    }

    public void closePhidgetBridges() {

        grid.closeBridges();
    }
}