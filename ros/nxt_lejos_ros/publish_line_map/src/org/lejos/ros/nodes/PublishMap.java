package org.lejos.ros.nodes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.xml.stream.XMLStreamException;

import lejos.geom.Line;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.mapping.SVGMapLoader;
import lejos.util.Delay;

import org.ros.message.geometry_msgs.Point;
import org.ros.message.visualization_msgs.Marker;
import org.ros.namespace.GraphName;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;

public class PublishMap implements NodeMain {	
	String mapFileName = "Floor.svg";
	Marker marker = new Marker();
	long seq=0;

	@Override
	public void onShutdown(Node arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onShutdownComplete(Node arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStart(Node node) {
		File mapFile = new File(mapFileName);
		if (!mapFile.exists()) {
			String abs = mapFile.getAbsolutePath();
			// Try with .svg suffix
			mapFile = new File(mapFileName + ".svg");
			if (!mapFile.exists()) {
				System.err.println(abs + " does not exist");
				System.exit(1);
			}
		}
		try {
			FileInputStream is = new FileInputStream(mapFile);
			SVGMapLoader mapLoader = new SVGMapLoader(is);
			LineMap map = mapLoader.readLineMap();
			Line[] lines = map.getLines();
			for(int i=0;i<lines.length;i++) {
				Line line = lines[i];
				
				marker.header.seq = seq++;
				marker.header.frame_id = "/world";
				marker.header.stamp = node.getCurrentTime();
				marker.ns = "line_map";
				marker.id = 0;
				marker.type = Marker.LINE_LIST;
				marker.action = Marker.ADD;
				marker.scale.x = 0.05;
				marker.scale.y = 0.05;
				marker.scale.z = 0.05;
				marker.color.r = 1.0f;
				marker.color.g = 0.0f;
				marker.color.b = 1.0f;
				marker.color.a = 1.0f;
				
				Point p1 = new Point();
				p1.x = line.x1 / 100;
				p1.y = line.y1 / 100;
				p1.z = 0;
				
				marker.points.add(p1);
				
				Point p2 = new Point();
				p2.x = line.x2 / 100;
				p2.y = line.y2 / 100;
				p2.z = 0;
				
				marker.points.add(p2);
				
			}
			 
			String messageType = "visualization_msgs/Marker";
		    Publisher<Marker> topic = node.newPublisher("map", messageType);
		    Delay.msDelay(1000);
		    topic.publish(marker);
		    
		} catch (FileNotFoundException e1) {
			System.err.println("File not found");
			System.exit(1);
		} catch (XMLStreamException e2) {
			System.err.println("Error loading map file");
			System.exit(1);
		}
		System.out.println("Map published OK");
	}

	@Override
	public GraphName getDefaultNodeName() {
		// TODO Auto-generated method stub
		return null;
	}

}
