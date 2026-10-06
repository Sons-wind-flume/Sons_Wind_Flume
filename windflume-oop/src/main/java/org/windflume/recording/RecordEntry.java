package org.windflume.recording;

import java.util.ArrayList;

public class RecordEntry {

    private final int timestamp;
    private final ArrayList<VoltageValue> recordedValues;

    public RecordEntry(
            int time,
            ArrayList<VoltageValue> list) {

        timestamp = time;
        recordedValues = list;
    }

    @Override
    public String toString() {

        String text = "";

        text += timestamp + " ,";

        for (int i = 0; i < recordedValues.size(); i++) {

            VoltageValue curValue =
                    recordedValues.get(i);

            text += " " + curValue.toString();

            if (i != recordedValues.size() - 1) {
                text += " ,";
            }
        }

        return text;
    }
}