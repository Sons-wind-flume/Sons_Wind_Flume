package org.windflume.importer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import org.windflume.config.Configuration;

public class Reader1 {
    public static Configuration config = null;
    public static void readFile(String filePath){
        try{
            
            ArrayList<String> lines = new ArrayList<>();
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String line;
            while((line = br.readLine())!=null){
                lines.add(line);
            }
            config = new Configuration();
            Scanner scan =new Scanner(config);
            for(String str: lines){
                scan.scan(str);
            }
        }
        catch(Exception e){
            config =Configuration.defaults();
            System.out.println("failed to open:"+filePath);
        }
    }
    public static Configuration getConfiguration(){
        return config;
    }
}
