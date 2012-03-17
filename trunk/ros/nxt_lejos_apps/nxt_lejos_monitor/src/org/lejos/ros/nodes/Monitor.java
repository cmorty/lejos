package org.lejos.ros.nodes;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.ros.message.MessageListener;
import org.ros.message.geometry_msgs.PoseStamped;
import org.ros.message.nav_msgs.Odometry;
import org.ros.message.nxt_lejos_msgs.Battery;
import org.ros.message.nxt_lejos_msgs.Compass;
import org.ros.message.nxt_lejos_msgs.Decibels;
import org.ros.message.nxt_msgs.Color;
import org.ros.message.nxt_msgs.Contact;
import org.ros.message.nxt_msgs.Gyro;
import org.ros.message.sensor_msgs.Imu;
import org.ros.message.sensor_msgs.LaserScan;
import org.ros.message.sensor_msgs.Range;
import org.ros.message.tf.tfMessage;
import org.ros.namespace.GraphName;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Subscriber;

public class Monitor extends JFrame implements NodeMain {
	private static final long serialVersionUID = 1L;
	private float batteryVoltage;
	private float range;
	private float heading;
	private float angularVelocity;
	private float decibels;
	private float intensity;
	private boolean contact;
	private tfMessage tf = new tfMessage();
	private Odometry od = new Odometry();
	private PoseStamped pose = new PoseStamped();
	private Imu imu = new Imu();
	private LaserScan laser = new LaserScan();

