import java.awt.Color;
import java.util.ArrayList;
import javax.swing.*;
public class interfaceGrid extends JPanel {
    ArrayList<phidgetInterface> interfaceList;

    public interfaceGrid(int width, int height, ArrayList<phidgetGate> list){
        setLayout(null);
        setSize(width,height);
        interfaceList=new ArrayList<>();
        createInterfaces(list);
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

    public void updateinterfaces(){
        for(phidgetInterface e : interfaceList){
            if(Canvas.recording){
                //todo
            }
            e.updateText();
            //e.setSize(200,5);
        }
    }
}
