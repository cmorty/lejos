package lejos.robotics.pathfinding;

import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.DestinationUnreachableException;
import lejos.robotics.navigation.Waypoint;
import lejos.robotics.navigation.WaypointListener;
import java.util.*;
import lejos.geom.*;
import lejos.robotics.navigation.Pose;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * This class calculates the shortest path from a starting point to a finish point.
 * while avoiding obstacles that are represented as a set of straight lines.
 * The path passes through the end points of some of these lines, which is where the
 * changes of direction occur.  Since the robot is not point, the lines representing
 * the obstacles should be lengthened so the actual robot will miss the actual obstacles.
 * Use the lengthenLines() method to do this.
 * Uses modification of the A* algorithm,which is a  a variant of the
 * Dijkstra shortest path algorithm.  This variant adds nodes needed.
 * It uses the Node inner class for its internal representation of points.
 *
 * @author Roger Glassey
 */
public class ShortestPathFinder implements PathFinder
{

  public ShortestPathFinder(LineMap map)
  {
    setMap(map);
  }
  
  /**
   * Finds the shortest path from start to finish using the map (or collection of lines)
   * in the constructor.
   * @param start  the initial robot pose
   * @param finish the final robot location
   * @return the shortest route
   * @throws DestinationUnreachableException  if, for example, you nave not called setMap();
   */
  public Path findRoute(Pose start, Waypoint finish) throws DestinationUnreachableException
  {
    return findPath(start.getLocation(), finish, _map);
  }

  /**
   * Finds the shortest path from start to finish using the map ( collection of lines)
   * in the constructor.
   * @param start  the initial robot pose
   * @param finish the final robot location
   * @param theMap  the LineMap of obstacles
   * @return the shortest route
   * @throws DestinationUnreachableException  if, for example, you nave not called setMap();
   */
  public Path findRoute(Pose start, Waypoint finish, LineMap theMap) throws DestinationUnreachableException
  {
    setMap(theMap);
    return findPath(start.getLocation(), finish, _map);
  }
  
  /**
   * Finds the shortest path between start  and finish Points while
   * avoiding the obstacles represented by lines in the map
   * @param start : the beginning of the path
   * @param finish : the destination
   * @param theMap  that contains the obstacles
   * @return an array list of waypoints.  If no path exists, returns null and throws
   * an exception.
   */
  private Path findPath(Point start, Point finish, ArrayList<Line>
          theMap)throws DestinationUnreachableException
  {
    _map = theMap;
    initialize(); // in case this method has already been called before
    Node source = new Node(start);
    _destination = new Node(finish);  // current destination
    if(_debug) System.out.println(" Start "+source +" Destination "+_destination);
    source.setSourceDistance(0);
    _reached.add(source);
    _candidate.add(_destination);
    Node from;  // current start node; in _reached;
    Node dest;  // current destination node
    _index = 0;//index of current destination in candidate list
    /* This list is kept in order of increasing straight line distance to the
     * destination. If a new node is reached, the index is reset to 0.
    */
    boolean failed = false;
  // The real work is here:
    while (! _reached.contains(_destination) && !failed)
    {
      _count++;
      // get temporary destination from candidate list
      dest = _candidate.get(_index);
       if(_debug) System.out.println("dest " +dest+" index "+_index);
      from = getBest(dest);  //best predecessor in _reached set
      float distance = from.getDistance(dest);// straight line distance
           if(_debug) System.out.println(" best possible node in reached  "+from +" distance "+distance);
      if (distance >= BIG)  // dest is known to be blocked from best node in  _reached
      {
          if(_debug) System.out.println("dest already blocked  ");
        _index++; // try another temporary destination node, next farther from destination
        failed = _index == _candidate.size(); // tried all candidates
        if(failed) 
        {
        	if(dest != _destination)
        	{
        		dest.unreachable();
        		_candidate.remove(dest);
        		if(_debug) System.out.println("UNREACHABLE "+dest);
        		failed = false;
        		_index = 0;
        	}
        	else 
        		{
        		System.out.println("Destinatin unreachable");
        		throw new DestinationUnreachableException();       		
        		}
        }
      } else  // is temp dest reachable from the best reached node?
      {
        if (segmentBlocked(from, dest)) //line between from and dest intersects a map line
        { // this method call may have  created  and added new nodes (line ends) to the _candidate list
          from.block(dest);// Record in from node that  dest is not  directly reachable

        } else  // not blocked  so dest node has  been reacheds- add it to set
        {
          if (distance < .05f) // essentially same node as best node in _reached,
          { // so will not be a separate way point in the route
            dest.setPredecessor(from.getPredecessor());
            dest.setSourceDistance(from.getSourceDistance());
          } else
          {
            dest.setPredecessor(from); // allows backtracking to recover the path
            dest.setSourceDistance(from.getSourceDistance() + from.getDistance(dest));
          }
          // move dest from _candidate list  to _reached
          _reached.add(dest);
          _candidate.remove(dest);
          _index = 0;  // start over with a new node in reached set.
          if(_debug) System.out.println("Moved from candidate to reached "+dest);
        } // end else  dest not blocked  snf id now in _reached
      } // end else dest not previously blocked
    }// end while

    System.out.println("DONE");

    return getRoute(_destination);
  }

