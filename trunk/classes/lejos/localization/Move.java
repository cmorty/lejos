package lejos.localization;

/**
 * Represents a robot move consisting of a drive forward and a turn
 * 
 * Supports generating of a random move.
 * 
 * @author Lawrie Griffiths
 * 
 */
public class Move {
  private static float maxDistance = 80f;
  public float angle, distance;

  /**
   * Create the move from an angle and a distance
   * 
   * @param angle the angle to turn
   * @param distance the distance to travel
   */
  private Move(float angle, float distance) {
    this.angle = angle;
    this.distance = distance;
  }

  /**
   * Generate a random move.
   * 
   * @return the generated move
   */
  public static Move randomMove() {
    float a = (float) Math.random() * 360;
    float d = (float) Math.random() * maxDistance;
    
    if (a > 180f) a -= 360f;
    return new Move(a, d);
  }
  
  public static void setMaxDistance(float distance) {
    maxDistance = distance;
  } 
}

