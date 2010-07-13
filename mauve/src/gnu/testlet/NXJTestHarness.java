// Copyright (c) 1998, 1999, 2001, 2003  Red Hat, Inc.
// Written by Tom Tromey <tromey@cygnus.com>

// This file is part of Mauve.

// Mauve is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2, or (at your option)
// any later version.

// Mauve is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License
// along with Mauve; see the file COPYING.  If not, write to
// the Free Software Foundation, 59 Temple Place - Suite 330,
// Boston, MA 02111-1307, USA.

// KNOWN BUGS:
//   - should look for /*{ ... }*/ and treat contents as expected
//     output of test.  In this case we should redirect System.out
//     to a temp file we create.

package gnu.testlet;
import java.io.PrintStream;
import lejos.nxt.comm.RConsole;

public class NXJTestHarness extends TestHarness
{
  private int failures = 0, passes = 0;
  private boolean verbose = false;
  private boolean debug = false;

  protected int getFailures() {
    return failures;
  }

  public void check (boolean result)
  {
	  if (result) passes++; else failures++;
	  System.out.println(result ? "Pass" : "Fail");
  }

  public void checkPoint (String name)
  {
  }
  
  public void verbose (String message)
  {
    if (verbose)
      System.out.println(message);
  }
  
  public void debug (String message)
  {
    debug(message, true);
  }
  
  public void debug (String message, boolean newline)
  {
    if (debug)
      {
	if (newline)
	  System.out.println(message);
	else
	  System.out.print(message);
      }
  }
  
  public void debug (Throwable ex)
  {
    if (debug)
      System.out.println(ex.getMessage());
  }

  
  protected void runtest (String name, Testlet t)
  {
    // Try to ensure we start off with a reasonably clean slate.
    System.gc();
    
    System.out.println(name);
    
    t.test(this);
  }
  
  protected int done ()
  {
	  return 0; 
  }
  
  public int getPasses() {
	  return passes;
  }

  public static void main (String[] args)
  {
	  RConsole.openBluetooth(0);
	  System.setOut(new PrintStream(RConsole.getPrintStream()));
	  NXJTestHarness harness = new NXJTestHarness();
	  
	  // Run the tests
	  
	  // Line2D
	  
	  harness.runtest("Line2D clone", new gnu.testlet.java.awt.geom.Line2D.clone());
	  harness.runtest("Line2D contains", new gnu.testlet.java.awt.geom.Line2D.contains());
	  harness.runtest("Line2D equals", new gnu.testlet.java.awt.geom.Line2D.equals());
	  harness.runtest("Line2D getBounds", new gnu.testlet.java.awt.geom.Line2D.getBounds());
	  harness.runtest("Line2D getP1", new gnu.testlet.java.awt.geom.Line2D.getP1());
	  harness.runtest("Line2D getP2", new gnu.testlet.java.awt.geom.Line2D.getP1());
	  harness.runtest("Line2D intersects", new gnu.testlet.java.awt.geom.Line2D.intersects());
	  harness.runtest("Line2D intersectsLine", new gnu.testlet.java.awt.geom.Line2D.intersectsLine());
	  harness.runtest("Line2D relativeCCW", new gnu.testlet.java.awt.geom.Line2D.relativeCCW());
	  harness.runtest("Line2D setLine", new gnu.testlet.java.awt.geom.Line2D.setLine());
	  
	  // Rectangle2D
	  
	  harness.runtest("Rectangle2D constants", new gnu.testlet.java.awt.geom.Rectangle2D.constants());
	  harness.runtest("Rectangle2D contains", new gnu.testlet.java.awt.geom.Rectangle2D.contains());
	  harness.runtest("Rectangle2D equals", new gnu.testlet.java.awt.geom.Rectangle2D.equals());
	  harness.runtest("Rectangle2D getBounds2D", new gnu.testlet.java.awt.geom.Rectangle2D.getBounds2D());
	  harness.runtest("Rectangle2D intersects", new gnu.testlet.java.awt.geom.Rectangle2D.intersects());
	  harness.runtest("Rectangle2D setFrame", new gnu.testlet.java.awt.geom.Rectangle2D.setFrame());
	  
	  // Rectangle2D.Double
	  
	  harness.runtest("Rectangle2D.Double clone", new gnu.testlet.java.awt.geom.Rectangle2D.Double.clone());
	  harness.runtest("Rectangle2D.Double isEmpty", new gnu.testlet.java.awt.geom.Rectangle2D.Double.clone());
	  harness.runtest("Rectangle2D.Double outcode", new gnu.testlet.java.awt.geom.Rectangle2D.Double.outcode());
	  harness.runtest("Rectangle2D.Double setRect", new gnu.testlet.java.awt.geom.Rectangle2D.Double.setRect());
	  
	  // Rectangle2D.Float
	  
	  harness.runtest("Rectangle2D.Float clone", new gnu.testlet.java.awt.geom.Rectangle2D.Float.clone());
	  harness.runtest("Rectangle2D.Float isEmpty", new gnu.testlet.java.awt.geom.Rectangle2D.Float.clone());
	  harness.runtest("Rectangle2D.Float outcode", new gnu.testlet.java.awt.geom.Rectangle2D.Float.outcode());
	  harness.runtest("Rectangle2D.Float setRect", new gnu.testlet.java.awt.geom.Rectangle2D.Float.setRect());
	  
	  // Point
	  
	  harness.runtest("Point clone", new gnu.testlet.java.awt.Point.clone());
	  harness.runtest("Point constructors", new gnu.testlet.java.awt.Point.constructors());
	  harness.runtest("Point equals", new gnu.testlet.java.awt.Point.equals());
	  harness.runtest("Point getLocation", new gnu.testlet.java.awt.Point.getLocation());
	  harness.runtest("Point move", new gnu.testlet.java.awt.Point.move());
	  harness.runtest("Point setLocation", new gnu.testlet.java.awt.Point.setLocation());
	  harness.runtest("Point translate", new gnu.testlet.java.awt.Point.translate());
	  
	  // Rectangle
	  
	  harness.runtest("Rectangle clone", new gnu.testlet.java.awt.Rectangle.clone());
	  harness.runtest("Rectangle contains", new gnu.testlet.java.awt.Rectangle.contains());
	  harness.runtest("Rectangle equals", new gnu.testlet.java.awt.Rectangle.equals());
	  harness.runtest("Rectangle intersects", new gnu.testlet.java.awt.Rectangle.intersects());
	  harness.runtest("Rectangle isEmpty", new gnu.testlet.java.awt.Rectangle.isEmpty());
	  harness.runtest("Rectangle outcode", new gnu.testlet.java.awt.Rectangle.outcode());
	  harness.runtest("Rectangle setBounds", new gnu.testlet.java.awt.Rectangle.setBounds());
	  harness.runtest("Rectangle setLocation", new gnu.testlet.java.awt.Rectangle.setLocation());
	  harness.runtest("Rectangle setRect", new gnu.testlet.java.awt.Rectangle.setRect());
	  harness.runtest("Rectangle setSize", new gnu.testlet.java.awt.Rectangle.setSize());
	  
	  // Print summary
	  
	  System.out.println("Passes: " + harness.getPasses());
	  System.out.println("Failures: " + harness.getFailures());
	  RConsole.close(); 
  }

	@Override
	public void debug(Object[] o, String desc) {
	}

}

