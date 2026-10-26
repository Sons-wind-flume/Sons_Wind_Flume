package org.windflume.recording;
import java.util.ArrayList;

import org.windflume.config.Configuration;
import org.windflume.config.TestObject;

public class RecordEntry {

    private final int timestamp;
    private final ArrayList<VoltageValue> recordedValues;

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
        char sphereProjectedArea,testObjectProjectedArea,testObjectAttachmentArea, testObjectCentroidHeight,sphereDragCoefficient, sphereForce,modelForce,waterVelocity;
        sphereForce='Z';
        modelForce='Z';
        text += timestamp + " ,=";
        TestObject testObject=null;
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
                testObject=curValue.getObject();
            }
            
            text += " ,=";
        }
        
         if(testObject!= null){
            text +=testObject.toStringProjectedArea()+",=";
            text +=testObject.toStringAttachmentArea()+",=";
            text +=testObject.toStringCentroidHeight()+",";
        } else{
            text+="=0,=0,=0,";
        }
        if(sphere!= null){
            text +=sphere.toStringProjectedArea()+",";
            text +=sphere.toStringDragCoefficient()+",";
        } else{
            text+="=0,=0,";
        }
        
       

        index++;
        testObjectProjectedArea=(char)('A'+index);
        index++;
        testObjectAttachmentArea=(char)('A'+index);
        index++;
        testObjectCentroidHeight=(char)('A'+index);
        index++;
        
        sphereProjectedArea=(char)('A'+index);
        index++;
        sphereDragCoefficient=(char)('A'+index);
        index++;
        //dragCoefficient=(char)('A'+index);
       
        //index++;
        waterVelocity=(char)('A'+index);
         //Water Velocity (m/s)
        text+="=sqrt("+ sphereForce+height+"/("+sphereProjectedArea+height+"*B1*"+sphereDragCoefficient+height+"*(1/2))),=";
        //Model force / attachment area (N/m2)
        text+=modelForce +""+ height+"/(pi()*("+testObjectAttachmentArea+height+")),=";
        //Model drag coefficient
        text+=modelForce +""+height+"/((1/2)*B1*("+waterVelocity+height+"^2)*"+testObjectProjectedArea+height+"),=";
        //model bending moment
        text+=modelForce+""+height+"*"+testObjectCentroidHeight+""+height;

        return text;
    }
}