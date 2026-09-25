import java.text.DecimalFormat;
public class VoltageValue {
    DecimalFormat df = new DecimalFormat("0.00000");
    public double voltage;
    public VoltageValue(double vol){
        voltage=vol;
    }
    public String toString(){
        return (df.format(voltage)+"");
    }
}
