package org.lejos.ros.nxt;

/**
 * 
 * @author jabrena
 *
 */
public class NXTDevice {

	//Topic data
	private String name;
	private float desiredFrequency;
	
	public NXTDevice(){
		
	}
	
	public void setName(String _name){
		name = _name;
	}
	
	public String getName(){
		return name;
	}
	
	public void setDesiredFrequency(final float df){
		desiredFrequency = df;
	}
	
	public float getDesiredFrequency(){
		return desiredFrequency;
	}
}
