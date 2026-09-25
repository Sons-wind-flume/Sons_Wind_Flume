import java.util.ArrayList;
public class RecordEntry {
    int timestamp;
    ArrayList<VoltageValue> recordedValues;
    public RecordEntry(int time, ArrayList<VoltageValue> list) {
        timestamp=time;
        recordedValues=list;
    }
    public String toString(){
        String Text ="";
        VoltageValue curValue;
        Text+=timestamp+" ,";
        for(int i=0; i<recordedValues.size();i++){
            curValue=recordedValues.get(i);
            Text+=" "+curValue.toString();
            if(i!=recordedValues.size()-1){
                Text+=" ,";
            }
        }
        return Text;
    }
    
}
