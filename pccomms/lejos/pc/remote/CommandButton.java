package lejos.pc.remote;

import javax.swing.JButton;

public class CommandButton extends JButton {
	private String name;
	private Command command;
	
	public CommandButton(String name, Command command) {
		this.name = name;
		this.command = command;
	}
	
	public String getName() {
		return name;
	}
	
	public Command getCommand() {
		return command;
	}

}
