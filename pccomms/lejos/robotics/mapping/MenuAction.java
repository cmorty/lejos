package lejos.robotics.mapping;

import java.awt.Point;
import java.awt.event.ActionEvent;
import javax.swing.*;

import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;

public class MenuAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	protected NavigationModel.NavEvent navEvent;
	protected Point p;
	protected PCNavigationModel model;
	protected NavigationPanel panel;
	
	public MenuAction(NavigationModel.NavEvent navEvent, String name, Point p, PCNavigationModel model, NavigationPanel panel) {
		super(name);
		this.navEvent = navEvent;
		this.p = p;
		this.model = model;
		this.panel = panel;
	}
	public void actionPerformed(ActionEvent e) {
		switch (navEvent) {
		case SET_POSE:
			model.setPose(new Pose(p.x / panel.pixelsPerUnit,p.y / panel.pixelsPerUnit,0));
			panel.repaint();
			break;
		case GOTO:
			model.setTarget(new Waypoint(p.x / panel.pixelsPerUnit,p.y / panel.pixelsPerUnit));
			model.goTo(new Waypoint(p.x /panel.pixelsPerUnit,p.y / panel.pixelsPerUnit));
			break;
		case FIND_CLOSEST:
			model.findClosest(p.x / panel.pixelsPerUnit,p.y / panel.pixelsPerUnit);
			break;
		case ADD_WAYPOINT:
			model.addWaypoint(new Waypoint(p.x / panel.pixelsPerUnit,p.y / panel.pixelsPerUnit));
			break;
		case SET_TARGET:
			model.setTarget(new Waypoint(p.x / panel.pixelsPerUnit,p.y / panel.pixelsPerUnit));
			break;
		case FOLLOW_ROUTE:
			break;
		}
	}
}
