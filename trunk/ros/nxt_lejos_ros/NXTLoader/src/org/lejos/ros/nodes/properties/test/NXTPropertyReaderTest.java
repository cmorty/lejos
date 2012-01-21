package org.lejos.ros.nodes.properties.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * This example show how to use a property files in a ROS Node
 * 
 * @author jabrena
 *
 */
public class NXTPropertyReaderTest{
	
    public static void main( String[] args ){
    	
    	Properties prop = new Properties();
 
    	try {
            
    		final String path = "./src/test/resources/nxt/robots/examples/";
    		final String file = "NXTLoader.properties"; 
    		
    		//load a properties file
    		prop.load(new FileInputStream(path + file));
 
            //get the property value and print it out
            System.out.println(prop.getProperty("NXT-BRICK"));
    		System.out.println(prop.getProperty("CONNECTION-TYPE"));
    		System.out.println(prop.getProperty("YAML-ROBOT-DESCRIPTOR"));
 
    	} catch (IOException ex) {
    		ex.printStackTrace();
        }
 
    }
}