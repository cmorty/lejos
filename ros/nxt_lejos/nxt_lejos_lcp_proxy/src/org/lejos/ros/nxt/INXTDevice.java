package org.lejos.ros.nxt;

import org.ros.node.Node;

public interface INXTDevice {

	public void publishTopic(Node node);
	public void updateTopic(Node node, long seq);
}
