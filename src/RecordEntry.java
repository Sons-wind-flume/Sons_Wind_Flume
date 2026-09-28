import java.util.ArrayList;

public class RecordEntry {

    int timestamp;
    ArrayList<VoltageValue> recordedValues;

    public RecordEntry(int time, ArrayList<VoltageValue> list) {
        timestamp = time;
        recordedValues = list;
    }

    public String toString() {
        String text;
        VoltageValue currentValue;
        int index;

        text = "";
        text = text + timestamp + " ,";

        for (index = 0; index < recordedValues.size(); index = index + 1) {
            currentValue = recordedValues.get(index);
            text = text + " " + currentValue.toString();

            if (index != recordedValues.size() - 1) {
                text = text + " ,";
            }
        }

        return text;
    }
}
