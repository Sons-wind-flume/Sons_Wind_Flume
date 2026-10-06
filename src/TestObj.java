public class TestObj {
    private String testObjName;
    private float projectedArea,  attachmentArea, centroidHeight;
    TestObj(String name){
        testObjName=name;
        projectedArea=0;
        attachmentArea=0;
        centroidHeight=0;
    }
    public String getName(){
        return testObjName;
    }

    public void setProjectedArea(float area){
        projectedArea=area;
    }

    public void setAttachmentArea(float area){
        attachmentArea=area;
    }

    public void setCentroidHeight(float area){
        centroidHeight=area;
    }

    public float getProjectedArea(){
        return projectedArea;
    }

    public float getAttachmentArea(){
        return attachmentArea;
    }

    public float getCentroidHeight(){
        return centroidHeight;
    }

}