  /**
   * Helper method for findPath(). Determines if the straight line segment 
   * crosses a line on the map.
   * Side effect: creates nodes at the end of the blocking line and adds them to the _candidate set
   * @param from the  beginning of the line segment
   * @param theDest the end of the line segment
   * @return  true if the segment is blocked
   */
  private boolean segmentBlocked(final Node from, final Node theDest)
  {
    Node to = new Node(theDest.getLocation()); // alias the destination
    Node n1 = null; // one end of the blocking line
    Node n2 = null; // other end of the blocking line
    Line line = null; // the line connecting  from node   with to node
    Point intersection; // point where the segment crosses the blocking line
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
    if (blocked)  // add end points of the blocking segment to  inCandidateSet set
    {
     if(_debug) System.out.println("  blocked from " + from + " to " + theDest);
      Point p1 =  line.getP1();
      Point  p2 = line.getP2();
      n1 = new Node((float)p1.getX(),(float)p1.getY());
      if(!inReachedSet(n1) &&!inCandidateSet(n1)  && n1.isReachable())
      {
        n1.setSourceDistance(from.getSourceDistance() + from.getDistance(n1));
//        _candidate.add(n1);
        int i =  addToCandidate(n1);
        _index = (_index < 0 ) ? _index : i ;
//        System.out.println("Candidate add "+n1.toString()+" Source Distance "
//                +n1.getSourceDistance());
      }
       n2 = new Node((float)p2.getX(),(float)p2.getY());
       if(!inReachedSet(n2) && !inCandidateSet(n2) && n2.isReachable())
      {
        n2.setSourceDistance(from.getSourceDistance() + from.getDistance(n2));
//        _candidate.add(n2);
        addToCandidate(n2);
//         System.out.println("Candidate add "+n2+" Source Distance "
//                +n2.getSourceDistance());
      }
    }
    return blocked;
  }
  
  /**
   * keep candidate list sorted in order on increasing distance to destination
   * return index of Node n in _candidate
   * @param n
   *
   */
  private int addToCandidate (Node n)
    {
      float distance = n.getDistance(_destination);
      int indx = -1;
      for (Node c : _candidate)
      {
          if ( distance  < c.getDistance(_destination))
          {
               indx = _candidate.indexOf(c);
              _candidate.add(indx,n);
              break;
          }
      }
       if(indx == -1) _candidate.add(n);
       if(indx > 0 && _index > indx) _index = indx;
     if(_debug) System.out.println(n+" added to candidate index " + _candidate.indexOf(n)
              + " destination distance "+distance);
      return _candidate.indexOf(n);

  }
  
