package lejos.robotics.proposal;

/**
 * should  be implemented by a MoveController that will automatically update a
 * Pose1 in real time
 * @author nxj team
 */
public interface MoveProvider1 {

  
  /**
   * Adds a Pose1 that will be notified of all movement events.
   * @param listener the move listener (a Pose1)
   */
  public void addPose(Pose1 listener);
 /**
  * Update the pose
  */
  public void updatePose();

}
