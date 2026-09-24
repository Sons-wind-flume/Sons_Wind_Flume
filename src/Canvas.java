import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*; 
public class Canvas extends JFrame {
    Color white = new Color(207,207,207);
    static boolean recording= false;
    private ArrayList<phidgetGate> bridgeList;
    private ArrayList<phidgetInterface> interfaceList;
    public Canvas(){
        setLayout(null);
        setResizable(false);
        setBackground(white);
        setTitle("Sons wind flume interface");
        setSize(800, 800);
        setUpWindowCloser();

        bridgeList=new ArrayList<>();
        interfaceList= new ArrayList<>();
        bridgeList.add(new phidgetGate(0));
        bridgeList.add(new phidgetGate(2));
        bridgeList.add(new phidgetGate(1));
        initalizeBridgeInterfaces();
        setVisible(true);

        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("update list");
                updateBridgeList();
            }
        });
        timer.start();
    }
    
    public void initalizeBridgeInterfaces(){
        phidgetInterface panelInterface;
        for(int i=0;i<bridgeList.size();i++){
            panelInterface= new phidgetInterface(i);
            panelInterface.setSize(getWidth(), (getHeight()/(bridgeList.size()+1))-1);
            panelInterface.setLocation(0,i*(getHeight()/(bridgeList.size()+1)));
            panelInterface.setBackground(Color.gray);
            add(panelInterface);
            interfaceList.add(panelInterface);
            //System.out.println(i*(getHeight()/(bridgeList.size()+1)));
        }

    }

    public void updateBridgeList(){
        if(bridgeList==null){
            return;
        }
        for (phidgetGate bridge : bridgeList) {
            updateBridgeInterface(bridge);
        }
    }
    
    public void updateBridgeInterface(phidgetGate bridge){
        //System.out.println("interface"+ bridge.getChannel()+" current value: " +bridge.getMedianValue());
        for(phidgetInterface e:interfaceList){
            if(e.ChannelNum==bridge.getChannel()){
                System.out.println("found according interface updating");
                e.value=bridge.getMedianValue();
                e.updateText();
            }
        }
        
        bridge.resetValues();

    }
    public void setUpWindowCloser(){
        addWindowListener(new WindowAdapter(){
            @Override 
            public void windowClosing(WindowEvent e){
                closePhidgetBridges();
                System.exit(0);
            }
        }  );
    }
    public void closePhidgetBridges(){
        if(bridgeList==null){
            return;
        }
        for (phidgetGate bridge : bridgeList) {
            bridge.close();
        }

    }

}
