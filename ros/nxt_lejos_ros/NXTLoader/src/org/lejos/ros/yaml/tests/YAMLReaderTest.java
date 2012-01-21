package org.lejos.ros.yaml.tests;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.esotericsoftware.yamlbeans.YamlReader;


public class YAMLReaderTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {

		final String path = "src/test/resources/nxt/robots/examples/";
		String yaml = "NXT.yaml";
		
		//Read a YAML File
        YamlReader reader = new YamlReader(new FileReader(path+yaml));
        Object object = reader.read();
        Map map = (Map)object;
        //System.out.println(map.size());
        
        //Get component list for NXT Robot
        List list =  (List) map.get("nxt_robot");
        //System.out.println(list.size());
        
        int i = 0;
        for (Object obj : list) {
        	//System.out.println(list.get(i).toString());
        	Map map2 = (Map) list.get(i);
        	//System.out.println(map2.get("type"));
        	
        	String type = map2.get("type").toString().trim(); 

        	//Actuators
        	if(type.equals("motor")){
        		System.out.println("* I found a motor description");

        		String name = map2.get("name").toString().trim();
        		String port = map2.get("port").toString().trim();
        		String desiredFrequency = map2.get("desired_frequency").toString().trim();
        				        		
        		System.out.println(name);
        		System.out.println(port);
        		System.out.println(desiredFrequency);
        		
        	//Sensors
        	}else{
        		System.out.println("* I found a sensor description");
        		System.out.println(map2.get("port"));
        	}
        	
        	i++;
        }
        
        System.out.println("END");
 
	}

}
