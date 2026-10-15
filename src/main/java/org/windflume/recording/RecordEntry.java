package org.windflume.recording;
import org.windflume.config.Configuration;
import org.windflume.config.TestObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RecordEntry {

    private final int timestamp;
    private final ArrayList<VoltageValue> recordedValues;
    //private final Map<Integer, VoltageValue> recordedValues = new HashMap<>();

    public RecordEntry(
            int time,
            ArrayList<VoltageValue> list,
            Configuration config) {

        timestamp = time;
        recordedValues=list;
    }

    public String toString(int height) {

        String text = "";
        int index=0;
        char ProjectedArea,AttachmentArea, CentroidHeight, sphereForce;
        sphereForce='Z';
        text += timestamp + " ,";
        TestObject sphere=null;;
        for (int i = 0; i < recordedValues.size(); i++) {
            index++;
            VoltageValue curValue =
                    recordedValues.get(i); 
            text += " " + curValue.toString();
            text += " ,";
        }
        for (int i = 0; i < recordedValues.size(); i++) {
            index++;
            VoltageValue curValue =
                    recordedValues.get(i);

            text += "" + curValue.toStringForce();
            if(curValue.getObject().isReference()){
                sphere=curValue.getObject();
                sphereForce=(char)('A'+index);
            }
            
            text += " ,";
        }
        if(sphere!= null){
            text +=sphere.toStringExport()+",";
        } else{
            text+="0,0,0,";
        }
        index++;
        ProjectedArea=(char)('A'+index);
        index++;
        AttachmentArea=(char)('A'+index);
        index++;
        CentroidHeight=(char)('A'+index);
        System.out.println(sphereForce);
        return text;
    }
}