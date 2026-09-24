import java.util.ArrayList;
import javax.swing.*;
public class interfaceGrid extends JPanel {
    ArrayList<phidgetInterface> interfaceList;

    public interfaceGrid(int width, int height, ArrayList<phidgetGate> list){
        setLayout(null);
        setSize(width,height);
        createInterfaces(list);
    }

    public void createInterfaces(ArrayList<phidgetGate> list){
        phidgetGate curGate;
        for (int i = 0; i < list.size(); i++) {
            curGate=list.get(i);
            createInterface(curGate, (getHeight()/list.size())-1, i*(getHeight()/list.size()));
        }
    }

    public void createInterface(phidgetGate gate,int height,int y){
        phidgetInterface bridgeinterface;
        bridgeinterface=new phidgetInterface(gate,height,getWidth());
        bridgeinterface.setLocation(0,y);
        add(bridgeinterface);
        interfaceList.add(bridgeinterface);
    }

    public void updateinterfaces(){
        for(phidgetInterface e : interfaceList){
            e.updateText();
        }
    }
}
