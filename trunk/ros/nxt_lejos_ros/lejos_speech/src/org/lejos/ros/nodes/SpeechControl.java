package org.lejos.ros.nodes;

import org.ros.message.MessageListener;
import org.ros.message.nxt_lejos_ros_msgs.DNSCommand;
import org.ros.namespace.GraphName;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

/**
 * 
 * @author Lawrie Griffiths
 */
public class SpeechControl implements NodeMain {
	
  private Publisher<org.ros.message.nxt_lejos_ros_msgs.DNSCommand> topic = null;
  private String messageType = "nxt_lejos_ros_msgs/DNSCommand";
  private DNSCommand dns = new DNSCommand();

  @Override
  public GraphName getDefaultNodeName() {
    return new GraphName("lejos_speech/SpeechControl");
  }

  @Override
  public void onStart(Node node) {
	  
	topic = node.newPublisher("dns_command", messageType);
    
    Subscriber<org.ros.message.std_msgs.String> subscriber =
        node.newSubscriber("/recognizer/output", "std_msgs/String");
    subscriber.addMessageListener(new MessageListener<org.ros.message.std_msgs.String>() {
      @Override
      public void onNewMessage(org.ros.message.std_msgs.String message) {
    	  String cmd = message.data;
    	  System.out.println("Received: " + cmd);
    	  if (cmd.equals("left")) dns.type = "rotateLeft";
    	  else if (cmd.equals("right")) dns.type = "rotateRight";
    	  else if (cmd.equals("forward")) dns.type = "forward";
    	  else if (cmd.equals("backward")) dns.type = "backward";
    	  else if (cmd.equals("stop")) dns.type = "stop";
    	  dns.value = 0;
    	  topic.publish(dns);
      }
    });
  }

  @Override
  public void onShutdown(Node node) {
  }

  @Override
  public void onShutdownComplete(Node node) {
  }
}


