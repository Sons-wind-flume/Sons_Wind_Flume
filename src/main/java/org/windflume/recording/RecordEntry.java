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

        String text = "=";
        int index=0;
        char ProjectedArea,AttachmentArea, CentroidHeight,dragCoefficient, sphereForce,modelForce,waterVelocity;
        sphereForce='Z';
        modelForce='Z';
        text += timestamp + " ,=";
        TestObject sphere=null;;
        for (int i = 0; i < recordedValues.size(); i++) {
            index++;
            VoltageValue curValue =
                    recordedValues.get(i); 
            text += "" + curValue.toString();
            text += " ,=";
        }
        for (int i = 0; i < recordedValues.size(); i++) {
            index++;
            
            VoltageValue curValue =
                    recordedValues.get(i);

            text += "" + curValue.toStringForce((char)('A'+i+1),height);
            if(curValue.getObject().isReference()){
                sphere=curValue.getObject();
                sphereForce=(char)('A'+index);
                
            }
            else{
                modelForce=(char)('A'+index);
            }
            
            text += " ,=";
        }
        if(sphere!= null){
            text +=sphere.toStringExport()+",=";
        } else{
            text+="0,0,0,";
        }

        index++;
        ProjectedArea=(char)('A'+index);
        index++;
        AttachmentArea=(char)('A'+index);
        index++;
        CentroidHeight=(char)('A'+index);
        index++;
        dragCoefficient=(char)('A'+index);
       
        index++;
        waterVelocity=(char)('A'+index);
         //Water Velocity (m/s)
        text+="sqrt("+ sphereForce+height+"/("+ProjectedArea+height+"*B1*"+dragCoefficient+height+"*(1/2))),=";
        //Model force / attachment area (N/m2)
        text+=modelForce +""+ height+"/(pi()*("+AttachmentArea+height+")),=";
        //Model drag coefficient
        text+=modelForce +""+height+"/((1/2)*B1*("+waterVelocity+height+"^2)*"+ProjectedArea+height+"),=";
        //model bending moment
        text+=modelForce+""+height+"*"+CentroidHeight+""+height;

        return text;
    }
}