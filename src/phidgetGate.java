import com.phidget22.*;
import java.util.ArrayList;
import java.io.IOException;
public class phidgetGate{
    VoltageRatioInput voltageRatioInput;
    ArrayList<Double> values;
    int ChannelNum;
    public phidgetGate(int phidgetChannelNum){
        ChannelNum=phidgetChannelNum;
        try {
            voltageRatioInput=new VoltageRatioInput();
            voltageRatioInput.setChannel(phidgetChannelNum);
            voltageRatioInput.open(5000);
        } catch (Exception e) { 
        }
        
        voltageRatioInput.addVoltageRatioChangeListener((VoltageRatioInputVoltageRatioChangeEvent e) -> {
			values.add(e.getVoltageRatio());
		});

        try {
            System.in.read();
            voltageRatioInput.close();
        } catch (Exception e) {
        }
    }

    
    public void sort(){
        //todo
        values.sort(null);
    }

    public double getMedianValue(){
        if (values==null||values.isEmpty()) {
            return 0;
        }
        sort();
        int len=values.size();
        double median=0;
        if(len%2!=0){
            median=values.get(len/2);
        }
        else{
            median= (values.get((len / 2) - 1) + values.get(len / 2)) / 2.0;
        }
        return median;
    }
    public void resetValues(){
        values = new ArrayList<>();
    }
    public int getChannel(){
        return ChannelNum;
    }

}
