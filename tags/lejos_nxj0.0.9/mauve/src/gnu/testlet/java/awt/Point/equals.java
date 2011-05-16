// Tags: JDK1.1

// Copyright (C) 2004 David Gilbert <david.gilbert@object-refinery.com>

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
// Boston, MA 02111-1307, USA.  */

package gnu.testlet.java.awt.Point;

import gnu.testlet.TestHarness;
import gnu.testlet.Testlet;

import java.awt.Point;
import java.awt.geom.Point2D;

/**
 * Some checks for the equals() method in the {@link Point} class.
 */
public class equals implements Testlet {

  /**
   * Runs the test using the specified harness.
   * 
   * @param harness  the test harness (<code>null</code> not permitted).
   */
  public void test(TestHarness harness)      
  {
    Point p1 = new Point();
    Point p2 = new Point();
    harness.check(p1.equals(p2));
    
    p1 = new Point(1, 0);
    harness.check(!p1.equals(p2));
    p2 = new Point(1, 0);
    harness.check(p1.equals(p2));
    
    p1 = new Point(1, 2);
    harness.check(!p1.equals(p2));
    p2 = new Point(1, 2);
    harness.check(p1.equals(p2));
    
    harness.check(!p1.equals(null));
    
    Point2D p3 = new Point2D.Double(1.0, 2.0);
    harness.check(p3.equals(p1));
  }

}
