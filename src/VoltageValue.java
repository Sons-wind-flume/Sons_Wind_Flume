import java.text.DecimalFormat;

public class VoltageValue {

    DecimalFormat decimalFormat = new DecimalFormat("0.00000");
    public double voltage;

    public VoltageValue(double voltageValue) {
        voltage = voltageValue;
    }

    public String toString() {
        return decimalFormat.format(voltage);
    }
}
