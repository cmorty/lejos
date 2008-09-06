//import java.util.*;
package java.util;

/**
 * This class has been developed to parse strings with delimiters
 * 
 * @author Juan Antonio Brenha Moral
 */
public class StringTokenizer implements Enumeration{
	/**
	 * Delimiter string.
	 */
	private String delimiter = ",";
	private int total;
	private int currentPosition;
	private int nextPosition;
	private String s;

	/**
	 * The constructor
	 * 
	 * @param s String to be StringTokenizer
	 */
	public StringTokenizer(String s){
		String character = (String)s.substring(s.length()-1);
		if(character != delimiter){
			s += delimiter;
		}

		this.s = s;
		this.total = s.length();
		this.currentPosition = 0;
		this.nextPosition = s.indexOf(delimiter, currentPosition);
	}

	/**
	 * The constructor
	 * 
	 * @param s
	 * @param _delimiter
	 */
	public StringTokenizer(String s, String _delimiter) {
		String character = (String)s.substring(s.length()-1);
		if(character != _delimiter){
			s += _delimiter;
		}
		
		this.delimiter = _delimiter;
		this.s = s;
		this.total = s.length();
		this.currentPosition = 0;
		this.nextPosition = s.indexOf(delimiter, currentPosition);
	}

	/**
	 * Method used to know if exists new tokens
	 * 
	 * @return
	 */
	public boolean hasMoreTokens(){
		return ((nextPosition != -1) && (currentPosition <= nextPosition));
	}
	
	/**
	 * Method implemented by interface requirement
	 */
	public boolean hasMoreElements() {
		return hasMoreTokens();
	}
	
	/**
	 * 
	 * @return Next token
	 * @throws NoSuchElementException If there is no token left
	 */
	public String nextToken() throws NoSuchElementException{// 
		if (!hasMoreElements()){
			throw new NoSuchElementException();
		}

		String next = s.substring(currentPosition, nextPosition);

		currentPosition = nextPosition + 1;
		nextPosition = s.indexOf(delimiter, currentPosition);

		return next;
	}

	/**
	 * Method implemented by interface requirement
	 */
	public Object nextElement(){
		Object ne = null;
		try{
			ne = nextToken();
		}catch(NoSuchElementException e){
			
		}
		return ne;
	}
}