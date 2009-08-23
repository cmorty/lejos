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
import gnu.testlet.java.awt.geom.Line2D.*;

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
	  System.setOut(new PrintStream(RConsole.openOutputStream()));
	  NXJTestHarness harness = new NXJTestHarness();
	  
	  // Run the tests
	  
	  harness.runtest("Line2D clone", new clone());
	  harness.runtest("Line2D contains", new contains());
	  harness.runtest("Line2D equals", new equals());
	  harness.runtest("Line2D getBounds", new getBounds());
	  harness.runtest("Line2D intersects", new intersects());
	  
	  // Print summary
	  
	  System.out.println("Passes: " + harness.getPasses());
	  System.out.println("Failures: " + harness.getFailures());
	  RConsole.close();
	  
  }


	@Override
	public void debug(Object[] o, String desc) {
		// TODO Auto-generated method stub
		
	}

}

