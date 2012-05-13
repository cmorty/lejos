package org.lejos.ros.nodes;

import lejos.util.Delay;

import org.ros.message.MessageListener;
import nxt_lejos_msgs.DNSCommand;
import sensor_msgs.Range;

import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

/**
 * 
 * @author Lawrie Griffiths
 */
public class SpeechControl implements NodeMain {
	
  private Publisher<DNSCommand> topic = null;
  private String messageType = "nxt_lejos_msgs/DNSCommand";
  private DNSCommand dns;
  private boolean stopped =false;

  @Override
  public GraphName getDefaultNodeName() {
    return new GraphName("lejos_speech/SpeechControl");
  }

  @Override
  public void onStart(ConnectedNode node) {
	  
	dns = node.getTopicMessageFactory().newFromType(DNSCommand._TYPE);
	topic = node.newPublisher("dns_command", messageType);
    
    Subscriber<std_msgs.String> subscriber =
        node.newSubscriber("/recognizer/output", "std_msgs/String");
    subscriber.addMessageListener(new MessageListener<std_msgs.String>() {
      @Override
      public void onNewMessage(std_msgs.String message) {
    	  String cmd = message.getData();
    	  System.out.println("Received: " + cmd);
    	  if (cmd.equals("left")) dns.setType("rotateLeft");
    	  else if (cmd.equals("right")) dns.setType("rotateRight");
    	  else if (cmd.equals("forward")) dns.setType("forward");
    	  else if (cmd.equals("backward")) dns.setType("backward");
    	  else if (cmd.equals("stop")) dns.setType("stop");
    	  dns.setValue(0);
    	  topic.publish(dns);
    	  Delay.msDelay(1000);
    	  stopped = false;
      }
    });
    
	//Subscription to ultrasonic_sensor topic
    Subscriber<Range> subscriberRange =
        node.newSubscriber("ultrasonic_sensor", "sensor_msgs/Range");
    subscriberRange.addMessageListener(new MessageListener<Range>() {
    	@Override
    	public void onNewMessage(Range msg) {
			if (msg.getRange() < 0.5 && !stopped) {
				stopped = true;;
				dns.setType("stop"); 
				topic.publish(dns);
			}		
    	}
    });
  }

  @Override
  public void onShutdown(Node node) {
  }

  @Override
  public void onShutdownComplete(Node node) {
  }

  @Override
  public void onError(Node arg0, Throwable arg1) {
	// Do nothing
  }

}