  /**
   * Helper method for findPath() <br>
   * returns the  node in  the Reached set, whose distance from the start node plus
   * its straight line distance to the destination is the minimum.
   * @param currentDestination : the current destination node, (in the Candidate set)
   * @return the node the node which could be the last node in the shortest path
   */
  private  Node getBest(Node currentDestination)
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
  private boolean inReachedSet(final Node  aNode)
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
  private boolean inCandidateSet(final Node aNode)
  {
    boolean found = false;
    for (Node n : _candidate)
    {
      found = aNode.getLocation().equals(n.getLocation());
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
  private  Path getRoute(Node destination)
  {
    Path route = new Path();
    Node n = destination;
    Waypoint  w ;
    do {  // add waypoints to route as push down stack
      w = new Waypoint(n.getLocation());
      route.add(0, w);
      n = n.getPredecessor();
    } while (n != null);
    return route;
  }
  
  public void setMap(ArrayList<Line> theMap)
  {
    _map = theMap;
  }

  public void setMap(LineMap theMap)
  {
    Line [] lines = theMap.getLines();
    for(int i = 0; i < lines.length; i++)
    _map.add(lines[i]);
  }

  public void setDebug(boolean yes )
  { _debug = yes; }

  /**
   * lengthens all the lines in the map by delta at each end
   * @param delta   added to each end of each  line
   */
  public void lengthenLines( float delta)
  {
    for (Line line : _map)
    {
   	  line.lengthen(delta);
    }    
  }
  
  private void initialize()
  {
    _reached = new ArrayList<Node>();
    _candidate = new ArrayList<Node>();
  }
  
  public ArrayList<Line> getMap()
  {
   return _map;
  }
  
  public int getIterationCount(){ return _count;}

  public int getNodeCount(){return _reached.size();}

  public void addListener(WaypointListener wpl) {
    if(listeners == null )listeners = new ArrayList<WaypointListener>();
    listeners.add(wpl);
  }
  
  public void startPathFinding(Pose start, Waypoint end) {
	  Path solution = null;
	  try {
		  solution = findPath(start.getLocation(), end, _map);
	  } catch (DestinationUnreachableException e) {
		  // TODO Not sure how to handle this.
		  return;
	  }
	  if(listeners != null) { 
		  for(WaypointListener l : listeners) {
			  Iterator<Waypoint> iterator = solution.iterator(); 
			  while(iterator.hasNext()) {
				  l.addWaypoint(iterator.next());
			  }
			  l.pathGenerated();
		  }
	  }
  }

  //***********  instance variables in ShortestPathFinder *******************
  private ArrayList<WaypointListener> listeners;
  
  private int _count =  0;

  /**
   * Set by segmentBlocked() used by findPath()
   */
  private boolean _blocked = false;

  private int  _index; // location of current destination in _candidate

  private static final float BIG = 999999999;

  private Node _destination;
  
  /**
   * the set of nodes that are candidates for being in the shortest path, but
   * whose distance from the start node is not yet known
   * stored as a list, in increasing order of straight list distance to destination
   */
  private ArrayList<Node> _candidate = new ArrayList<Node>();

  /**
   * the set of nodes that are candidates for being in the shortest path, and
   * whose distance from the start node is known
   */
  private  ArrayList<Node> _reached = new ArrayList<Node>();
  /**  
   * The map of the obstacles
   */
  private  ArrayList<Line> _map = new ArrayList<Line>();

  private boolean _debug = false;

//************Begin definition of Node class  **********************
 private  class Node
{
  public Node(Point p)
  {
    _p = p;
  }
  
  private Node(float x, float y)
  {
    this(new Point(x,y));
  }

  /**
   * test if this Node is one of the ends of  theLine
   * @param theLine  endpoints to check
   * @return true if this node is an end of the line
   */
  private  boolean atEndOfLine(Line theLine)
  {
    return _p.equals(theLine.getP1()) || _p.equals(theLine.getP2());
  }
  
  /**
   * Set the distance of this Node from the source
   * @param theDistance
   */
  private void setSourceDistance(float theDistance)
  {
    _sourceDistance = theDistance;
  }
  
  /**
   * Return the shortest path length to this node from the start node
   * @return shortest distance
   */
 private  float getSourceDistance(){return _sourceDistance;}

  /**
   * Get the straight line distance from this node to aPoint
   * @param aPoint
   * @return the distance
   */
  private  float getDistance(Point aPoint)
  {
    return (float)_p.distance(aPoint);
  }

  /**
   * Return the straight line distance from this node to aNode
   * or a big number if the straight line is known to be blocked
   * @param aNode
   * @return the distance
   */
  private float getDistance(Node aNode)
  {
    if(_blocked.indexOf(aNode) > -1) return BIG;
    if(!aNode.isReachable()) return BIG;
    return getDistance(aNode.getLocation());
  }

  /**
   * return the location of this node
   * @return the location
   */
  private Point getLocation()
  {
    return _p;
  }

 /**
  * add aNode to list of nodes not a neighbour of this Node
  * @param aNode
  */
 private  void block(Node aNode)
 {
   _blocked.add(aNode);
 }

 /**
  * set the predecessor of this node in the shortest path from the start node
  * @param thePredecessor
  */
 private void setPredecessor(Node thePredecessor)
 {_predecessor = thePredecessor;}
 
 /**
  * get the predecessor of this node in the shortest path from the start
  * @return the predecessor node
  */
 private  Node getPredecessor() { return _predecessor;}

 /**
  * get the X coordinate of this node
  * @return X coordinate
  */
  private  float getX(){return (float)_p.getX();}
  /**
   * get the Y coordinate of the Node
   * @return Y coordinate
   */
  private float getY(){return (float)_p.getY();}
  
  public void unreachable(){ _reachable = false;}
  public boolean isReachable(){return _reachable;}
  
  @Override
  public  String toString(){return " "+getX()+" , "+getY()+" ";}
  
  private  Point _p;
  private float _sourceDistance;
  private Node _predecessor;
  private boolean _reachable = true;
  
  public ArrayList<Node> _blocked = new ArrayList<Node>();
 }
// ****************   end Node class ****************************

} 

