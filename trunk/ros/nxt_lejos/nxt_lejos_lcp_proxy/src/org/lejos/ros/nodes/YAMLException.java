package org.lejos.ros.nodes;

public class YAMLException extends Exception{

	  String error;

	  public YAMLException(){
		  super();// call superclass constructor
		  error = "unknown";
	  }
	  
	  public YAMLException(String err){
	    super(err);     // call super class constructor
	    error = err;  // save message
	  }

	  public String getError(){
	    return error;
	  }
}
	  
