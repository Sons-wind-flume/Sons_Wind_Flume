import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.Timer;

public class Canvas extends JFrame {

    Color white = new Color(207, 207, 207);
    static boolean recording = false;

    public interfaceGrid grid;
    public JButton recordButton;

    public Canvas() {
        setLayout(null);
        setResizable(false);
        setBackground(white);
        setTitle("Sons wind flume interface");
        setSize(800, 800);
        setUpWindowCloser();

        ArrayList<phidgetGate> bridgeList;
        bridgeList = new ArrayList<phidgetGate>();

        bridgeList.add(new phidgetGate(0));
        bridgeList.add(new phidgetGate(1));

        grid = new interfaceGrid(getWidth(), 600, bridgeList);
        grid.setLocation(0, 0);
        grid.setBackground(Color.gray);
        add(grid);

        recordButton = new JButton("START Trial");
        recordButton.setLocation(600, 600);
        recordButton.setSize(120, 100);
        recordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                startStopRecording();
            }
        });

        JButton exportButton;
        exportButton = new JButton("Export");
        exportButton.setLocation(0, 600);
        exportButton.setSize(100, 100);
        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                export();
            }
        });

        add(recordButton);
        add(exportButton);

        Timer timer;
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                grid.updateinterfaces();
            }
        });
        timer.start();

        setVisible(true);
    }

    public void export() {
        FileWriter writer;
        writer = new FileWriter();
        writer.export(grid.toString());
    }

    public void startStopRecording() {
        if (recording == true) {
            recording = false;
            recordButton.setText("START Trial");
        } else {
            recording = true;
            grid.resetRecord();
            recordButton.setText("STOP Trial");
        }
    }

    public void setUpWindowCloser() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                closePhidgetBridges();
                System.exit(0);
            }
        });
    }

    public void closePhidgetBridges() {
        grid.closeBridges();
    }
}
