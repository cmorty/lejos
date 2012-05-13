package org.lejos.ros.nodes;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.ros.message.MessageListener;
import geometry_msgs.PoseStamped;
import nav_msgs.Odometry;
import nxt_lejos_msgs.Battery;
import nxt_lejos_msgs.Compass;
import nxt_lejos_msgs.Decibels;
import nxt_msgs.Color;
import nxt_msgs.Contact;
import nxt_msgs.Gyro;
import sensor_msgs.Imu;
import sensor_msgs.LaserScan;
import sensor_msgs.Range;
import tf.tfMessage;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
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
	private tfMessage tf;
	private Odometry od;
	private PoseStamped pose;
	private Imu imu;
	private LaserScan laser;

	@Override
	public void onStart(ConnectedNode node) {
		
		tf = node.getTopicMessageFactory().newFromType(tfMessage._TYPE);
		od = node.getTopicMessageFactory().newFromType(Odometry._TYPE);
		pose = node.getTopicMessageFactory().newFromType(PoseStamped._TYPE);
		imu = node.getTopicMessageFactory().newFromType(Imu._TYPE);
		laser = node.getTopicMessageFactory().newFromType(LaserScan._TYPE);
		
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
            			return (float) quatToHeading(imu.getOrientation().getZ(), imu.getOrientation().getW());
            		case 9:
            			return (float) imu.getAngularVelocity().getZ();           			
            		case 10:
            			return od.getTwist().getTwist().getLinear().getX();
            		case 11:
            			return od.getTwist().getTwist().getAngular().getZ();
            		case 12:
            			return "x: " + (float) pose.getPose().getPosition().getX() + "," + " y: " + 
            		                   (float) pose.getPose().getPosition().getY() + "," +
            				   " h: " + (float) Math.toDegrees(quatToHeading(pose.getPose().getOrientation().getZ(), 
            						                                         pose.getPose().getOrientation().getW()));
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
	    		batteryVoltage = (float) message.getVoltage();
	    		dataModel.fireTableDataChanged();
	    	}
	    });
        
		//Subscription to ultrasonic topic
        Subscriber<Range> subscriberSonic =
	        node.newSubscriber("ultrasonic_sensor", "sensor_msgs/Range");
        subscriberSonic.addMessageListener(new MessageListener<Range>() {
	    	@Override
	    	public void onNewMessage(Range message) {   		
	    		range = (float) message.getRange();
	    		dataModel.fireTableDataChanged();
	    	}
	    });
        
		//Subscription to touch topic
        Subscriber<Contact> subscriberTouch =
	        node.newSubscriber("touch_sensor", "nxt_msgs/Contact");
        subscriberTouch.addMessageListener(new MessageListener<Contact>() {
	    	@Override
	    	public void onNewMessage(Contact message) {   		
	    		contact = message.getContact();
	    		dataModel.fireTableDataChanged();
	    	}
	    });
        
		//Subscription to gyro topic
        Subscriber<Gyro> subscriberGyro =
	        node.newSubscriber("gyro_sensor", "nxt_msgs/Gyro");
        subscriberGyro.addMessageListener(new MessageListener<Gyro>() {
	    	@Override
	    	public void onNewMessage(Gyro message) {   		
	    		angularVelocity = (float) message.getAngularVelocity().getZ();
	    		dataModel.fireTableDataChanged();
	    	}
	    });
        
		//Subscription to compass topic
        Subscriber<Compass> subscriberCompass =
	        node.newSubscriber("compass_sensor", "nxt_lejos_msgs/Compass");
        subscriberCompass.addMessageListener(new MessageListener<Compass>() {
	    	@Override
	    	public void onNewMessage(Compass message) {   		
	    		heading = message.getHeading();
	    		dataModel.fireTableDataChanged();
	    	}
	    });
        
		//Subscription to sound topic
        Subscriber<Decibels> subscriberSound =
	        node.newSubscriber("sound_sensor", "nxt_lejos_msgs/Decibels");
        subscriberSound.addMessageListener(new MessageListener<Decibels>() {
	    	@Override
	    	public void onNewMessage(Decibels message) {   		
	    		decibels = message.getDecibels();
	    		dataModel.fireTableDataChanged();
	    	}
	    });
        
		//Subscription to light topic
        Subscriber<Color> subscriberLight =
	        node.newSubscriber("light_sensor", "nxt_msgs/Color");
        subscriberLight.addMessageListener(new MessageListener<Color>() {
	    	@Override
	    	public void onNewMessage(Color message) {   		
	    		intensity = (float) message.getIntensity();
	    		dataModel.fireTableDataChanged();
	    	}
	    });
        
		//Subscription to color topic
        Subscriber<Color> subscriberColor =
	        node.newSubscriber("color_sensor", "nxt_msgs/Color");
        subscriberColor.addMessageListener(new MessageListener<Color>() {
	    	@Override
	    	public void onNewMessage(Color message) {   		
	    		intensity = (float) message.getIntensity();
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

	@Override
	public void onError(Node arg0, Throwable arg1) {
		// Do nothing	
	}
}
