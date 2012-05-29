package org.lejos.sample.svgmltest;
import java.io.File;
import java.io.FileInputStream;

import lejos.geom.Line;
import lejos.geom.Rectangle;
import lejos.nxt.Button;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.mapping.SVGMapLoader;

/**
 * This example has been designed to test the new way to load LineMaps created
 * in SVG Format.
 * 
 * Create a file "Room.svg" with the following XML Code:
 * 
 * 	<?xml version="1.0"?>
	<svg width="640" height="480" xmlns="http://www.w3.org/2000/svg">
	 <!-- Created with SVG-edit - http://svg-edit.googlecode.com/ -->
	 <g>
	  <title>Layer 1</title>
	  <line id="svg_1" y2="0" x2="230" y1="0" x1="0" stroke-width="5" stroke="#000000" fill="none"/>
	  <line fill="none" stroke="#000000" stroke-width="5" x1="230" y1="0" x2="230" y2="28" id="svg_2"/>
	  <line fill="none" stroke="#000000" stroke-width="5" x1="230" y1="28" x2="252" y2="28" id="svg_3"/>
	  <line fill="none" stroke="#000000" stroke-width="5" x1="252" y1="28" x2="252" y2="320" id="svg_4"/>
	  <line id="svg_5" y2="320" x2="0" y1="320" x1="252" stroke-width="5" stroke="#000000" fill="none"/>
	  <line fill="none" stroke="#000000" stroke-width="5" x1="0" y1="320" x2="0" y2="0" id="svg_6"/>
	 </g>
	</svg>
 * 
 * @author Juan Antonio Brenha Moral
 *
 */
public class SVGMLTest {

	public static void main(String[] args) throws Exception {
		
		System.out.println("1. Loading Map in SVG Format");
		File f = new File("Room.svg");
		FileInputStream in = new FileInputStream(f);
		SVGMapLoader svgml = new SVGMapLoader(in);
		LineMap lm = svgml.readLineMap();
		
		System.out.println("2. Testing output:");
		
		System.out.println("[Bound]:");
		Rectangle rect = lm.getBoundingRect();
		System.out.println("Min X: " + rect.getMinX());
		System.out.println("Min Y: " + rect.getMinY());
		System.out.println("Max X: " + rect.getMaxX());
		System.out.println("Max Y: " + rect.getMaxY());
		
		System.out.println("[Lines]:");
		Line[] lines = lm.getLines();
		
		for(int i=0; i<lines.length;i++){
			Line l = lines[i];
			System.out.println("Line " + i + ": " + l.getX1() + " " + l.getY1() + " " + l.getX2() + " " + l.getY2());
		}
		
		Button.waitForAnyPress();
		
		//Once you have loaded the map, robot is able to navigate 
		//with that map or try lo localize using MCL
	}

}
