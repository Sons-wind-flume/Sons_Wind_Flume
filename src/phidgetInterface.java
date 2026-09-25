import java.awt.Color;
import java.text.DecimalFormat;
import javax.swing.*;
import javax.swing.border.Border;

public class phidgetInterface extends JPanel {
    public double curVoltageValue=0;
    DecimalFormat df = new DecimalFormat("0.0000");
    public JLabel curVoltageText;
    
    public phidgetGate connectedBridge;
    public phidgetInterface(phidgetGate gate,int height,int width){
        //setLayout(null);
        connectedBridge=gate;
        System.out.println(height);
        setSize(width,height);
        Border lineBorder=BorderFactory.createLineBorder(Color.black);
        setBorder(BorderFactory.createTitledBorder(lineBorder,"Bridge " + gate.getChannel() + " Interface"));
        curVoltageText=new JLabel();
        //curVoltageText.setLocation(10,-50);
        //curVoltageText.setSize(100,200);
        add(curVoltageText);
        
    }
    public int getChannelNum(){
        return connectedBridge.getChannel();
    }

    public void updateText(){
        updateVoltage();
    }
    public double getMedianVoltageValue(){
        return connectedBridge.getMedianVoltageValue();
    }

    public void updateVoltage(){
        curVoltageValue= connectedBridge.getMedianVoltageValue();
        connectedBridge.resetValues();
        curVoltageText.setText("current voltage:"+df.format(curVoltageValue));
        //System.out.println("new voltatage for gate["+connectedBridge.getChannel()+"]: "+ curVoltageValue);
    }

    public void closeBridge(){
        connectedBridge.close();
    }
    

}
