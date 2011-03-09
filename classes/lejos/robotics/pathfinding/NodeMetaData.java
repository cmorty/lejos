package lejos.robotics.pathfinding;

// TODO: This should be a class level/inner class kind of invisible thing with Astar? Might get repeat use from D* Lite, or different algorithm totally?
public class NodeMetaData {
	
	private float g_score;
	private float h_score;
	private Node predecessor = null;
	
	public float getF_Score() {
		return g_score + h_score; // TODO: optimize speed by calculating only once?
	}
	
	public float getG_Score() {
		return g_score;
	}
	
	public void setG_Score(float score) {
		g_score = score;
	}
	
	public void setH_Score(float score) {
		h_score = score;
	} 
	
	public Node getPredecessor(){
		return this.predecessor;
	}
	
	public void setPredecessor(Node predecessor){
		this.predecessor = predecessor;
	}
	
}
