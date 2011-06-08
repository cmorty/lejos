package lejos.robotics.mapping;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import lejos.geom.Line;
import lejos.geom.Point;
import lejos.geom.Rectangle;

import org.w3c.dom.Element;

public class SVGMapLoader2 {

	private List mapLines;
	private List points;
	
	private LineMap lm;
	private Line[] lines;
	private RangeMap map;
	
	public SVGMapLoader2(){
		//create a list to hold Line objects
		mapLines = new ArrayList();
		points = new ArrayList();
	}
	
	public void processXML(){
		try {
			File f = new File("Room2.svg");
			FileInputStream in;
	
				in = new FileInputStream(f);
	
			XMLInputFactory factory = XMLInputFactory.newInstance();
			XMLStreamReader parser = factory.createXMLStreamReader(in);
			int numAttrs;
			
			int x1 = 0;
			int y1 = 0;
			int x2 = 0;
			int y2 = 0;
			
			while (true) {
			    int event = parser.next();
			    if (event == XMLStreamConstants.END_DOCUMENT) {
			       parser.close();
			       System.out.println("END");
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
						mapLines.add(e);
			        }
			    }
			}		    
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//End process
		
		printData();
	}

	/**
	 * Iterate through the list and print the 
	 * content to console
	 */
	private void printData(){
		
		System.out.println("#1 Processing SVG File");
		
		System.out.println("No of Lines '" + mapLines.size() + "'.");
		
		Iterator it = mapLines.iterator();
		while(it.hasNext()) {
			Line l = (Line) it.next();
			
			System.out.println(l.getX1() + " " + l.getY1() + " " + l.getX2() + " " + l.getY2());
			
			//Add points to list
			points.add(l.getP1());
			points.add(l.getP2());
		}
		
		float[] arrX = new float[points.size()];
		float[] arrY = new float[points.size()];
		Iterator it2 = points.iterator();
		int i = 0;
		while(it2.hasNext()){
			Point p = (Point)it2.next();
			arrX[i] = (float) p.getX();
			arrY[i] = (float) p.getY();
			i++;
		}
		
		System.out.println("#2 Calculating bounds for map");
		//Sort
		//http://www.cs.ubc.ca/~harrison/Java/sorting-demo.html
		BubbleSort bs2 = new BubbleSort(arrX);
		float[] arrXsorted = bs2.sort();
		System.out.println("Min X1: " + arrXsorted[0]);
		
		float valueX1 = arrXsorted[0];
		float valueY1 = 0;
		
		int j = 0;
		Iterator it3 = points.iterator();
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
		
		System.out.println("Min Y1: " + valueY1);
		
		bs2 = new BubbleSort(arrY);
		float[] arrYsorted = bs2.sort();
		System.out.println("Max Y2: " + arrYsorted[arrYsorted.length-1]);
		
		float valueY2 = arrYsorted[arrYsorted.length-1];
		float valueX2 = 0;
		
		Iterator it4 = points.iterator();
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
		
		System.out.println("Max X2: " + valueX2);
		
		System.out.println("#3 Creating a LineMap & RangeMap");
				
		//Creating a LineMap & RangeMap
		int lineNumber = mapLines.size();
		lines = new Line[lineNumber];
		
		Iterator it5 = mapLines.iterator();
		j=0;
		while(it5.hasNext()){
			Line l = (Line) it5.next();
			
			lines[j] = l;
			j++;
		}
		Rectangle bound = new Rectangle(valueX1,valueY1,valueX2,valueY2);
		map = new LineMap(lines, bound); 
		lm = new LineMap(lines, bound);
		
		System.out.println("#3 Generating code to paste in projects");
		
		// Create a rudimentary map:
		/*
		33 	Line [] lines = new Line[3];
		34 	lines [0] = new Line(75f, 100f, 100f, 100f);
		35 	lines [1] = new Line(100, 100, 87, 75);
		36 	lines [2] = new Line(87, 75, 75, 100);
		37 	lejos.geom.Rectangle bounds = new Rectangle(-50, -50, 250, 250);
		38 	LineMap myMap = new LineMap(lines, bounds); 
		*/
		System.out.println("Line [] lines = new Line[" + lineNumber + "];");
		
		Iterator it6= mapLines.iterator();
		j=0;
		while(it6.hasNext()){
			Line l = (Line) it6.next();
			
			System.out.println("lines [" + j + "] = new Line(" + (int)l.getX1() + ", " + (int)l.getY1() + ", " + (int)l.getX2() + ", " + (int)l.getY2() + ");");
			j++;
		}
		System.out.println("lejos.geom.Rectangle bounds = new Rectangle(" + (int)valueX1 + "," + (int)valueY1 + ", " + (int)valueX2 + ", " + (int)valueY2 + ");");
		System.out.println("LineMap myMap = new LineMap(lines, bounds);");
		
		
		System.out.println("Done");
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		SVGMapLoader2 svg = new SVGMapLoader2();
		svg.processXML();
	}

}
