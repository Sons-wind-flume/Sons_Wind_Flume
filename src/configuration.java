import java.util.ArrayList;
public class configuration {
    ArrayList<phidgetGate> activeGates;
    ArrayList<TestObj> testObjects;

    float dragCoefficent, waterDensity;

    public configuration() {
        activeGates=new ArrayList<>();
        testObjects=new ArrayList<>();
        dragCoefficent=0;
        waterDensity=0;
    }

    public void addTestObject(TestObj obj){
        testObjects.add(obj);
    }

    public void addActiveGate(phidgetGate gate){
        activeGates.add(gate);
    }
    
    public float getWaterDensity(){
        return waterDensity;
        
    }

    public float getDragCoefficent(){
        return dragCoefficent;
    }

    public void setWaterDensity(float value){
        waterDensity=value;
        
    }

    public void setDragCoefficent(float value){
        dragCoefficent=value;
    }

    public ArrayList<phidgetGate> getGates(){
        return activeGates;
        
    }

    public ArrayList<TestObj> getTestObjects(){
        return testObjects;
    }

    public TestObj getTestObject(String name){
        for (TestObj elem : testObjects) {
            if(elem.getName().toLowerCase().compareTo(name.toLowerCase())==0){
                return elem;
            }
        }
        return null;
    }

    public phidgetGate getGate(int ChannelNum){
        for (phidgetGate elem : activeGates) {
            if(elem.getChannel()==ChannelNum){
                return elem;
            }
        }
        return null;
    }
    

    

}
