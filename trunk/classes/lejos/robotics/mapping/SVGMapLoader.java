package lejos.robotics.mapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import lejos.geom.Line;
import lejos.geom.Point;
import lejos.geom.Rectangle;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This class is a experimental work to develop a solution to load maps 
 * modeled with in SVG format.
 * 
 * @author Juan Antonio Brenha Moral
 *
 */
public class SVGMapLoader {

	//No generics
	private List mapLines;
	private List points;
	private Document dom;
	
	private LineMap lm;
	private Line[] lines;
	private RangeMap map;

	public SVGMapLoader(){
		//create a list to hold Line objects
		mapLines = new ArrayList();
		points = new ArrayList();
	}

	public void process() {
		
		//parse the xml file and get the dom object
		parseXmlFile();
		
		//get each line element and create a Line object
		parseDocument();
		
		//Iterate through the list and print the data
		printData();
		
	}
	
	
	private void parseXmlFile(){
		//get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		try {
			
			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			//parse using builder to get DOM representation of the XML file
			dom = db.parse("Room.svg");
			

		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}

	
	private void parseDocument(){
		//get the root elememt
		Element docEle = dom.getDocumentElement();
		
		//get a nodelist of <Line> elements
		NodeList nl = docEle.getElementsByTagName("line");
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0 ; i < nl.getLength();i++) {
				
				//get the line element
				Element el = (Element)nl.item(i);
				
				//get the Line object
				Line e = getLine(el);
				
				//add it to list
				mapLines.add(e);
			}
		}
	}


	/**
	 * I take an line element and read the values in, create
	 * an Line object and return it
	 * @param mapLine
	 * @return
	 */
	private Line getLine(Element lineEl) {

		int x1 = Integer.parseInt(lineEl.getAttributeNode("x1").getValue());
		int y1 = Integer.parseInt(lineEl.getAttributeNode("y1").getValue());
		int x2 = Integer.parseInt(lineEl.getAttributeNode("x2").getValue());
		int y2 = Integer.parseInt(lineEl.getAttributeNode("y2").getValue());
		
		//Create a new Employee with the value read from the xml nodes
		Line e = new Line(x1,y1,x2,y2);
		
		return e;
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
		
		System.out.println("Done");
	}
	
	public LineMap getLineMap(){
		return lm;
	}
	
	public RangeMap getRangeMap(){
		return map;
	}
	
	public static void main(String[] args){
		//create an instance
		SVGMapLoader dpe = new SVGMapLoader();
		
		//call run example
		dpe.process();
	}

}
