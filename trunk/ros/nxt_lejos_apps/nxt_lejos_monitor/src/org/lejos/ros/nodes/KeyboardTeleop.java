package org.lejos.ros.nodes;

import geometry_msgs.Twist;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;

public class KeyboardTeleop extends JFrame implements NodeMain, KeyListener {
	private static final long serialVersionUID = 1L;
	private Twist twist;
	private Publisher<Twist> twistTopic;
	private String twistMessageType = "geometry_msgs/Twist";
	double angular = 0, linear = 0;
	int key = 0;

	@Override
	public void onStart(ConnectedNode node) {
		
		twist = node.getTopicMessageFactory().newFromType(Twist._TYPE);
		twistTopic = node.newPublisher("cmd_vel", twistMessageType);
        
        JLabel msg = new JLabel("Press arrow keys to move the robot");
        this.getContentPane().add(msg, BorderLayout.WEST);
        JTextField dummy = new JTextField(0);
        this.getContentPane().add(dummy, BorderLayout.EAST);
        dummy.addKeyListener(this);
        
        setTitle("leJOS Keyboard teleop");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
	}

	@Override
	public GraphName getDefaultNodeName() {
		return new GraphName("nxt_lejos_apps/nxt_lejos_teleop");
	}
	

	@Override
	public void onShutdown(Node node) {
		// Do nothing
	}

	@Override
	public void onShutdownComplete(Node node) {
		// Do nothing
	}

	@Override
	public void onError(Node arg0, Throwable arg1) {
		// Do nothing	
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (key == e.getKeyCode()) return; // Ignore the same key
		key = e.getKeyCode();
		
		switch(key) {
		case KeyEvent.VK_UP:
			linear = 0.5;
			break;
		case KeyEvent.VK_DOWN:
			linear = -0.5;
			break;
		case KeyEvent.VK_LEFT:
			angular = 2;
			break;
		case KeyEvent.VK_RIGHT:
			angular = -2;
			break;
		} 
		
		twist.getAngular().setZ(angular);;
		twist.getLinear().setX(linear);
		twistTopic.publish(twist);	
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		angular = 0;
		linear = 0;
		twist.getAngular().setZ(angular);
		twist.getLinear().setX(linear);
		twistTopic.publish(twist);
		key = 0;
	}

	@Override
	public void keyTyped(KeyEvent e) {	
	}
}
