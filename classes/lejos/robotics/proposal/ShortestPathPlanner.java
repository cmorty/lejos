package lejos.robotics.proposal;


import java.util.*;
import lejos.geom.*;
import java.awt.geom.Point2D;
import lejos.robotics.proposal.WayPoint;
// The  library for this project sould be set to include  classes.jar from  NXJ_HOME

/**
 * This class calculates the shortest path from a starting point to a finish point.
 * while avoiding obstacles that are represented as a set of straight lines.
 * The path passes through the end points of some of these lines, which is where the
 * changes of direction occur.
 * It uses the Node inner class for its internal representatin of points.
 *
 * @author Roger
 */
public class ShortestPathPlanner
{

  /**
   * finds the shortest path between start  and finish Points whild avoiding the obstacles
   * in the map
   * @param start : the beginning of the path
   * @param finish: the destination
   * @param theMap  that contains the obstacles
   * @return an array list of waypoints.  If no path exists, returns null
   */
  public ArrayList<WayPoint> findPath(Point start, Point finish, ArrayList<Line> theMap)
  {
    _map = theMap;
    initialize(); // in case this method has already been called before
    Node source = new Node(start);
    Node destination = new Node(finish);
    source.setSourceDistance(0);
    _reached.add(source);
    _candidate.add(destination);
    Node from;  // current start node
    Node dest;  // current destination node
    int index = _candidate.size()-1;  //index of current destinatin in candidate set
    boolean failed = false;

    while (_candidate.size() > 0 && !failed)
    {
      _count++;
      // get destination from in_candidate set
      dest = _candidate.get(index);
      from = getBest(dest);
      float distance = from.getDistance(dest);
      if (distance >= BIG)  // dest is blocked  from best  reached node
      {
        index--;  // try another temporary start node
        failed = index < 0; // tried the whole stack.
      } else
      { // dest  not previously blocked  from  best  reached node
//     if  now blocked, this call may add  two nodes to Candidate stack
        if (segmentBlocked(from, dest))
        {
          from.block(dest);// dest not directly reachable
          index = _candidate.size() - 1;  // start from top of stack
        } else  // not blocked  so dest node has is  reached
        {
          if (distance < .05f) // essentially same node as from
          {
            dest.setPredecessor(from.getPredecessor());
            dest.setSourceDistance(from.getSourceDistance());
          } else
          {
            dest.setPredecessor(from); // allows backtracking to recover the path
            dest.setSourceDistance(from.getSourceDistance() + from.getDistance(dest));
          }
          // move dest from _candidate to _reached
          _reached.add(dest); // add only if not in the se
          _candidate.remove(dest);  // pop the stack
          index = _candidate.size() - 1;
        } // end else  dest not blocked

      } // end else dest not previously blocked
    }// end while
    if (failed)
    {
      return null;
    }
    return getRoute(destination);
  }


  protected void initialize()
  {
    _reached = new ArrayList<Node>();
    _candidate = new ArrayList<Node>();
  }

  /**
   * helper method for findPath(). Determines if the straight line segment 
   * crosses a line on the map.
   * Side effect: nodes at the end of the blocking line are added to the in_candidate set
   * @param from the  beginning of the line segment
   * @param theDest the end of the line segment
   * @return  true if the segment is blocked
   */
  protected boolean segmentBlocked(final Node from, final Node theDest)
  {
    Node to = new Node(theDest.getLocation()); // alias the destination
    Node n1 = null; // one end of the blocking line
    Node n2 = null; // other end of the blocking line
    Line line = null; // the line conecting  from node   with to node
    Point intersection; // point wher the segment crosses the blockin gline
    boolean blocked = false;
    Line segment = new Line(from.getX(), from.getY(),
            to.getX(), to.getY());
    for (Line l : _map)// test ever line in the map to see if it blocks the segment
    {
      intersection = l.intersectsAt(segment);
      if (intersection != null && !from.atEndOfLine(l) && !to.atEndOfLine(l))
      {  //segment is legal if it starts or ends at an end point of the line
        line = l;
        blocked = true;
      }// nodes at end of the line
    }
    if (blocked)  // add end points of the blocking segment to  in_candidate set
    {
      Point2D p1 =  line.getP1();
      Point2D  p2 = line.getP2();
      n1 = new Node((float)p1.getX(),(float)p1.getY());
      if(!in_reached(n1) &&!in_candidate(n1))
      {
        n1.setSourceDistance(from.getSourceDistance() + from.getDistance(n1));
        _candidate.add(n1);
      }
       n2 = new Node((float)p2.getX(),(float)p2.getY());
       if(!in_reached(n2) && !in_candidate(n2))
      {
        n2.setSourceDistance(from.getSourceDistance() + from.getDistance(n2));
        _candidate.add(n2);
      }
    }
    return blocked;
  }

