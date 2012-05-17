package org.lejos.ros.nodes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.xml.stream.XMLStreamException;

import lejos.geom.Line;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.mapping.SVGMapLoader;
import lejos.util.Delay;

import geometry_msgs.Point;
import visualization_msgs.Marker;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.parameter.ParameterTree;
import org.ros.node.topic.Publisher;

public class PublishMap implements NodeMain {
	private ParameterTree params;
	String mapFileName;
	Marker marker;
	long seq=0;

	@Override
	public void onShutdown(Node arg0) {
		// Do nothing
	}

	@Override
	public void onShutdownComplete(Node arg0) {
		// Do nothing	
	}

	@Override
	public void onStart(ConnectedNode node) {
		
		params = node.getParameterTree();
		
		mapFileName = params.getString("map_file_name");
		
		marker = node.getTopicMessageFactory().newFromType(Marker._TYPE);
				
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
					
			marker.getHeader().setFrameId("/world");
			marker.getHeader().setStamp(node.getCurrentTime());
			marker.setNs("line_map");
			marker.setId(0);
			marker.setType(Marker.LINE_LIST);
			marker.setAction(Marker.ADD);
			marker.getScale().setX(0.05);
			marker.getScale().setY(0.05);
			marker.getScale().setZ(0.05);
			marker.getColor().setR(0.0f);
			marker.getColor().setG(0.0f);
			marker.getColor().setB(0.0f);
			marker.getColor().setA(1.0f);
			
			for(int i=0;i<lines.length;i++) {
				Line line = lines[i];
				
				Point p1 = node.getTopicMessageFactory().newFromType(Point._TYPE);
				p1.setX(line.x1 / 100);
				p1.setY(line.y1 / 100);
				p1.setZ(0);
				
				marker.getPoints().add(p1);
				
				Point p2 = node.getTopicMessageFactory().newFromType(Point._TYPE);
				p2.setX(line.x2 / 100);
				p2.setY(line.y2 / 100);
				p2.setZ(0);
				
				marker.getPoints().add(p2);
				
			}
			 
			String messageType = "visualization_msgs/Marker";
		    Publisher<Marker> topic = node.newPublisher("map", messageType);
		    Delay.msDelay(1000);
		    topic.setLatchMode(true);
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
		return new GraphName("nxt_lejos/nxt_lejos_map_server");
	}

	@Override
	public void onError(Node arg0, Throwable arg1) {
		// Do nothing		
	}
}
