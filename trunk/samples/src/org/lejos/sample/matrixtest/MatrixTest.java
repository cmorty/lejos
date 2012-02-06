package org.lejos.sample.matrixtest;
import lejos.util.DebugMessages;
import lejos.util.Matrix;

/**
 * Example created to test Matrix Class
 * 
 * @author Juan Antonio Brenha Moral
 *
 */

public class MatrixTest{
	private static DebugMessages dm;
	
	//Main
	public static void main(String[] args) throws Exception{
		dm = new DebugMessages();
		dm.setLCDLines(6);
		dm.echo("Testing Matrix Class");
		
		double[][] array = {{1.,2.,3},{4.,5.,6.},{7.,8.,10.}};
		Matrix a = new Matrix(array);
		
		Matrix b = Matrix.random(3,1);
		dm.echo("dim(b) = "+b.getRowDimension()+"x"+b.getColumnDimension());
		
		Matrix c = a.times(b);
		dm.echo("dim(c) = "+c.getRowDimension()+"x"+c.getColumnDimension());
		
		dm.echo("Test finished");
		Thread.sleep(5000);
	}
}
