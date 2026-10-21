package org.windflume.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.windflume.config.Configuration;
import org.windflume.config.TestObject;
import org.windflume.phidget.PhidgetGate;

public class PhidgetInterface extends JPanel {
    private static final TestObject CUSTOM = new TestObject("Custom object…", 0);

    public double curVoltageValue = 0;

    private final DecimalFormat df = new DecimalFormat("0.0000");

    public JLabel curVoltageText;

    public PhidgetGate connectedBridge;
    public final JComboBox<TestObject> objectBox;
    private final Configuration config;
    private final GaugeBlockPanel chart;
    private final JLabel statusLabel = new JLabel();
    private DashboardDialog dashboard;

    private TestObject lastObject;
    private Consumer<TestObject> onCustomCreated;

    private final JTextField attachmentField = readOnlyField();
    private final JTextField modelField = readOnlyField();
    private final JTextField projectedField = readOnlyField();
    private final JTextField orientationField = readOnlyField();
    private final JTextField dragField = readOnlyField();

    public PhidgetInterface(PhidgetGate gate, int height, int width, Configuration config) {

        connectedBridge = gate;
        this.config = config;
        List<TestObject> objects = config.getTestObjects();

        setLayout(new BorderLayout(0, 8));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JLabel title = new JLabel("Bridge " + gate.getChannel());
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));

        updateStatus();

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titlePanel.add(title);
        titlePanel.add(statusLabel);

        List<TestObject> options = new ArrayList<>(objects);
        options.add(CUSTOM);
        objectBox = new JComboBox<>(options.toArray(new TestObject[0]));
        objectBox.setSelectedIndex(Math.min(gate.getChannel(), objects.size() - 1));
        objectBox.setPrototypeDisplayValue(new TestObject("Large test object", 0));
        objectBox.addActionListener(e -> {
            if (objectBox.getSelectedItem() == CUSTOM) {
                askForCustomObject();
            } else {
                refreshFields();
            }
        });

        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        rolePanel.setOpaque(false);
        rolePanel.add(new JLabel("Assigned object:"));
        rolePanel.add(objectBox);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(titlePanel, BorderLayout.WEST);
        top.add(rolePanel, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        JPanel fields = new JPanel(new GridLayout(2, 3, 14, 8));
        fields.setOpaque(false);
        fields.add(labeled("Attachment area (m²)", attachmentField));
        fields.add(labeled("Model ID", modelField));
        fields.add(labeled("Projected area (m²)", projectedField));
        fields.add(labeled("Orientation", orientationField));
        fields.add(labeled("Drag coefficient", dragField));

        chart = new GaugeBlockPanel("Bridge " + gate.getChannel());
        chart.setOnClick(this::openDashboard);

        JPanel middle = new JPanel(new BorderLayout(15, 0));
        middle.setOpaque(false);
        middle.add(fields, BorderLayout.WEST);
        middle.add(chart, BorderLayout.CENTER);
        add(middle, BorderLayout.CENTER);

        curVoltageText = new JLabel();

        JLabel hint = new JLabel("Click the chart for more details");
        hint.setFont(hint.getFont().deriveFont(Font.ITALIC, 11f));
        hint.setForeground(Color.DARK_GRAY);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(curVoltageText, BorderLayout.WEST);
        bottom.add(hint, BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);

        refreshFields();
    }


    private static JTextField readOnlyField() {
        JTextField f = new JTextField(9);
        f.setEditable(false);
        f.setBackground(Color.WHITE);
        f.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        return f;
    }

    private static JPanel labeled(String label, JTextField field) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(l);
        p.add(field);
        return p;
    }

    private static String area(double v) {
        return v > 0 ? String.format("%.4f", v) : "--";
    }

    private static String text(String s) {
        return (s == null || s.isBlank()) ? "--" : s;
    }

    private void refreshFields() {
        TestObject obj = getSelectedObject();
        lastObject = obj;

        attachmentField.setText(area(obj.getAttachmentArea()));
        modelField.setText(text(obj.getModelId()));
        projectedField.setText(area(obj.getProjectedArea()));
        orientationField.setText(text(obj.getOrientation()));
        dragField.setText(obj.isReference() && obj.getKnownDragCoefficient() > 0
                ? String.format("%.3f", config.getDragCoefficient())
                : "--");
    }

    public void showMeasuredCd(double cd) {
        if (!getSelectedObject().isReference()) {
            dragField.setText(cd >= 0 ? String.format("%.3f", cd) : "--");
        }
    }

    public void setOnCustomCreated(Consumer<TestObject> callback) {
        onCustomCreated = callback;
    }

    public void addObjectOption(TestObject obj) {
        objectBox.insertItemAt(obj, objectBox.getItemCount() - 1);
    }

    public void selectObject(TestObject obj) {
        objectBox.setSelectedItem(obj);
    }

    private void askForCustomObject() {
        JTextField nameField = new JTextField("My object");
        JTextField areaField = new JTextField();
        Object[] form = {
                "Name:", nameField,
                "Projected area (m²), e.g. 0.0030 = 30 cm²:", areaField
        };

        int ok = JOptionPane.showConfirmDialog(this, form, "New test object",
                JOptionPane.OK_CANCEL_OPTION);

        TestObject created = null;

        if (ok == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            try {
                double area = Double.parseDouble(areaField.getText().trim());
                if (name.isEmpty() || area <= 0) {
                    JOptionPane.showMessageDialog(this,
                            "Please enter a name and an area above 0.");
                } else if (config.getTestObject(name) != null) {
                    JOptionPane.showMessageDialog(this,
                            "An object called \"" + name + "\" already exists.");
                } else {
                    created = new TestObject(name, area);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "The area must be a number, e.g. 0.0030.");
            }
        }

        if (created == null) {
            objectBox.setSelectedItem(lastObject);  
        } else if (onCustomCreated != null) {
            onCustomCreated.accept(created);
        }
    }


    public void updateStatus() {
        if (connectedBridge.isConnected()) {
            statusLabel.setText("Sensor connected");
            statusLabel.setForeground(new Color(40, 140, 60));
        } else {
            statusLabel.setText("Offline: check cable");
            statusLabel.setForeground(new Color(200, 100, 0));
        }
    }

    private void openDashboard() {
        if (dashboard == null) {
            dashboard = new DashboardDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Bridge " + getChannelNum() + " Dashboard");
        }
        dashboard.setVisible(true);
        dashboard.toFront();
    }

    public TestObject getSelectedObject() {
        return (TestObject) objectBox.getSelectedItem();
    }

    public int getChannelNum() {
        return connectedBridge.getChannel();
    }

    public DashboardDialog getDashboard() {
        return dashboard;
    }

    public void updateText() {
        updateVoltage();
    }

    public double getMedianVoltageValue() {
        return connectedBridge.getMedianVoltageValue();
    }

    public void updateVoltage() {
        curVoltageValue = connectedBridge.getMedianVoltageValue();
        chart.addVoltage(curVoltageValue);

        connectedBridge.resetValues();

        curVoltageText.setText("Current voltage: " + df.format(curVoltageValue));
    }

    public void clearChart() {
        chart.clearGraph();
        if (dashboard != null) {
            dashboard.clearChart();
        }
    }

    public void closeBridge() {
        connectedBridge.close();
    }
}