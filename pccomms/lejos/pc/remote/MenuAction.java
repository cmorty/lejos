package lejos.pc.remote;

import java.awt.Point;
import java.awt.event.ActionEvent;

import javax.swing.*;

import lejos.robotics.NavigationModel;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.WayPoint;

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
		if (navEvent == NavigationModel.NavEvent.SET_POSE) {
			model.currentPose = new Pose(p.x / panel.pixelsPerUnit,p.y / panel.pixelsPerUnit,0);
			panel.repaint();
		} else if (navEvent == NavigationModel.NavEvent.GOTO) {
			model.goTo(new WayPoint(p.x,p.y));
		}
	}
}
