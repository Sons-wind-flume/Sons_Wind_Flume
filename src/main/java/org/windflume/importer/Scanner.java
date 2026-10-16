package org.windflume.importer;

import org.windflume.config.Configuration;
import org.windflume.config.TestObject;
import org.windflume.phidget.PhidgetGate;

import com.phidget22.Phidget;
import com.phidget22.PhidgetException;

public class Scanner {
    public Configuration config;    
    public int maxActiveGates=2;

    public Scanner(Configuration conf){
        config =conf;
    }

    public void scan(String str){
        String[] strList= str.split(" ");
        int index=0;

        if(strList.length<1){
            return;
        }

        switch (strList[index].toUpperCase()) {
            
            case "ACTIVE_GATES":
                index++;
                activateGates(strList,index);
                break;
            case "GATE":
                index++;
                gateOperation(strList,index);
                break;
            case "WATER_DENSITY":
                index++;
                setWaterDensity(strList,index);
                break;
            case "SPHERE":
                index++;
                sphereOperation(strList, index);
                break;
        
            default:
                System.out.println("failed to read: " + strList[0]);
                break;
        }
    }
    public void setWaterDensity(String str[], int index){
        double value;
        
        try{
            value= Double.valueOf(str[index]);
        }
        catch(Exception e){
            //System.out.println("failed to read double for value error msg: "+ e);
            return;
        }
        config.setWaterDensity(value);
    }

    public void activateGates(String str[], int index){
        String[] strList= str[index].split(",");

        for(String curStr: strList){
            int channel;
            PhidgetGate gate;
            
            try{
                channel= Integer.parseInt(curStr);
            }catch(Exception e){
                System.out.println("failed to read integer for channel ");
                continue;
            }
            
            
            try {
                gate=new PhidgetGate(channel);
            } catch (PhidgetException | LinkageError e) {
                System.out.println("Bridge "+ channel +" hardware not connected."+strList.length);
                gate=new PhidgetGate(channel, true);
            }

            config.addGate(gate);
        }
    }

    public void gateOperation(String[] str, int index){
        int channel;
        try{
            channel= Integer.parseInt(str[index]);
            index++;
        }
        catch(Exception e){
            return;
        }

        if(config.isActiveGate(channel)){
            switch (str[index].toUpperCase()) {
            case "SLOPE/OFFSET":
                index++;
                assignSlopeAndOffset(channel,str[index]);
                break;
            }
        }
    }

    public void assignSlopeAndOffset(int Channel, String str){
        String[] strList= str.split(",");
        int index=0;
        double slope,offset;
        try{
            slope= Double.valueOf(strList[index++]);
            offset= Double.valueOf(strList[index]);
            //System.out.println("slope: "+slope+" offset: "+ offset);
        }
        catch(Exception e){
            //System.out.println("failed to assign slope/ offset at:"+index+ " error msg: "+e);
            return;
        }
        config.setChannelCalibration(Channel,slope,offset);
    }
    
    public void sphereOperation(String[] str, int index){
        TestObject sphere=config.getTestObject("SPHERE");
        if(sphere==null){
            //System.out.println("no sphere found");
            return;
        }
        double value;
        index++;
        try{
            value= Double.valueOf(str[index]);
        }
        catch(Exception e){
            return;
        }
        index--;
        switch (str[index].toUpperCase()) {
        case "PROJECTED_AREA":
            sphere.setprojectedArea(value);
            break;
        
        case "ATTACHMENT_AREA":
            sphere.setAttachmentArea(value);
            break;
        
        case "CENTROID_HEIGHT":
            sphere.setCentroidHeight(value);
            break;
        
        case "DRAG_COEFFICIENT":
            sphere.setKnownDragCoefficient(value);
            break;
        }
        
    }
}
