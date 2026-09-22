import javax.swing.*;
public class phidgetInterface extends JPanel {

    public double value=0;
    public JLabel curVoltage;
    public phidgetInterface() {
        //setLayout(null);
        
        curVoltage=new JLabel("current value:"+value);
        //curVoltage.setLocation(100,100);
        add(curVoltage);
    }
    public void updateText(){
        curVoltage.setText("current value:"+value);
    }
    

}
