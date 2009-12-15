package lejos.robotics.proposal;

/**
 * should  be implemented by a MoveController that will automatically update a
 * Pose in real time
 * @author nxj team
 */
public interface MoveProvider {

  
  /**
   * Adds a Pose that will be notified of all movement events.
   * @param listener the move listener (a Pose)
   */
  public void addPose(Pose listener);
 /**
  * Update the pose
  */
  public void updatePose();

}
