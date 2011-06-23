package lejos.pc.remote;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;

import javax.swing.*;

import lejos.robotics.NavigationModel;

public class MenuAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	NavigationModel.NavEvent navEvent;
	Point p;
	
	public MenuAction(NavigationModel.NavEvent navEvent, String name, Point p) {
		super(name);
		this.navEvent = navEvent;
		this.p = p;
	}
	public void actionPerformed(ActionEvent e) {
		if (navEvent == NavigationModel.NavEvent.SET_POSE) {
			
		}
	}
}
