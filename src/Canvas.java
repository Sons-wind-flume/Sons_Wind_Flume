import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*; 
public class Canvas extends JFrame {
    Color white = new Color(207,207,207);
    static boolean recording= false;
    private ArrayList<phidgetGate> bridgeList;
    public Canvas(){
        setLayout(null);
        setResizable(false);
        setBackground(white);
        setTitle("Sons wind flume interface");
        setSize(800, 800);
        setUpWindowCloser();
        setVisible(true);
        bridgeList=new ArrayList<>();
        bridgeList.add(new phidgetGate(0));
        bridgeList.add(new phidgetGate(2));
        bridgeList.add(new phidgetGate(1));

        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("update list");
                updateBridgeList();
            }
        });
        timer.start();
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
        System.out.println("interface"+ bridge.getChannel()+" current value: " +bridge.getMedianValue());
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
