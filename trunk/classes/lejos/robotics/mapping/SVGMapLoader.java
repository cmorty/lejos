package lejos.robotics.mapping;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import lejos.geom.Line;
import lejos.geom.Point;
import lejos.geom.Rectangle;
import lejos.util.BubbleSort;
import lejos.robotics.mapping.LineMap;

/**
 * 
 * This class has beend designed to generate LineMaps using maps designed 
 * in SVG format.
 * 
 * @author Juan Antonio Brenha Moral
 *
 */
public class SVGMapLoader {

	private Line[] lines;
	
	DataInputStream data_is = null;
	
	public SVGMapLoader(InputStream in) {
		this.data_is = new DataInputStream(in);
	}
	
	/**
	 * Public method designed to return a LineMap Object	 *
	 * 
	 * @return
	 * @throws IOException
	 */
	public LineMap readLineMap() throws IOException, XMLStreamException {
		
		//1. process XML file
		ArrayList <Line> mapLines;
		mapLines = processXML();
		
		//2. Generate a linemap
		LineMap lm = null;
		lm = generateLineMap(mapLines);
		
		return lm;
		
	}
	
	/**
	 * This method process XML file and load a data structure with information
	 * about lines stored in SVG file.
	 */
	private ArrayList <Line> processXML(){
		
		ArrayList <Line> lines;
		lines = new ArrayList<Line>();
		
		try {
			XMLInputFactory factory = XMLInputFactory.newInstance();
			XMLStreamReader parser = factory.createXMLStreamReader(data_is);
			int numAttrs;
			
			int x1 = 0;
			int y1 = 0;
			int x2 = 0;
			int y2 = 0;
			
			while (true) {
			    int event = parser.next();
			    if (event == XMLStreamConstants.END_DOCUMENT) {
			       parser.close();
			       //System.out.println("END");
			       break;
			    }
			    if (event == XMLStreamConstants.START_ELEMENT) {
			    	numAttrs = parser.getAttributeCount();
			        //System.out.println("Node: " + parser.getLocalName());
			        
			        String nodeName = parser.getLocalName();
			        
			        if(nodeName.equals("line")){
			        
				    	for(int i=0;i<numAttrs;i++) {
				    		String attributeName = parser.getAttributeLocalName(i);
				    		
				    		if(attributeName.equals("x1")){
				    			x1 = Integer.parseInt(parser.getAttributeValue(i));
				    		}
	
				    		if(attributeName.equals("y1")){
				    			y1 = Integer.parseInt(parser.getAttributeValue(i));
				    		}
	
				    		if(attributeName.equals("x2")){
				    			x2 = Integer.parseInt(parser.getAttributeValue(i));
				    		}
	
				    		if(attributeName.equals("y2")){
				    			y2 = Integer.parseInt(parser.getAttributeValue(i));
				    		}
				    		
				    		//System.out.println("Attribute(" + parser.getAttributeType(i) + "):" + parser.getAttributeLocalName(i) + "=" + parser.getAttributeValue(i));
				    	}
	
			    		Line e = new Line(x1,y1,x2,y2);
						//add it to list
			    		lines.add(e);
			        }
			    }
			}
		} catch (XMLStreamException e) {
			System.out.println(e.getMessage());
		}
		
		return lines;
	}
	
	/**
	 * This method extracts points from Lines
	 * 
	 * @return
	 */
	private ArrayList <Point> extractPoints(ArrayList <Line> mapLines){
		
		ArrayList <Point> points;
		points = new ArrayList<Point>();
		
		Iterator<Line> it = mapLines.iterator();
		while(it.hasNext()) {
			Line l = (Line) it.next();
			
			//System.out.println(l.getX1() + " " + l.getY1() + " " + l.getX2() + " " + l.getY2());
			
			//Add points to list
			points.add(l.getP1());
			points.add(l.getP2());
		}
		
		return points;
	}
	
	/**
	 * This method is used to generate a bound for future LineMap
	 * 
	 * @param points
	 * @return
	 */
	private Rectangle getBound(ArrayList <Point> points){
		
		Rectangle bound = null;
		
		//1. Extract X,Y data from Points
		float[] arrX = new float[points.size()];
		float[] arrY = new float[points.size()];
		Iterator<Point> it2 = points.iterator();
		int i = 0;
		while(it2.hasNext()){
			Point p = (Point)it2.next();
			arrX[i] = (float) p.getX();
			arrY[i] = (float) p.getY();
			i++;
		}
		
		//2. Sort arrays to get minimum and maximum values to generate the area
		//A rectangle is generated with the minimum point and maximum point
		BubbleSort bs2 = new BubbleSort(arrX);
		float[] arrXsorted = bs2.sort();
		//System.out.println("Min X1: " + arrXsorted[0]);
		
		//Once you have sorted the array, the first element, is the minimum, x1
		float valueX1 = arrXsorted[0];
		float valueY1 = 0;
		
		//To calculate y2, we will try to find the first element which x1 = element analyzed.
		int j = 0;
		Iterator <Point> it3 = points.iterator();
		while(it3.hasNext()){
			Point p = (Point)it3.next();
			float x = (float) p.getX();
			float y = (float) p.getY();
			if(x == valueX1){
				j++;
				//System.out.println("Iter: " + j+ " x1 " + valueX1 + " tx1 " + x + " y1 " + y);
				valueY1 = y;
				break;
			}
		}

		//JAB: 13/06/2011
		//What happen if 2 or more points has the same x1?
		//Maybe it is necessary to reinforce the algorithm to cover this scenarios. 
		
		//System.out.println("Min Y1: " + valueY1);
		
		//Once system has the minimum point then it is necessary to calculate the
		//another point to generate a rectangle.
		
		bs2 = new BubbleSort(arrY);
		float[] arrYsorted = bs2.sort();
		//System.out.println("Max Y2: " + arrYsorted[arrYsorted.length-1]);
		
		//In this case, we use the y axis to define the maximum element.
		float valueY2 = arrYsorted[arrYsorted.length-1];
		float valueX2 = 0;
		
		Iterator <Point> it4 = points.iterator();
		j=0;
		while(it4.hasNext()){
			Point p = (Point)it4.next();
			float x = (float) p.getX();
			float y = (float) p.getY();
			if(y == valueY2){
				j++;
				//System.out.println("Iter: " + j+ " x1 " + valueY2 + " tx1 " + x + " y1 " + y);

				valueX2 = x;
				break;
			}
		}
		
		//System.out.println("Max X2: " + valueX2);
		
		bound = new Rectangle(valueX1,valueY1,valueX2,valueY2);
		
		return bound;
	}
	
	/**
	 * This method generate a LineMap using data Lines from SVG File.
	 * The method calculate the bound of the new map automatically.
	 * 
	 * @return
	 */
	private LineMap generateLineMap(ArrayList <Line> mapLines){
		
		LineMap lm = null;
		
		//Extract points from lines
		ArrayList <Point> points;
		points = this.extractPoints(mapLines);
	
		Rectangle bound = this.getBound(points);

		//Creating a LineMap & RangeMap
		int lineNumber = mapLines.size();
		lines = new Line[lineNumber];
		
		Iterator <Line> it5 = mapLines.iterator();
		int j=0;
		while(it5.hasNext()){
			Line l = (Line) it5.next();
			
			lines[j] = l;
			j++;
		}
		
		lm = new LineMap(lines, bound);
		
		return lm;
	}
}
