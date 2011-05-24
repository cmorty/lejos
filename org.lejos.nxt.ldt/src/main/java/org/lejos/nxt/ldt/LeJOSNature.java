package org.lejos.nxt.ldt;

import java.util.ArrayList;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

/**
 *
 * leJOS NXJ project nature
 * @author Matthias Paul Scholz
 * 
 */
public class LeJOSNature implements IProjectNature {

	/**
	 * ID of this project nature
	 */
	public static final String ID = "org.lejos.nxt.ldt.leJOSNature";

	private IProject project;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IProjectNature#configure()
	 */
	public void configure() throws CoreException {
		ArrayList<ICommand> newCommands = new ArrayList<ICommand>();
		
		IProjectDescription desc = project.getDescription();
		ICommand[] commands = desc.getBuildSpec();
		for (ICommand c : commands) {
			if (!LeJOSBuilder.ID.equals(c.getBuilderName())) {
				newCommands.add(c);
			}
		}

		ICommand command = desc.newCommand();
		command.setBuilderName(LeJOSBuilder.ID);
		newCommands.add(command);
		
		commands = new ICommand[newCommands.size()];
		newCommands.toArray(commands);
		desc.setBuildSpec(commands);
		project.setDescription(desc, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IProjectNature#deconfigure()
	 */
	public void deconfigure() throws CoreException {
		ArrayList<ICommand> newCommands = new ArrayList<ICommand>();
		
		IProjectDescription desc = project.getDescription();
		ICommand[] commands = desc.getBuildSpec();
		for (ICommand c : commands) {
			if (!LeJOSBuilder.ID.equals(c.getBuilderName())) {
				newCommands.add(c);
			}
		}

		commands = new ICommand[newCommands.size()];
		newCommands.toArray(commands);
		desc.setBuildSpec(commands);
		project.setDescription(desc, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IProjectNature#getProject()
	 */
	public IProject getProject() {
		return project;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IProjectNature#setProject(org.eclipse.core.resources.IProject)
	 */
	public void setProject(IProject project) {
		this.project = project;
	}

}
