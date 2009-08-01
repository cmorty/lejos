package lejos.robotics.proposal;

public interface PilotListenerGenerator {
  /**
   * Adds a PilotListener that will be notified of all Pilot movement events.
   * @param p
   */
  public void addPilotListener(PilotListener listener);
}
