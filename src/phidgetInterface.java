import java.text.DecimalFormat;
import javax.swing.*;

public class phidgetInterface extends JPanel {
    public int ChannelNum;
    public double curVoltageValue=0;
    DecimalFormat df = new DecimalFormat("#.#####");
    public JLabel curVoltageText;
    public phidgetInterface(int ChannelNum) {
        //setLayout(null);
        this.ChannelNum=ChannelNum;
        curVoltageText=new JLabel("current value:"+df.format(curVoltageValue));
        //curVoltage.setLocation(100,100);
        add(curVoltageText);
    }
    public phidgetGate connectedBridge;
    public phidgetInterface(phidgetGate gate,int height,int width){
        //setLayout(null);
        connectedBridge=gate;
        setSize(height,width);
        setBorder(BorderFactory.createTitledBorder("Bridge " + gate.getChannel() + " Interface"));

    }
    public void updateText(){
        curVoltage.setText("current value:"+df.format(value));
    }
    

}
