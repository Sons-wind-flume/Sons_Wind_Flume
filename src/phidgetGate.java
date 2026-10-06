import com.phidget22.*;
import java.util.ArrayList;
import java.io.IOException;
public class phidgetGate{
    VoltageRatioInput voltageRatioInput;
    ArrayList<Double> values;
    float slope,offset;
    int ChannelNum;
    public phidgetGate(int phidgetChannelNum){
        ChannelNum=phidgetChannelNum;
        initalizeGate();
        slope=0;
        offset=0;
    }

    public void initalizeGate(){
        try {
            voltageRatioInput=new VoltageRatioInput();
            voltageRatioInput.setChannel(ChannelNum);
            voltageRatioInput.open(1000);
        } catch (Exception e) { 
            System.out.println(e);
        }
        
        voltageRatioInput.addVoltageRatioChangeListener((VoltageRatioInputVoltageRatioChangeEvent e) -> {
            //System.out.println(e.getVoltageRatio());
			values.add(e.getVoltageRatio());
		});
    }

    public void close(){
        try {
        voltageRatioInput.close();
        }
        catch(Exception e){

        };
    }
    public void sort(){
        //todo
        values.sort(null);
    }

    public double getMedianVoltageValue(){
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

    public float getOffset(){
        return offset;
    }

    public float getSlope(){
        return slope;
    }

    public void setOffset(float value){
        offset=value;
    }

    public void SetSlope(float value){
        offset=value;
    }
}
