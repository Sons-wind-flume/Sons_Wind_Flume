package org.windflume.recording;
import org.windflume.config.TestObject;
import org.windflume.config.Configuration;
import java.text.DecimalFormat;

public class VoltageValue {

    private final DecimalFormat df =
            new DecimalFormat("0.00000");

    public double voltage;
    public int channelNum;
    public TestObject obj;
    public Configuration config;

    public VoltageValue(double vol, int channelNum,Configuration conf,TestObject object) {
        this.channelNum=channelNum;
        config=conf;
        voltage = vol;
    }
    public double calculateForce(){
        return voltage*config.getSlope(channelNum)+config.getOffset(channelNum);
    }
    public double getVoltage(){
        return voltage;
    }
    @Override
    public String toString() {

        return df.format(voltage);
    }
}