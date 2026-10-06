package org.windflume.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JPanel;

import org.windflume.phidget.PhidgetGate;

public class InterfaceGrid extends JPanel {

    private final ArrayList<PhidgetInterface> interfaceList;
    private ArrayList<org.windflume.recording.RecordEntry> recordingList;

    public InterfaceGrid( int width, int height, ArrayList<PhidgetGate> list) {

        setLayout(new GridLayout(1, 0, 15, 0));
        setSize(width, height);
        setPreferredSize(new Dimension(width, height));
        setOpaque(false);

        interfaceList = new ArrayList<>();

        createInterfaces(list);

        recordingList = new ArrayList<>();
    }

    public void createInterfaces(
            ArrayList<PhidgetGate> list) {

        list.sort(
                (u1, u2) ->
                        Integer.compare(
                                u1.getChannel(),
                                u2.getChannel()
                        )
        );

        for (int i = 0; i < list.size(); i++) {

            PhidgetGate curGate = list.get(i);

            createInterface(
                    curGate,
                    (getHeight() / list.size()) - 1,
                    i * (getHeight() / list.size())
            );
        }
    }

    public void createInterface( PhidgetGate gate, int height, int y) {

        PhidgetInterface bridgeInterface = new PhidgetInterface( gate, height, getWidth());

        bridgeInterface.setBackground(new Color(212, 203, 207) );

        add(bridgeInterface);

        interfaceList.add(bridgeInterface);

        bridgeInterface.roleBox.addActionListener(e -> {
    GaugeRole picked = bridgeInterface.getRole();
    for (PhidgetInterface other : interfaceList) {
        if (other != bridgeInterface && other.getRole() == picked) {
            other.roleBox.setSelectedItem(
                    picked == GaugeRole.SPHERE
                            ? GaugeRole.TEST_OBJECT
                            : GaugeRole.SPHERE);
        }
    }
});}

   public PhidgetInterface getInterfaceByRole (GaugeRole role){
        for (PhidgetInterface e : interfaceList){
                if (e.getRole()  == role) return e;
        }
        return null;
   }

   public boolean hasRecording(){
    return !recordingList.isEmpty();
   }


    public void resetRecord() {

        recordingList = new ArrayList<>();
    }

    public void updateinterfaces() {

    ArrayList<org.windflume.recording.VoltageValue> values =
            new ArrayList<>();

    for (PhidgetInterface e : interfaceList) {

        e.updateText();

        if (Canvas.recording) {

            values.add(
                    new org.windflume.recording.VoltageValue(
                            e.curVoltageValue
                    )
            );
        }
    }

    if (Canvas.recording) {

        recordingList.add(
                new org.windflume.recording.RecordEntry(
                        recordingList.size(),
                        values
                )
        );

        System.out.println(
                "Recorded row: " + recordingList.size()
        );
    }
}

public void clearCharts() {
        for (PhidgetInterface e : interfaceList){
                e.clearChart();
        }
}

    public void closeBridges() {

        for (PhidgetInterface e : interfaceList) {
            e.closeBridge();
        }
    }

    @Override
    public String toString() {

        String text = toStringHeader() + "\n";

        for (
                org.windflume.recording.RecordEntry entry
                : recordingList
        ) {

            text += entry.toString() + "\n";
        }

        return text;
    }

    public String toStringHeader() {

        String text = "Time Stamps ,";

        for (
                int i = 0;
                i < interfaceList.size();
                i++
        ) {

            PhidgetInterface curInterface =
                    interfaceList.get(i);

            text +=
                    " Channel Voltage[" +
                    curInterface.getChannelNum() +
                    "] ,";
        }

        return text;
    }

    public double getVoltage(int index) {

    if (index < 0 || index >= interfaceList.size()) {
        return 0.0;
    }

    return interfaceList.get(index).curVoltageValue;
}

}