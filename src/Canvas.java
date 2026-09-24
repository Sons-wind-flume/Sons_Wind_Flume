import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*; 
public class Canvas extends JFrame {
    Color white = new Color(207,207,207);
    static boolean recording= false;
    public interfaceGrid grid;
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
        bridgeList.add(new phidgetGate(0));
        bridgeList.add(new phidgetGate(2));
        bridgeList.add(new phidgetGate(1));
        grid=new interfaceGrid(getWidth(), 600, bridgeList);
        //grid.setSize(100,100);
        grid.setLocation(0,0);
        grid.setBackground(Color.gray);
        add(grid);
        
        setVisible(true);

        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                grid.updateinterfaces();
            }
        });
        timer.start();
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
