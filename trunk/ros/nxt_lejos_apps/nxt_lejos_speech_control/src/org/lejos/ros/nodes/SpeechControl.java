package org.lejos.ros.nodes;

import lejos.util.Delay;

import org.ros.message.MessageListener;
import org.ros.message.nxt_lejos_msgs.DNSCommand;
import org.ros.message.sensor_msgs.Range;
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
	
  private Publisher<DNSCommand> topic = null;
  private String messageType = "nxt_lejos_msgs/DNSCommand";
  private DNSCommand dns = new DNSCommand();
  private boolean stopped =false;

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
			if (msg.range < 0.5 && !stopped) {
				stopped = true;;
				dns.type = "stop"; 
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
}