  /**
   * Helper method for findPath() <br>
   * returns the  node in  the Reached set, whose distance from the start node plus
   * its straignt line distance to the destination is the minimum.
   * @param currentDestinati0n - the current destination node, (in the Candidate set)
   * @return the node the node which could be the last node in the shortest path
   */
  protected  Node getBest(Node currentDestination)
  {
    Node best = _reached.get(0);
    float minDist = best._sourceDistance + best.getDistance(currentDestination);
    for (Node n : _reached)
    {
      float d = n._sourceDistance + n.getDistance(currentDestination);
      if (d < minDist)
      {
        minDist = d;
        best = n;
      }
    }
    return best;
  }


  /**
   * helper method for findPath; check if aNode is in the set of reached nodes
   * @param aNode
   * @return true if aNode has been reached already
   */
  protected boolean in_reached(final Node  aNode)
  {
    boolean found = false;
    for (Node n : _reached)
    {
      found = aNode.getLocation().equals(n.getLocation());
      if (found) break;
    }
    return found;
  }
  
/**
   * helper method for findPath; check if aNode is in the set of candidate nodes
   * @param aNode
   * @return true if aNode has been reached already
   */
  protected boolean in_candidate(final Node aNode)
  {
    boolean found = false;
    for (Node n : _candidate)
    {
      found = aNode.getLocation().equals(n.getLocation());
//      RConsole.println("aNode "+aNode+" n "+n);
      if (found) break;
    }
    return found;
  }

  /**
   * helper method for find path() <br>
   * calculates the route backtracking through predecessor chain
   * @param destination
   * @return the route of the shortest path
   */
protected  ArrayList<WayPoint> getRoute(Node destination)
{
    ArrayList<WayPoint> route = new ArrayList <WayPoint>();
    Node n = destination;
    WayPoint  w ;
    do {  // add waypoints to route as push down stack
      w = new WayPoint((Point)n.getLocation());
      route.add(0, w);
      n = n.getPredecessor();
    } while (n != null);
    return route;
}
  protected ArrayList<Line> getMap()
  {
   return _map;
  }
  public int getIterationCount(){ return _count;}

  public int getNodeCount(){return _reached.size();}
  protected    int _count =  0;
  /**
   * set by segmentBlocked() used by findPath()
   */
  protected boolean _blocked = false;

  private static final float BIG = 999999999;

  /**
   * the set of nodes that are candidates for being in the shortest path, but
   * whose distance from the start node is not yet known
   */
  protected ArrayList<Node> _candidate = new ArrayList<Node>();

  /**
   * the set of nodes that are candidates for being in the shortest path, and
   * whose distance from the start node is known
   */
 protected  ArrayList<Node> _reached = new ArrayList<Node>();
  /**  
   * The map of the obstacles
   */
  protected  ArrayList<Line> _map = new ArrayList<Line>();


 protected class Node
{
  public Node(Point p)
  {
    _p = p;
  }
  public Node(float x, float y)
  {
    this(new Point(x,y));
  }


/**
 * test if this Node is one of the ends of  theLine
 * @param the line  endpoints to check
 * @return true if this node is an end of the line
 */
  public boolean atEndOfLine(Line theLine)
  {
    return _p.equals(theLine.getP1()) || _p.equals(theLine.getP2());
  }
  /**
   * set the distance of this Node from the source
   * @param theDistance
   */
  public void setSourceDistance(float theDistance)
  {
    _sourceDistance = theDistance;
  }
  /**
   * return the shortest path length to this node from the start node
   * @return shortest distance
   */
  public float getSourceDistance(){return _sourceDistance;}

  /**
   * get the straight line distance from this node to aPoint
   * @param aPoint
   * @return the distance
   */
  public float getDistance(Point aPoint)
  {

    return (float)_p.distance(aPoint);
  }

  /**
   * return the straight distance from this node to aNode
   * @param n
   * @return
   */
  public float getDistance(Node aNode)
  {
    if(_blocked.indexOf(aNode) > -1) return ShortestPathPlanner.BIG;
    else return getDistance(aNode.getLocation());
  }

  /**
   * return the locataion of this node
   * @return
   */
 public Point getLocation()
 {
   return _p;
 }

 /**
  * add aNode to list of nodes not a neighbor of this Node
  * @param aNode
  */
 public void block(Node aNode)
 {
   _blocked.add(aNode);
 }

 /**
  * set the predecessor of this node in the shortest path from the start node
  * @param thePredecessor
  */
 public void setPredecessor(Node thePredecessor)
 {_predecessor = thePredecessor;}
 /**
  * get the predecessor of this node in the shortest path from the start
  * @return
  */
 public Node getPredecessor() { return _predecessor;}

 /**
  * get the X coordinate of this node
  * @return X coordinate
  */
  public float getX(){return (float)_p.getX();}
  /**
   * get the Y coordinate of thes Node
   * @return Y coordinate
   */
  public float getY(){return (float)_p.getY();}
 public String toString(){return " "+getX()+" , "+getY()+" ";}
  protected Point _p;
  protected float _sourceDistance;
  protected Node _predecessor;
  public ArrayList<Node> _blocked = new ArrayList<Node>();
 }

} 

