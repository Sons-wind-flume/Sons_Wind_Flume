package org.windflume.importer;

import org.windflume.config.Configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Reader {
    public static Configuration config = null;
    public static void readFile(String filePath){
        
        try{
            ArrayList<String> lines = new ArrayList<>();
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String line;
            while((line = br.readLine())!=null){
                lines.add(line);
            }
        }
        catch(Exception e){
            config =Configuration.defaults();
        }
    
    }
}
