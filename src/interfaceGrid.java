import java.awt.Color;
import java.util.ArrayList;
import javax.swing.JPanel;

public class interfaceGrid extends JPanel {

    ArrayList<phidgetInterface> interfaceList;
    ArrayList<RecordEntry> recordingList;

    public interfaceGrid(int width, int height, ArrayList<phidgetGate> list) {
        setLayout(null);
        setSize(width, height);

        interfaceList = new ArrayList<phidgetInterface>();
        recordingList = new ArrayList<RecordEntry>();

        sortBridgeList(list);
        createInterfaces(list);
    }

    public void sortBridgeList(ArrayList<phidgetGate> list) {
        int firstIndex;
        int secondIndex;
        phidgetGate firstGate;
        phidgetGate secondGate;

        for (firstIndex = 0; firstIndex < list.size(); firstIndex = firstIndex + 1) {
            for (secondIndex = firstIndex + 1; secondIndex < list.size(); secondIndex = secondIndex + 1) {
                firstGate = list.get(firstIndex);
                secondGate = list.get(secondIndex);

                if (firstGate.getChannel() > secondGate.getChannel()) {
                    list.set(firstIndex, secondGate);
                    list.set(secondIndex, firstGate);
                }
            }
        }
    }

    public void createInterfaces(ArrayList<phidgetGate> list) {
        int index;
        int height;
        int yPosition;
        phidgetGate currentGate;

        for (index = 0; index < list.size(); index = index + 1) {
            currentGate = list.get(index);
            height = (getHeight() / list.size()) - 1;
            yPosition = index * (getHeight() / list.size());
            createInterface(currentGate, height, yPosition);
        }
    }

    public void createInterface(phidgetGate gate, int height, int yPosition) {
        phidgetInterface bridgeInterface;

        bridgeInterface = new phidgetInterface(gate, height, getWidth());
        bridgeInterface.setLocation(0, yPosition);
        bridgeInterface.setBackground(new Color(212, 203, 207));

        add(bridgeInterface);
        interfaceList.add(bridgeInterface);
    }

    public void resetRecord() {
        recordingList = new ArrayList<RecordEntry>();
    }

    public void updateinterfaces() {
        ArrayList<VoltageValue> values;
        int index;
        phidgetInterface currentInterface;

        values = new ArrayList<VoltageValue>();

        for (index = 0; index < interfaceList.size(); index = index + 1) {
            currentInterface = interfaceList.get(index);
            currentInterface.updateText();

            if (Canvas.recording == true) {
                values.add(new VoltageValue(currentInterface.getCurrentVoltageValue()));
            }
        }

        if (Canvas.recording == true) {
            recordingList.add(new RecordEntry(recordingList.size(), values));
        }
    }

    public void closeBridges() {
        int index;
        phidgetInterface currentInterface;

        for (index = 0; index < interfaceList.size(); index = index + 1) {
            currentInterface = interfaceList.get(index);
            currentInterface.closeBridge();
        }
    }

    public String toString() {
        String text;
        int index;
        RecordEntry entry;

        text = toStringHeader() + "\n";

        for (index = 0; index < recordingList.size(); index = index + 1) {
            entry = recordingList.get(index);
            text = text + entry.toString() + "\n";
        }

        return text;
    }

    public String toStringHeader() {
        String text;
        int index;
        phidgetInterface currentInterface;

        text = "Time Stamps ,";

        for (index = 0; index < interfaceList.size(); index = index + 1) {
            currentInterface = interfaceList.get(index);
            text = text + " Channel Voltage[" + currentInterface.getChannelNum() + "] ,";
        }

        return text;
    }
}
