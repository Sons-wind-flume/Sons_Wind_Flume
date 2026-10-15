package org.windflume.config;

public class TestObject {

    private final String name;
    private double projectedArea;
    private double attachmentArea;
    private double centroidHeight;
    private double knownDragCoefficient = 0;
    private boolean reference = false;
    private String modelId = "";
    private String orientation = "";

    public TestObject(String name, double projectedArea) {
        this.name = name;
        this.projectedArea = projectedArea;
    }

    public String getName()              { return name; }
    public double getProjectedArea()    { return projectedArea; }
    public double getAttachmentArea()   { return attachmentArea; }
    public double getCentroidHeight()   { return centroidHeight; }
    public boolean isReference()       { return reference; }
    public String getModelId()        { return modelId; }
    public String getOrientation()     { return orientation; }
    public double getKnownDragCoefficient() { return knownDragCoefficient; }

    public void setprojectedArea(double area)  { projectedArea = area; }
    public void setAttachmentArea(double area)  { attachmentArea = area; }
    public void setCentroidHeight(double h)     { centroidHeight = h; }
    public void setReference(boolean value)     { reference = value; }
    public void setModelId(String value)        { modelId = value; }
    public void setOrientation(String value)    { orientation = value; }
    public void setKnownDragCoefficient(double v) { knownDragCoefficient = v; }

    @Override
    public String toString() { return name; }
    public String toStringExport(){
        String text="";
        text+=""+getProjectedArea()+",="+getAttachmentArea()+",="+ getCentroidHeight()+",="+getKnownDragCoefficient();
        return text;
    }
}
