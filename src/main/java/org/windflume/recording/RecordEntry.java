package org.windflume.recording;
import org.windflume.config.Configuration;

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

    @Override
    public String toString() {

        String text = "";

        text += timestamp + " ,";
        //for
        for (int i = 0; i < recordedValues.size(); i++) {

            VoltageValue curValue =
                    recordedValues.get(i);

            text += " " + curValue.toString();
            text += " ,";
        }
        for (int i = 0; i < recordedValues.size(); i++) {

            VoltageValue curValue =
                    recordedValues.get(i);

            text += " " + curValue.calculateForce();
            text += " ,";

            
        }

        return text;
    }
}