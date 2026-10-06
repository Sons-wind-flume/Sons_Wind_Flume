package org.windflume.config;

public class TestObject {

    private final String name;
    private final double projectedArea;
    private double attachmentArea;
    private double centroidHeight;

    public TestObject(String name, double projectedArea){
        this.name = name;
        this.projectedArea = projectedArea;
    }

    public String getName(){
        return name;
    }
    public double getProjectedArea(){
        return projectedArea;
    }

    public double getAttachmentArea(){
        return attachmentArea;
    }

    public double getCentroidHeight(){
        return centroidHeight;
    }

    public void setAttachmentArea(double area){
        attachmentArea = area;
    }

    public void setCentroidHeight(double h){
        centroidHeight = h;
    }

}
    
