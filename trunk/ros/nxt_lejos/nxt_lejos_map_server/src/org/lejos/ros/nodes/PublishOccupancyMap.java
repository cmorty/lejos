package org.lejos.ros.nodes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.xml.stream.XMLStreamException;

import lejos.geom.Line;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.mapping.SVGMapLoader;
import lejos.util.Delay;

import nav_msgs.OccupancyGrid;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.parameter.ParameterTree;
import org.ros.node.topic.Publisher;

public class PublishOccupancyMap implements NodeMain {
	private ParameterTree params;
	String mapFileName;
	OccupancyGrid grid;
	float resolution = 0.1f;
	int width = 65;
	int height = 60;

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
		
		grid = node.getTopicMessageFactory().newFromType(OccupancyGrid._TYPE);
				
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
			
			width = (int) Math.ceil((map.getBoundingRect().width + map.getBoundingRect().x) / (100 * resolution)) + 1;
			height = (int) Math.ceil((map.getBoundingRect().height  + map.getBoundingRect().y) / (100 * resolution)) + 1;
			
			//System.out.println("Width = " + width + ", Height = " + height);
			//System.out.println("Origin x = " + map.getBoundingRect().x);
			//System.out.println("Bounding rect width = " + map.getBoundingRect().width);
			
			grid.getHeader().setFrameId("/world");
			grid.getHeader().setStamp(node.getCurrentTime());
			
			grid.getInfo().setMapLoadTime(node.getCurrentTime());
			grid.getInfo().setHeight(height);
			grid.getInfo().setWidth(width);
			grid.getInfo().setResolution(resolution);
			
			byte[] data = new byte[width * height];
			
			// Set all data to unknown
			for(byte b: data) {
				b = -1;
			}
			
			int n;
			for(int i=0;i<lines.length;i++) {
				Line line = lines[i];
				float x1 = line.x1;
				float x2 = line.x2;
				float y1 = line.y1;
				float y2 = line.y2;
				float inc = resolution * 100f;
				//System.out.println("Line from (" + x1 + "," + y1 + ") to (" + x2 + "," + y2 + ")");
				//boolean diagonal = Math.abs(x1-x2) > 0 && Math.abs(y2-y1) > 0;
				//if (diagonal) System.out.println("diagonal");
				if (Math.abs(x2 - x1) > 0) {
					if (x1 > x2) {
						x1 = line.x2;
						y1 = line.y2;
						x2 = line.x1;
						y2 = line.y1;
					} 
					for(float x = x1; x <= x2; x+=inc) {
						float y= y1 + ((y2 - y1) * ((x - x1)/(x2 - x1)));
						//if (diagonal) System.out.println("x = " + x + ", y = " + y);
						int ix = (int) (x/(100 * resolution));
						int iy = (int) (y/(100 * resolution));
						if (ix < width && iy < height) {
							n = (iy * width) + ix;
							//if (diagonal) System.out.println("By X ix = " + ix + ", iy = " + iy + ", n =" + n);
							data[n] = 100;
						}
					}
				} else {
					if (y1 > y2) {
						x1 = line.x2;
						y1 = line.y2;
						x2 = line.x1;
						y2 = line.y1;
					}
					for(float y = y1; y <= y2; y+=inc) {
						float x = x1 + ((x2 - x1) * ((y - y1)/(y2 - y1)));
						//System.out.println("x = " + x + ", y = " + y);
						int ix = (int) (x/(100 * resolution));
						int iy = (int) (y/(100 * resolution));
						if (ix < width && iy < height) {
							n = (iy * width) + ix;
							//if (diagonal) System.out.println("By Y ix = " + ix + ", iy = " + iy + ", n =" + n);
							data[n] = 100;
						}
					}
				}
			}
			
			grid.setData(data);
			 
			String messageType = "nav_msgs/OccupancyGrid";
		    Publisher<OccupancyGrid> topic = node.newPublisher("map", messageType);
		    Delay.msDelay(1000);
		    topic.setLatchMode(true);
		    topic.publish(grid);
		    
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
