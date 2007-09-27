package org.lejos.nxt.ldt.builder;

import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class leJOSBuilder extends IncrementalProjectBuilder {

	class SampleDeltaVisitor implements IResourceDeltaVisitor {
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
		 */
		public boolean visit(IResourceDelta delta) throws CoreException {
			IResource resource = delta.getResource();
			switch (delta.getKind()) {
			case IResourceDelta.ADDED:
				// handle added resource
				checkXML(resource);
				break;
			case IResourceDelta.REMOVED:
				// handle removed resource
				break;
			case IResourceDelta.CHANGED:
				// handle changed resource
				checkXML(resource);
				break;
			}
			//return true to continue visiting children.
			return true;
		}
	}

	class SampleResourceVisitor implements IResourceVisitor {
		public boolean visit(IResource resource) {
			checkXML(resource);
			//return true to continue visiting children.
			return true;
		}
	}

	class XMLErrorHandler extends DefaultHandler {
		
		private IFile file;

		public XMLErrorHandler(IFile file) {
			this.file = file;
		}

		private void addMarker(SAXParseException e, int severity) {
			leJOSBuilder.this.addMarker(file, e.getMessage(), e
					.getLineNumber(), severity);
		}

		public void error(SAXParseException exception) throws SAXException {
			addMarker(exception, IMarker.SEVERITY_ERROR);
		}

		public void fatalError(SAXParseException exception) throws SAXException {
			addMarker(exception, IMarker.SEVERITY_ERROR);
		}

		public void warning(SAXParseException exception) throws SAXException {
			addMarker(exception, IMarker.SEVERITY_WARNING);
		}
	}

	public static final String BUILDER_ID = "org.lejos.nxt.ldt.leJOSBuilder";

	private static final String MARKER_TYPE = "org.lejos.nxt.ldt.xmlProblem";

	private SAXParserFactory parserFactory;

	private void addMarker(IFile file, String message, int lineNumber,
			int severity) {
		try {
			IMarker marker = file.createMarker(MARKER_TYPE);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
			if (lineNumber == -1) {
				lineNumber = 1;
			}
			marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
		} catch (CoreException e) {
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.internal.events.InternalBuilder#build(int,
	 *      java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException {
		if (kind == FULL_BUILD) {
			fullBuild(monitor);
		} else {
			IResourceDelta delta = getDelta(getProject());
			if (delta == null) {
				fullBuild(monitor);
			} else {
				incrementalBuild(delta, monitor);
			}
		}
		return null;
	}

	void checkXML(IResource resource) {
		if (resource instanceof IFile && resource.getName().endsWith(".xml")) {
			IFile file = (IFile) resource;
			deleteMarkers(file);
			XMLErrorHandler reporter = new XMLErrorHandler(file);
			try {
				getParser().parse(file.getContents(), reporter);
			} catch (Exception e1) {
			}
		}
	}

	private void deleteMarkers(IFile file) {
		try {
			file.deleteMarkers(MARKER_TYPE, false, IResource.DEPTH_ZERO);
		} catch (CoreException ce) {
		}
	}

	protected void fullBuild(final IProgressMonitor monitor)
			throws CoreException {
		try {
			getProject().accept(new SampleResourceVisitor());
		} catch (CoreException e) {
		}
	}

	private SAXParser getParser() throws ParserConfigurationException,
			SAXException {
		if (parserFactory == null) {
			parserFactory = SAXParserFactory.newInstance();
		}
		return parserFactory.newSAXParser();
	}

	protected void incrementalBuild(IResourceDelta delta,
			IProgressMonitor monitor) throws CoreException {
		// the visitor does the work.
		delta.accept(new SampleDeltaVisitor());
	}
}

//package org.lejos.tools.eclipse.plugin.builders;
//2 
//3 import java.util.HashMap;
//4 import java.util.Map;
//5 
//6 import org.eclipse.core.resources.IMarker;
//7 import org.eclipse.core.resources.IProject;
//8 import org.eclipse.core.resources.IResource;
//9 import org.eclipse.core.resources.IncrementalProjectBuilder;
//10 import org.eclipse.core.runtime.CoreException;
//11 import org.eclipse.core.runtime.IProgressMonitor;
//12 import org.eclipse.jdt.core.ICompilationUnit;
//13 import org.eclipse.jdt.core.IJavaElement;
//14 import org.eclipse.jdt.core.IJavaModelMarker;
//15 import org.eclipse.jdt.core.IJavaProject;
//16 import org.eclipse.jdt.core.JavaCore;
//17 import org.eclipse.jdt.core.JavaModelException;
//18 import org.lejos.tools.api.ToolsetException;
//19 import org.lejos.tools.eclipse.plugin.EclipseProgressMonitorToolsetImpl;
//20 import org.lejos.tools.eclipse.plugin.EclipseToolsetFacade;
//21 import org.lejos.tools.eclipse.plugin.EclipseUtilities;
//22 import org.lejos.tools.eclipse.plugin.LejosPlugin;
//23 
//24 /**
//25  * This is the leJOS builder, which is responsible for transparent linking of
//26  * all leJOS RCX main programs.
//27  *
//28  * @author <a href="mailto:jochen.hiller@t-online.de">JOchen Hiller </a>
//29  *
//30  */
//31 public class LejosBuilder extends IncrementalProjectBuilder
//32 {
//33    // overriden methods of incremental project builder
//34 
//35    /*
//36     * (non-Javadoc)
//37     *
//38     * @see org.eclipse.core.resources.IncrementalProjectBuilder#build(int,
//39     *      java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
//40     */
//41    protected IProject[] build (int kind, Map args, IProgressMonitor monitor)
//42       throws CoreException
//43    {
//44       LejosPlugin.debug("Builder has been running");
//45 
//46       if (hasBuildErrors())
//47       {
//48          return null;
//49       }
//50 
//51       clearLinkMarkers(getProject());
//52       linkAll(monitor);
//53 
//54       // TODO jhi make build later a delta based builder,
//55       // so only link the modified main classes
//56       // Object o = getDelta(getProject());
//57       return null;
//58    }
//59 
//60    // private methods
//61 
//62    /**
//63     * Link all compilation units with a main method.
//64     *
//65     * @param monitor the progress monitor to use
//66     */
//67    private void linkAll (IProgressMonitor monitor)
//68    {
//69       IProject p = getProject();
//70       IJavaProject jp = JavaCore.create(p);
//71       try
//72       {
//73          IJavaElement[] elems = jp.getChildren();
//74          for (int i = 0; i < elems.length; i++)
//75          {
//76             IJavaElement elem = elems[i];
//77             if (elem.getElementType() == IJavaElement.PACKAGE_FRAGMENT_ROOT)
//78             {
//79                // System.out.println(elem);
//80                ICompilationUnit[] cus = EclipseUtilities
//81                   .collectLinkClasses(elem);
//82                monitor.beginTask("Linking leJOS", cus.length);
//83                for (int j = 0; j < cus.length; j++)
//84                {
//85                   ICompilationUnit cu = cus[j];
//86                   if (EclipseUtilities.hasMain(cu))
//87                   {
//88                      EclipseToolsetFacade facade = new EclipseToolsetFacade();
//89                      facade
//90                         .setProgressMonitor(new EclipseProgressMonitorToolsetImpl(
//91                            monitor));
//92                      String cuName = String.valueOf(cu.getPath());
//93                      // remove leading slash
//94                      cuName = cuName.substring(1);
//95                      monitor.subTask("Linking " + cuName);
//96                      monitor.worked(j);
//97                      try
//98                      {
//99                         deleteMarkers(getProject(), cu);
//100                         facade
//101                            .linkJavaElement(cu, LejosPlugin.getPreferences());
//102                      }
//103                      catch (ToolsetException ex)
//104                      {
//105                         createMarker(getProject(), cu, ex);
//106                      }
//107                   }
//108                }
//109                monitor.done();
//110             }
//111          }
//112       }
//113       catch (JavaModelException ex)
//114       {
//115          // TODO Auto-generated catch block
//116          ex.printStackTrace();
//117       }
//118    }
//119 
//120    /**
//121     * Checks for any severy build errors
//122     *
//123     * @return true, if there are severy build erros
//124     * @throws CoreException will be raised if markers could not be read
//125     */
//126    private boolean hasBuildErrors () throws CoreException
//127    {
//128       IMarker[] markers = getProject().findMarkers(
//129          IJavaModelMarker.JAVA_MODEL_PROBLEM_MARKER, false,
//130          IResource.DEPTH_INFINITE);
//131       for (int i = 0; i < markers.length; i++)
//132       {
//133          IMarker marker = markers[i];
//134          if (marker.getAttribute(IMarker.SEVERITY, 0) == IMarker.SEVERITY_ERROR)
//135          {
//136             return true;
//137          }
//138       }
//139       return false;
//140    }
//141 
//142    /**
//143     * Delete all linker markers (before linking).
//144     */
//145    private void deleteMarkers (IProject project, ICompilationUnit cu)
//146    {
//147       try
//148       {
//149          IResource resource = cu.getUnderlyingResource();
//150          resource.deleteMarkers(LejosPlugin.LEJOS_MARKER_LINKER, false, IResource.DEPTH_INFINITE);
//151       }
//152       catch (CoreException ex)
//153       {
//154          // TODO Auto-generated catch block
//155          ex.printStackTrace();
//156       }
//157    }
//158 
//159    /**
//160     * Create a linker marker for a given exception (linking failed).
//161     */
//162    private void createMarker (IProject project, ICompilationUnit cu,
//163       Exception anException)
//164    {
//165       try
//166       {
//167          IResource resource = cu.getUnderlyingResource();
//168          IMarker newMarker = resource
//169             .createMarker(LejosPlugin.LEJOS_MARKER_LINKER);
//170          Map map = new HashMap();
//171          map.put(IMarker.SEVERITY, new Integer(IMarker.SEVERITY_ERROR));
//172          String message = "Link failed due to exception: ";
//173          message = message + anException.getMessage();
//174          map.put(IMarker.MESSAGE, message);
//175          newMarker.setAttributes(map);
//176       }
//177       catch (CoreException ex)
//178       {
//179          // TODO Auto-generated catch block
//180          ex.printStackTrace();
//181       }
//182    }
//183 
//184    private void clearLinkMarkers (IProject project) throws CoreException
//185    {
//186       project.getProject().deleteMarkers(LejosPlugin.LEJOS_MARKER_LINKER,
//187          false, IResource.DEPTH_INFINITE);
//188    }
//189 }
