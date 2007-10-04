package org.lejos.nxt.ldt.util;

import java.io.File;
import java.util.Iterator;

import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.lejos.nxt.ldt.LeJOSNXJPlugin;
import org.lejos.nxt.ldt.builder.leJOSNature;

/**
 * utility methods for the plugin
 * @author Matthias Paul Scholz
 *
 */
public class LeJOSNXJUtil {

	public static IJavaProject getJavaProjectFromSelection(ISelection selection) {
		IJavaProject project = null;
		if (selection instanceof IStructuredSelection) {
			for (Iterator it = ((IStructuredSelection) selection).iterator(); it
					.hasNext();) {
				Object element = it.next();
				if (element instanceof IJavaProject) {
					project = (IJavaProject) element;
				} else if (element instanceof IJavaElement) {
					project = ((IJavaElement) element).getJavaProject();
				}
			}
		}
		return project;
	}

	public static IJavaElement getFirstJavaElementFromSelection(
			ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			for (Iterator it = ((IStructuredSelection) selection).iterator(); it
					.hasNext();) {
				Object element = it.next();
				if (element instanceof IJavaElement) {
					return (IJavaElement) element;
				}
			}
		}
		return null;
	}

	public static boolean isLeJOSProject(IJavaProject project)
			throws CoreException {
		// loop over natures
		IProjectDescription description = project.getProject().getDescription();
		String[] natures = description.getNatureIds();
		for (int i = 0; i < natures.length; i++) {
			if (natures[i].equals(leJOSNature.NATURE_ID))
				return true;
		}
		return false;
	}

	public static void message(String message) {
		// log to leJOS NXJ console
		MessageConsole console = LeJOSNXJPlugin.getDefault().getLeJOSNXJConsole();
		console.newMessageStream().println(message);
		// System.out.println("leJOS NXJ> " + message);
	}

	public static void message(Throwable throwable) {
		// log to error log
		LeJOSNXJPlugin.getDefault().log(throwable);
		// log to leJOS NXJ console
		MessageConsole console = LeJOSNXJPlugin.getDefault().getLeJOSNXJConsole();
		console.newMessageStream().println("Error: " + throwable.getMessage());
	}

	public static String getClassNameFromJavaFile(String fileName) {
		if (fileName == null)
			return null;
		// get position of suffix
		int indexOfSuffix = fileName.lastIndexOf('.');
		if (indexOfSuffix >= 0) {
			return fileName.substring(0, indexOfSuffix);
		} else {
			return fileName;
		}
	}

	public static File getAbsoluteProjectTargetDir(IJavaProject project)
			throws JavaModelException {
		IPath outputDirWithProject = project.getOutputLocation();
		IPath locationOfProject = project.getProject().getLocation();
		IPath outputDir = outputDirWithProject.removeFirstSegments(1);
		IPath fullPath = locationOfProject.append(outputDir);
		return fullPath.makeAbsolute().toFile();
	}

}
