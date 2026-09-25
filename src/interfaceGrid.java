import java.awt.Color;
import java.util.ArrayList;
import javax.swing.*;
public class interfaceGrid extends JPanel {
    ArrayList<phidgetInterface> interfaceList;
    ArrayList<RecordEntry> recordingList;

    public interfaceGrid(int width, int height, ArrayList<phidgetGate> list){
        setLayout(null);
        setSize(width,height);
        interfaceList=new ArrayList<>();
        createInterfaces(list);
        recordingList=new ArrayList<>();
    }

    public void createInterfaces(ArrayList<phidgetGate> list){
        phidgetGate curGate;
        list.sort((u1, u2) -> Integer.compare(u1.getChannel(), u2.getChannel()));
        for (int i = 0; i < list.size(); i++) {
            curGate=list.get(i);
            createInterface(curGate, (getHeight()/(list.size()))-1, i*(getHeight()/list.size()));
        }
    }

    public void createInterface(phidgetGate gate,int height,int y){
        phidgetInterface bridgeinterface;
        bridgeinterface=new phidgetInterface(gate,height,getWidth());
        bridgeinterface.setLocation(0,y);
        bridgeinterface.setBackground(new Color(212, 203, 207));
        add(bridgeinterface);
        interfaceList.add(bridgeinterface);
    }
    public int compare(phidgetGate first, phidgetGate second){
        return first.getChannel()-second.getChannel();

    }
    public void resetRecord(){
        recordingList=new ArrayList<>();
    }
    public void updateinterfaces(){
        ArrayList<VoltageValue> values= new ArrayList<>();

        for(phidgetInterface e : interfaceList){
            if(Canvas.recording){
                values.add(new VoltageValue(e.getMedianVoltageValue()));
            }
            e.updateText();
        }

        if(Canvas.recording){
            recordingList.add(new RecordEntry(recordingList.size(), values));
        }
    }
    public void closeBridges(){
        for(phidgetInterface e: interfaceList){
            e.closeBridge();
        }
    }
    public String toString(){
        String Text = toStringHeader() +"\n";
        for(RecordEntry entry:recordingList){
            Text+= entry.toString()+"\n";
        }
        return Text;
    }
    public String toStringHeader(){
        String Text="Time Stamps ,";
        phidgetInterface curInterface;
        for(int i=0; i < interfaceList.size() ;i++){
            curInterface=interfaceList.get(i);
            Text+=" Channel Voltage["+curInterface.getChannelNum()+"] ,";
        }
        return Text;
    }
}
