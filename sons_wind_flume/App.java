import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

public class App extends JFrame implements BridgeDataListener {

    private static final int CHANNELS = 4;
    
    // Official PhidgetBridge hardware standard limits
    public static final int MIN_INTERVAL_MS = 8;       // 125 Hz (Max hardware rate)
    public static final int MAX_INTERVAL_MS = 60000;   // 1 minute (Max standard interval)
    public static final int DEFAULT_INTERVAL_MS = 1000; // 1 Hz (Standard monitoring rate)

    private BridgeDevice device;
    private CsvDataLogger logger;

    private JComboBox<String> modeCombo;
    private JTextField serialField;
    private JTextField intervalField;
    private JButton connectBtn;
    private JButton disconnectBtn;
    private JLabel statusLabel;

    private JCheckBox[] channelChecks;
    private JTextField[] labelFields;
    private JLabel[] valueLabels;

    private JTextField fileField;
    private JButton browseBtn;
    private JButton recordBtn;
    private JButton pauseBtn;
    private JLabel recordStatusLabel;
    private JLabel sampleCountLabel;

    private boolean recording = false;
    private boolean paused = false;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            App app = new App();
            app.setVisible(true);
        });
    }

    public App() {
        super("Sons Wind Flume - PhidgetBridge Monitor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(780, 650);
        setMinimumSize(new Dimension(700, 590));
        setLocationRelativeTo(null);

        // Clean shutdown on close
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                disconnect();
            }
        });

        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBorder(new EmptyBorder(12, 14, 12, 14));

        root.add(buildConnectionPanel());
        root.add(Box.createVerticalStrut(10));
        root.add(buildChannelsPanel());
        root.add(Box.createVerticalStrut(10));
        root.add(buildLoggingPanel());

        setContentPane(root);
        updateControlStates();
    }

    private JPanel buildConnectionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder("Hardware Connection"));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 6, 5, 6);
        c.anchor = GridBagConstraints.WEST;

        c.gridx = 0; c.gridy = 0;
        panel.add(new JLabel("Mode:"), c);

        c.gridx = 1;
        modeCombo = new JComboBox<>(new String[]{"Physical PhidgetBridge", "Mock Simulator"});
        modeCombo.addActionListener(e -> {
            boolean isPhysical = modeCombo.getSelectedIndex() == 0;
            serialField.setEnabled(isPhysical);
        });
        panel.add(modeCombo, c);

        c.gridx = 2;
        panel.add(new JLabel("Serial:"), c);

        c.gridx = 3;
        serialField = new JTextField("Auto", 7);
        panel.add(serialField, c);

        c.gridx = 4;
        panel.add(new JLabel("Interval (ms):"), c);

        c.gridx = 5;
        intervalField = new JTextField(String.valueOf(DEFAULT_INTERVAL_MS), 5);
        intervalField.setToolTipText("Standard range: 8 ms (125 Hz) to 60,000 ms (1 min)");
        // Restrict interval input strictly to digits (0-9)
        ((AbstractDocument) intervalField.getDocument()).setDocumentFilter(new NumericDocumentFilter());
        panel.add(intervalField, c);

        c.gridx = 6;
        connectBtn = new JButton("Connect");
        connectBtn.addActionListener(e -> connect());
        panel.add(connectBtn, c);

        c.gridx = 7;
        disconnectBtn = new JButton("Disconnect");
        disconnectBtn.setEnabled(false);
        disconnectBtn.addActionListener(e -> disconnect());
        panel.add(disconnectBtn, c);

        // Status row
        c.gridx = 0; c.gridy = 1;
        c.gridwidth = 8;
        c.fill = GridBagConstraints.HORIZONTAL;
        statusLabel = new JLabel("Status: Disconnected (Valid Interval: 8 ms - 60,000 ms)");
        statusLabel.setForeground(Color.RED);
        panel.add(statusLabel, c);

        return panel;
    }

    private JPanel buildChannelsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 8, 8));
        panel.setBorder(new TitledBorder("Channels & Live Readout"));

        channelChecks = new JCheckBox[CHANNELS];
        labelFields = new JTextField[CHANNELS];
        valueLabels = new JLabel[CHANNELS];

        for (int i = 0; i < CHANNELS; i++) {
            JPanel card = new JPanel(new BorderLayout(5, 5));
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                    new EmptyBorder(6, 8, 6, 8)));
            card.setBackground(Color.WHITE);

            JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
            top.setOpaque(false);
            channelChecks[i] = new JCheckBox("Ch " + i, true);
            channelChecks[i].setOpaque(false);
            labelFields[i] = new JTextField("Port_" + i, 8);
            top.add(channelChecks[i]);
            top.add(new JLabel("Label:"));
            top.add(labelFields[i]);

            valueLabels[i] = new JLabel("+0.00000000 V/V", SwingConstants.CENTER);
            valueLabels[i].setFont(new Font("Monospaced", Font.BOLD, 15));
            valueLabels[i].setForeground(new Color(20, 60, 140));

            card.add(top, BorderLayout.NORTH);
            card.add(valueLabels[i], BorderLayout.CENTER);
            panel.add(card);
        }

        return panel;
    }

    private JPanel buildLoggingPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder("CSV Data Logging"));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 6, 5, 6);
        c.anchor = GridBagConstraints.WEST;

        c.gridx = 0; c.gridy = 0;
        panel.add(new JLabel("Save Path:"), c);

        c.gridx = 1;
        c.gridwidth = 3;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        fileField = new JTextField(CsvDataLogger.getNextAvailableFileName(".", "sons_wind_flume_data"));
        panel.add(fileField, c);

        c.gridx = 4;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        browseBtn = new JButton("Browse...");
        browseBtn.addActionListener(e -> selectFile());
        panel.add(browseBtn, c);

        // Control Row
        c.gridx = 0; c.gridy = 1;
        panel.add(new JLabel("Record:"), c);

        c.gridx = 1;
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        recordBtn = new JButton("Start Recording");
        recordBtn.addActionListener(e -> toggleRecord());
        pauseBtn = new JButton("Pause");
        pauseBtn.setEnabled(false);
        pauseBtn.addActionListener(e -> togglePause());
        btns.add(recordBtn);
        btns.add(pauseBtn);
        panel.add(btns, c);

        c.gridx = 2;
        c.gridwidth = 3;
        JPanel info = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        recordStatusLabel = new JLabel("Status: Idle");
        sampleCountLabel = new JLabel("Samples: 0");
        info.add(recordStatusLabel);
        info.add(sampleCountLabel);
        panel.add(info, c);

        return panel;
    }

    private void connect() {
        int interval = DEFAULT_INTERVAL_MS;
        String intervalText = intervalField.getText().trim();
        if (!intervalText.isEmpty()) {
            try {
                interval = Integer.parseInt(intervalText);
            } catch (Exception ignored) {}
        }

        // Validate and clamp to official Phidget hardware standards (8 ms to 60,000 ms)
        if (interval < MIN_INTERVAL_MS) {
            interval = MIN_INTERVAL_MS;
            intervalField.setText(String.valueOf(MIN_INTERVAL_MS));
            JOptionPane.showMessageDialog(this,
                    "Interval clamped to hardware minimum: " + MIN_INTERVAL_MS + " ms (125 Hz).",
                    "Interval Adjusted", JOptionPane.INFORMATION_MESSAGE);
        } else if (interval > MAX_INTERVAL_MS) {
            interval = MAX_INTERVAL_MS;
            intervalField.setText(String.valueOf(MAX_INTERVAL_MS));
            JOptionPane.showMessageDialog(this,
                    "Interval clamped to maximum standard: " + MAX_INTERVAL_MS + " ms (1 min).",
                    "Interval Adjusted", JOptionPane.INFORMATION_MESSAGE);
        }

        boolean useMock = modeCombo.getSelectedIndex() == 1;

        if (useMock) {
            device = new MockBridgeDevice(CHANNELS);
        } else {
            int serial = com.phidget22.Phidget.ANY_SERIAL_NUMBER;
            String text = serialField.getText().trim();
            if (!text.equalsIgnoreCase("Auto") && !text.isEmpty()) {
                try {
                    serial = Integer.parseInt(text);
                } catch (Exception ignored) {}
            }
            device = new PhidgetBridgeDevice(serial, CHANNELS);
        }

        device.setListener(this);
        device.setDataInterval(interval);

        try {
            statusLabel.setText("Connecting at " + interval + " ms interval...");
            statusLabel.setForeground(Color.ORANGE);
            device.open();

            if (device.isConnected() || useMock) {
                statusLabel.setText("Connected: " + device.getDeviceName() + " (" + device.getSerialNumber() + ") @ " + interval + " ms");
                statusLabel.setForeground(new Color(0, 130, 0));
            }
        } catch (Throwable t) {
            statusLabel.setText("Connection failed: " + t.getMessage());
            statusLabel.setForeground(Color.RED);
            JOptionPane.showMessageDialog(this,
                    "Could not open device.\n" + t.getMessage() +
                    "\n\nTip: You can select 'Mock Simulator' mode to test without hardware.",
                    "Connection Error", JOptionPane.ERROR_MESSAGE);
            device = null;
        }

        updateControlStates();
    }

    private void disconnect() {
        if (recording) stopRecording();

        if (device != null) {
            device.close();
            device = null;
        }

        statusLabel.setText("Status: Disconnected");
        statusLabel.setForeground(Color.RED);

        for (JLabel val : valueLabels) {
            val.setText("+0.00000000 V/V");
        }

        updateControlStates();
    }

    private void toggleRecord() {
        if (device == null || !device.isConnected()) {
            JOptionPane.showMessageDialog(this, "Connect to Phidget or Mock Simulator first!");
            return;
        }

        if (!recording) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void startRecording() {
        try {
            String path = fileField.getText().trim();
            logger = new CsvDataLogger(path);
            recording = true;
            paused = false;

            recordBtn.setText("Stop Recording");
            pauseBtn.setEnabled(true);
            pauseBtn.setText("Pause");
            recordStatusLabel.setText("Status: Recording");
            recordStatusLabel.setForeground(new Color(0, 130, 0));
            fileField.setEditable(false);
            browseBtn.setEnabled(false);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Cannot write to file: " + ex.getMessage(),
                    "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void stopRecording() {
        if (logger != null) {
            logger.close();
            logger = null;
        }
        recording = false;
        paused = false;

        recordBtn.setText("Start Recording");
        pauseBtn.setEnabled(false);
        recordStatusLabel.setText("Status: Idle");
        recordStatusLabel.setForeground(Color.DARK_GRAY);
        fileField.setEditable(true);
        browseBtn.setEnabled(true);
        fileField.setText(CsvDataLogger.getNextAvailableFileName(".", "sons_wind_flume_data"));
    }

    private void togglePause() {
        if (!recording) return;
        paused = !paused;
        if (paused) {
            pauseBtn.setText("Resume");
            recordStatusLabel.setText("Status: Paused");
            recordStatusLabel.setForeground(Color.ORANGE);
        } else {
            pauseBtn.setText("Pause");
            recordStatusLabel.setText("Status: Recording");
            recordStatusLabel.setForeground(new Color(0, 130, 0));
        }
    }

    private void selectFile() {
        JFileChooser chooser = new JFileChooser(".");
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            String path = f.getAbsolutePath();
            if (!path.toLowerCase().endsWith(".csv")) path += ".csv";
            fileField.setText(path);
        }
    }

    @Override
    public void onReading(SensorReading reading) {
        SwingUtilities.invokeLater(() -> {
            int ch = reading.getChannel();
            if (ch >= 0 && ch < CHANNELS) {
                valueLabels[ch].setText(String.format("%+12.8f V/V", reading.getVoltageRatio()));

                if (recording && !paused && logger != null && channelChecks[ch].isSelected()) {
                    logger.writeReading(reading, labelFields[ch].getText().trim());
                    sampleCountLabel.setText("Samples: " + logger.getRecordCount());
                }
            }
        });
    }

    @Override
    public void onDeviceAttached(int channel, String deviceName, int serialNumber) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("Connected: " + deviceName + " (" + serialNumber + ")");
            statusLabel.setForeground(new Color(0, 130, 0));
            updateControlStates();
        });
    }

    @Override
    public void onDeviceDetached(int channel) {
        SwingUtilities.invokeLater(() -> {
            if (channel == 0) {
                statusLabel.setText("Device Detached!");
                statusLabel.setForeground(Color.RED);
                updateControlStates();
            }
        });
    }

    private void updateControlStates() {
        boolean connected = (device != null && device.isConnected());
        connectBtn.setEnabled(!connected);
        disconnectBtn.setEnabled(connected);
        modeCombo.setEnabled(!connected);
        serialField.setEnabled(!connected && modeCombo.getSelectedIndex() == 0);
        intervalField.setEditable(!connected);
    }

    /**
     * DocumentFilter that only allows integer digits (0-9).
     */
    private static class NumericDocumentFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string == null) return;
            if (string.matches("\\d*")) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text == null) return;
            if (text.matches("\\d*")) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
    }
}