	@Override
	public void onStart(Node node) {
		
		// Table model data
        final String[] labels = {
        		"Battery  voltage:", "Ultrasonic sensor:", "Compass sensor:", "Gyro sensor:",
        		"Sound sensor:", "Touch sensor:","Light sensor:", "Color sensor:",
        		"Imu heading:", "Imu angular velocity:", "Linear velocity", "Angular velocity", "Pose:"
        		};
        
        final DefaultTableModel dataModel = new DefaultTableModel() {
			public int getColumnCount() { return 2; }
            public int getRowCount() { return 13;}
            public Object getValueAt(int row, int col) { 
            	if (col == 0) return labels[row]; 
            	if (col ==1) {
            		switch (row) {
            		case 0:
            			return batteryVoltage;
            		case 1:
            			return range;
            		case 2:
            			return heading;
            		case 3:
            			return angularVelocity;
            		case 4:
            			return decibels;
            		case 5:
            			return contact;
            		case 6:
            			return intensity;
            		case 7:
            			return intensity;
            		case 8:
            			return (float) quatToHeading(imu.orientation.z, imu.orientation.w);
            		case 9:
            			return (float) imu.angular_velocity.z;           			
            		case 10:
            			return od.twist.twist.linear.x;
            		case 11:
            			return od.twist.twist.angular.z;
            		case 12:
            			return "x: " + (float) pose.pose.position.x + "," + " y: " + (float) pose.pose.position.y + "," +
            				   " h: " + (float) Math.toDegrees(quatToHeading(pose.pose.orientation.z, pose.pose.orientation.w));
            		}
            	}
            	return "";
            }
        };
        
        // Create a table for the display values
        final JTable table = new JTable(dataModel);
        
        // Add the table and set the frame visible
        this.getContentPane().add(table);
        setTitle("leJOS ROS Monitor");
        table.setPreferredSize(new Dimension(800,220));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
		
		//Subscription to battery topic
        Subscriber<Battery> subscriberBattery =
	        node.newSubscriber("battery", "nxt_lejos_msgs/Battery");
        subscriberBattery.addMessageListener(new MessageListener<Battery>() {
	    	@Override
	    	public void onNewMessage(Battery message) {   		
	    		batteryVoltage = (float) message.voltage;
	    		dataModel.fireTableDataChanged();
	    	}
	    });
        
		//Subscription to ultrasonic topic
        Subscriber<Range> subscriberSonic =
	        node.newSubscriber("ultrasonic_sensor", "sensor_msgs/Range");
        subscriberSonic.addMessageListener(new MessageListener<Range>() {
	    	@Override
	    	public void onNewMessage(Range message) {   		
	    		range = (float) message.range;
	    		dataModel.fireTableDataChanged();
	    	}
	    });
        
		//Subscription to touch topic
        Subscriber<Contact> subscriberTouch =
	        node.newSubscriber("touch_sensor", "nxt_msgs/Contact");
        subscriberTouch.addMessageListener(new MessageListener<Contact>() {
	    	@Override
	    	public void onNewMessage(Contact message) {   		
	    		contact = message.contact;
	    		dataModel.fireTableDataChanged();
	    	}
	    });
        
		//Subscription to gyro topic
        Subscriber<Gyro> subscriberGyro =
	        node.newSubscriber("gyro_sensor", "nxt_msgs/Gyro");
        subscriberGyro.addMessageListener(new MessageListener<Gyro>() {
	    	@Override
	    	public void onNewMessage(Gyro message) {   		
	    		angularVelocity = (float) message.angular_velocity.z;
	    		dataModel.fireTableDataChanged();
	    	}
	    });
        
		//Subscription to compass topic
        Subscriber<Compass> subscriberCompass =
	        node.newSubscriber("compass_sensor", "nxt_lejos_msgs/Compass");
        subscriberCompass.addMessageListener(new MessageListener<Compass>() {
	    	@Override
	    	public void onNewMessage(Compass message) {   		
	    		heading = message.heading;
	    		dataModel.fireTableDataChanged();
	    	}
	    });
        
		//Subscription to sound topic
        Subscriber<Decibels> subscriberSound =
	        node.newSubscriber("sound_sensor", "nxt_lejos_msgs/Decibels");
        subscriberSound.addMessageListener(new MessageListener<Decibels>() {
	    	@Override
	    	public void onNewMessage(Decibels message) {   		
	    		decibels = message.decibels;
	    		dataModel.fireTableDataChanged();
	    	}
	    });
        
		//Subscription to light topic
        Subscriber<Color> subscriberLight =
	        node.newSubscriber("light_sensor", "nxt_msgs/Color");
        subscriberLight.addMessageListener(new MessageListener<Color>() {
	    	@Override
	    	public void onNewMessage(Color message) {   		
	    		intensity = (float) message.intensity;
	    		dataModel.fireTableDataChanged();
	    	}
	    });
        
		//Subscription to color topic
        Subscriber<Color> subscriberColor =
	        node.newSubscriber("color_sensor", "nxt_msgs/Color");
        subscriberColor.addMessageListener(new MessageListener<Color>() {
	    	@Override
	    	public void onNewMessage(Color message) {   		
	    		intensity = (float) message.intensity;
	    		dataModel.fireTableDataChanged();
	    	}
	    });
        
		//Subscription to laser topic
        Subscriber<LaserScan> subscriberLaser =
	        node.newSubscriber("scan", "sensor_msgs/LaserScan");
        subscriberLaser.addMessageListener(new MessageListener<LaserScan>() {
	    	@Override
	    	public void onNewMessage(LaserScan message) { 
	    		dataModel.fireTableDataChanged();
	    	}
	    });
        
		//Subscription to imu topic
        Subscriber<Imu> subscriberImu =
	        node.newSubscriber("imu", "sensor_msgs/Imu");
        subscriberImu.addMessageListener(new MessageListener<Imu>() {
	    	@Override
	    	public void onNewMessage(Imu message) { 
	    		dataModel.fireTableDataChanged();
	    	}
	    });
        
		//Subscription to tf topic
        Subscriber<tfMessage> subscriberTf =
	        node.newSubscriber("tf", "tf/tfMessage");
        subscriberTf.addMessageListener(new MessageListener<tfMessage>() {
	    	@Override
	    	public void onNewMessage(tfMessage message) {
	    		tf = message;
	    		dataModel.fireTableDataChanged();
	    	}
	    });
        
		//Subscription to odom topic
        Subscriber<Odometry> subscriberOdom =
	        node.newSubscriber("odom", "nav_msgs/Odometry");
        subscriberOdom.addMessageListener(new MessageListener<Odometry>() {
	    	@Override
	    	public void onNewMessage(Odometry message) {
	    		od = message;
	    		dataModel.fireTableDataChanged();
	    	}
	    });
        
		//Subscription to pose topic
        Subscriber<PoseStamped> subscriberPose =
	        node.newSubscriber("pose", "geometry_msgs/PoseStamped");
        subscriberPose.addMessageListener(new MessageListener<PoseStamped>() {
	    	@Override
	    	public void onNewMessage(PoseStamped message) {
	    		pose = message;
	    		dataModel.fireTableDataChanged();
	    	}
	    });
	}

	@Override
	public GraphName getDefaultNodeName() {
		return new GraphName("nxt_lejos_apps/nxt_lejos_monitor");
	}
	

	@Override
	public void onShutdown(Node node) {
		// Do nothing
	}

	@Override
	public void onShutdownComplete(Node node) {
		// Do nothing
	}
	
	private double quatToHeading(double z, double w) {
		return 2 * Math.atan2(z, w);
	}

}
