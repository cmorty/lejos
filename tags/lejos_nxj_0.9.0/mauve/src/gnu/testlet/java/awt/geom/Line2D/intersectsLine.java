//Tags: JDK1.2

//Copyright (C) 2004 David Gilbert (david.gilbert@object-refinery.com)

//This file is part of Mauve.

//Mauve is free software; you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation; either version 2, or (at your option)
//any later version.

//Mauve is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.

//You should have received a copy of the GNU General Public License
//along with Mauve; see the file COPYING.  If not, write to
//the Free Software Foundation, 59 Temple Place - Suite 330,
//Boston, MA 02111-1307, USA.  */

package gnu.testlet.java.awt.geom.Line2D;

import gnu.testlet.TestHarness;
import gnu.testlet.Testlet;

import java.awt.geom.Line2D;

/**
* Checks whether Line2D.intersectsLine() works correctly.
*/
public class intersectsLine
  implements Testlet
{
  public void test(TestHarness harness)
  {
    Line2D line1 = new Line2D.Double(0.0, 0.0, 1.0, 0.0);
    harness.check(line1.intersectsLine(0.0, 0.0, 1.0, 0.0));
    harness.check(line1.intersectsLine(0.0, 0.0, 1.0, 1.0));
    harness.check(line1.intersectsLine(1.0, 1.0, 1.0, 0.0));
    harness.check(line1.intersectsLine(0.5, 0.5, 0.5, -0.5));
    harness.check(!line1.intersectsLine(0.0, 1.0, 1.0, 1.0));

    harness.check(line1.intersectsLine(new Line2D.Double(0.0, 0.0, 1.0, 0.0)));
    harness.check(line1.intersectsLine(new Line2D.Double(0.0, 0.0, 1.0, 1.0)));
    harness.check(line1.intersectsLine(new Line2D.Double(1.0, 1.0, 1.0, 0.0)));
    harness.check(line1.intersectsLine(new Line2D.Double(0.5, 0.5, 0.5, -0.5)));
    harness.check(!line1.intersectsLine(new Line2D.Double(0.0, 1.0, 1.0, 1.0)));

    boolean pass = false;
    try {
      line1.intersectsLine(null);
    }
    catch (NullPointerException e) {
      pass = true;
    }
    harness.check(pass);  
  }
}