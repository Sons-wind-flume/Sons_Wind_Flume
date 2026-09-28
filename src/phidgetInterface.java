import java.awt.Color;
import java.text.DecimalFormat;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class phidgetInterface extends JPanel {

    public double currentVoltageValue = 0.0;
    DecimalFormat decimalFormat = new DecimalFormat("0.0000");
    public JLabel currentVoltageText;
    public phidgetGate connectedBridge;

    public phidgetInterface(phidgetGate gate, int height, int width) {
        connectedBridge = gate;
        setSize(width, height);

        Border lineBorder;
        lineBorder = BorderFactory.createLineBorder(Color.black);

        setBorder(
            BorderFactory.createTitledBorder(
                lineBorder,
                "Bridge " + gate.getChannel() + " Interface"
            )
        );

        currentVoltageText = new JLabel();
        add(currentVoltageText);
    }

    public int getChannelNum() {
        return connectedBridge.getChannel();
    }

    public void updateText() {
        updateVoltage();
    }

    public void updateVoltage() {
        currentVoltageValue = connectedBridge.getMedianVoltageValue();
        currentVoltageText.setText(
            "current voltage:" + decimalFormat.format(currentVoltageValue)
        );
    }

    public double getCurrentVoltageValue() {
        return currentVoltageValue;
    }

    public void closeBridge() {
        connectedBridge.close();
    }
}
